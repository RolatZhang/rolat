package rolat.repository.service;

import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;

import java.sql.Timestamp;
import java.util.List;

/**
 * 原trans处理类
 * 原始值转一次值
 * 底码转增量
 */
public interface ITransService {

    List<HisViewAcq> processHisViewAcq(List<HisViewAcq> hisViewAcqList,Double precision);

    int processHisProfileAcq(List<HisViewAcq> hisViewAcqList,Meters meters, MeterConfigItem meterConfigItem, boolean updateTime, MeterTimeTag meterTimeTag);
}
