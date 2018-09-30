package rolat.repository.controller;

import org.springframework.stereotype.Component;
import rolat.repository.service.IHisViewAcqService;
import rolat.repository.service.IMetersService;

import javax.annotation.Resource;

@Component("serviceHolder")
public class ServiceHolder {

    @Resource
    public IMetersService metersService;
    @Resource
    public IHisViewAcqService hisViewAcqService;

}
