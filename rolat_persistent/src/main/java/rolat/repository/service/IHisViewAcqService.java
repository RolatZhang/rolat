package rolat.repository.service;

import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;

import java.sql.Timestamp;
import java.util.List;

/**
 * 分时底码相关
 */
public interface IHisViewAcqService {

    List<HisViewAcq> getHisViewAcqList(Timestamp startTime,Timestamp endTime,Meters meters);
    List<HisViewAcq> getHisViewAcqListByMeters(Meters meters);

    int updateHisViewAcqList(List<HisViewAcq> hisViewAcqList,boolean updateTime,Meters meters,MeterTimeTag meterTimeTag);
}
