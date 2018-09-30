package rolat.repository.dao;

import com.sgcc.sgd5000.meas.Meter;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;
import com.sgcc.sgd5000.meas.ReadingTimeTag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TimeTagDao extends BaseDao {

    public MeterTimeTag getMeterTimeTag(Meters meters){
        String hql="from MeterTimeTag where meterId=?";
        List<MeterTimeTag> meterTimeTagList= (List<MeterTimeTag>) dao.executeHQLQuery(hql,meters.getMeterId());
        if(meterTimeTagList.size()>0)
            return meterTimeTagList.get(0);
        return null;
    }

    public ReadingTimeTag getReadingTimeTag(Meters meters){
        String hql="from ReadingTimeTag where meterId=?";
        List<ReadingTimeTag> readingTimeTagList= (List<ReadingTimeTag>) dao.executeHQLQuery(hql,meters.getMeterId());
        if(readingTimeTagList.size()>0)
            return readingTimeTagList.get(0);
        return null;
    }
    public List<ReadingTimeTag> getReadingTimeTagList(){
        String hql="from ReadingTimeTag";
        return (List<ReadingTimeTag>) dao.executeHQLQuery(hql);
    }

    public List<MeterTimeTag> getMeterTimeTagList() {
        String hql="from MeterTimeTag";
        return (List<MeterTimeTag>) dao.executeHQLQuery(hql);
    }


    public int updateMeterTimeTag(MeterTimeTag meterTimeTag){
         dao.update(meterTimeTag);
         return 1;
    }
}
