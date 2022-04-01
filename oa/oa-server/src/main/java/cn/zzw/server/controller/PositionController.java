package cn.zzw.server.controller;


import cn.zzw.server.pojo.Position;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IPositionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>  职位控制器
 *
 * @author zzw
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/system/basic/pos")
public class PositionController {

    @Resource
    private IPositionService positionService;

    @ApiOperation(value = "获取所有职位信息")
    @GetMapping("/")
    public List<Position> getAllPosition(){
        return positionService.list();
    }

    @ApiOperation(value = "新增职位信息")
    @PostMapping("/")
    public RespBean addPosition(@RequestBody Position position){
        position.setCreateDate(LocalDateTime.now());
        if(positionService.save(position)){
            return RespBean.success("新增职位信息成功！");
        }
        return RespBean.error("新增职位信息失败！");
    }

    @ApiOperation(value = "更新职位信息")
    @PutMapping("/")
    public RespBean updatePosition(@RequestBody Position position){
       if(positionService.updateById(position)){
            return RespBean.success("更新职位信息成功！");
       }
       return RespBean.error("更新职位信息失败！");
    }

    @ApiOperation(value = "删除职位信息")
    @DeleteMapping("/{id}")
    public RespBean deletePosition(@PathVariable Integer id){
        if(positionService.removeById(id)){
            return RespBean.success("删除职位信息成功！");
        }
        return RespBean.error("删除职位信息失败！");
    }

    @ApiOperation(value = "批量删除职位信息")
    @DeleteMapping("/")
    public RespBean deletePositionsByIds(Integer[]ids){
        if(positionService.removeByIds(Arrays.asList(ids))){
            return RespBean.success("删除职位信息成功！");
        }
        return RespBean.error("删除职位信息失败！");
    }


}
