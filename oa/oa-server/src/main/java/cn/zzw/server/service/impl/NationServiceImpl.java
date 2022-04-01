package cn.zzw.server.service.impl;

import cn.zzw.server.mapper.NationMapper;
import cn.zzw.server.pojo.Nation;
import cn.zzw.server.service.INationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzw
 * @since 2022-01-17
 */
@Service
public class NationServiceImpl extends ServiceImpl<NationMapper, Nation> implements INationService {

}
