package rolat.repository.serviceImpl;

import com.sgcc.sgd5000.constants.ETaskItem;
import com.sgcc.sgd5000.hisdata.HisViewAcq;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;
import com.sgcc.sgd5000.meas.ReadingTimeTag;
import net.njcp.service.util.CommFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rolat.repository.dao.HisViewAcqDao;
import rolat.repository.dao.TimeTagDao;
import rolat.repository.service.IHisViewAcqService;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Service("hisViewAcqService")
public class HisViewAcqServiceImpl implements IHisViewAcqService {

    private Logger log= LoggerFactory.getLogger(HisViewAcqServiceImpl.class);

    @Resource
    private HisViewAcqDao hisViewAcqDao;
    @Resource
    private TimeTagDao timeTagDao;

    @Override
    public List<HisViewAcq> getHisViewAcqList(Timestamp startTime, Timestamp endTime, Meters meters) {
        List<HisViewAcq> listHisViewAcq=new ArrayList<HisViewAcq>();
        try{
            int startYear = CommFunc.getTableYearBy(new Date(startTime.getTime()));
            int endYear = CommFunc.getTableYearBy(new Date(endTime.getTime()));
            if (startYear == endYear) {
                String tableName = CommFunc.getHisTableName(meters.getAcquiredId(), startYear, ETaskItem.VIEW);
                return hisViewAcqDao.getHisViewAcqList(startTime,endTime,meters,tableName);
            }else{
                String startTableName =  CommFunc.getHisTableName(meters.getAcquiredId(), startYear, ETaskItem.VIEW);
                listHisViewAcq.addAll(hisViewAcqDao.getHisViewAcqList(startTime,endTime,meters,startTableName));
                String endTableName =  CommFunc.getHisTableName(meters.getAcquiredId(), endYear, ETaskItem.VIEW);
                listHisViewAcq.addAll(hisViewAcqDao.getHisViewAcqList(startTime,endTime,meters,endTableName));
            }
        }catch (Exception e){
            log.error("接口出错",e);
        }
        return listHisViewAcq;
    }

    @Override
    public List<HisViewAcq> getHisViewAcqListByMeters(Meters meters) {
        MeterTimeTag meterTimeTag=timeTagDao.getMeterTimeTag(meters);
        ReadingTimeTag readingTimeTag=timeTagDao.getReadingTimeTag(meters);
        Timestamp startTime=meterTimeTag.getClass1TimeTag();

        return null;
    }

    @Override
    @Transactional
    public int updateHisViewAcqList(List<HisViewAcq> hisViewAcqList,boolean updateTime,Meters meters,MeterTimeTag meterTimeTag) {
        int result=0;
        Set<Integer> tableNameSet=new HashSet<Integer>();
        for (HisViewAcq hisViewAcq : hisViewAcqList) {
            int year = CommFunc.getTableYearBy(new Date(hisViewAcq.getId().getOccurTime().getTime()));
            tableNameSet.add(year);
        }
        for (Integer year : tableNameSet) {
            String tableName = CommFunc.getHisTableName(meters.getAcquiredId(), year, ETaskItem.VIEW);
            result+=hisViewAcqDao.updateHisViewAcqList(hisViewAcqList,tableName);
        }
        if(updateTime){
            timeTagDao.updateMeterTimeTag(meterTimeTag);
        }

      return result;
    }



}
