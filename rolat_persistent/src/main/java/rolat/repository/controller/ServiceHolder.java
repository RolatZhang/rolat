package rolat.repository.controller;

import com.sgcc.sgd5000.constants.EMeterDataItem;
import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.Meters;
import org.springframework.stereotype.Component;
import rolat.repository.service.IHisViewAcqService;
import rolat.repository.service.IMetersService;
import rolat.repository.service.ITransService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Component("serviceHolder")
public class ServiceHolder {

    @Resource
    public IMetersService metersService;
    @Resource
    public IHisViewAcqService hisViewAcqService;
    @Resource
    public ITransService transService;


    public int processTransTask(Timestamp startTime, Timestamp endTime, Meters meters,boolean updateTime){
        List<HisViewAcq> hisViewAcqList= hisViewAcqService.getHisViewAcqList(startTime,endTime,meters);
        MeterConfigItem meterConfigItemView=metersService.getMeterConfigItem(meters, EMeterDataItem.TOU.getValue());
        List<HisViewAcq> hisViewAcqListResult=transService.processHisViewAcq(hisViewAcqList,meterConfigItemView.getPrecision());
        hisViewAcqService.updateHisViewAcqList(hisViewAcqListResult,meters);
        MeterConfigItem meterConfigItemPROFILE=metersService.getMeterConfigItem(meters, EMeterDataItem.PROFILE.getValue());
        return 1;
    }

}
