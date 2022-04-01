package cn.zzw.server.controller;


import cn.zzw.server.pojo.Joblevel;
import cn.zzw.server.pojo.RespBean;
import cn.zzw.server.service.IJoblevelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/system/basic/joblevel")
public class JoblevelController {

    @Resource
    private IJoblevelService joblevelService;

    @ApiOperation(value = "获取所有职业等级信息")
    @GetMapping("/joblevelList")
    public List<Joblevel> getJoblevel(){
        return joblevelService.list();
    }

    @ApiOperation(value = "新增职业等级信息")
    @PostMapping("/")
    public RespBean addJoblevel(@RequestBody Joblevel joblevel){
        joblevel.setCreateDate(LocalDateTime.now());
        if(joblevelService.save(joblevel)){
            return RespBean.success("保存职业等级信息成功！");
        }
        return RespBean.error("保存职业等级信息失败！");
    }

    @ApiOperation(value = "更新职业等级信息")
    @PutMapping("/")
    public RespBean updateJoblevel(@RequestBody Joblevel joblevel){
        if(joblevelService.updateById(joblevel)){
            return RespBean.success("更新职业等级信息成功！");
        }
        return RespBean.error("更新职业等级信息失败！");
    }

    @ApiOperation(value = "删除职业等级信息")
    @DeleteMapping("/{id}")
    public RespBean deleteJoblevel(@PathVariable Integer id){
        if (joblevelService.removeById(id)){
            return RespBean.success("删除职业等级信息成功！");
        }
        return RespBean.error("删除职业等级信息失败！");
    }

    @ApiOperation(value = "批量删除职业等级信息")
    @DeleteMapping("/")
    public RespBean deleteJoblevelByIds(Integer[]ids){
        if(joblevelService.removeByIds(Arrays.asList(ids))){
            return RespBean.success("批量删除职业等级信息成功！");
        }
        return RespBean.error("批量删除职业等级信息失败！");
    }

}
