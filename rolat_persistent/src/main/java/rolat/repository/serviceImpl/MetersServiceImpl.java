package rolat.repository.serviceImpl;

import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;
import com.sgcc.sgd5000.meas.ReadingTimeTag;
import org.springframework.stereotype.Service;
import rolat.repository.dao.MetersDao;
import rolat.repository.dao.TimeTagDao;
import rolat.repository.service.IMetersService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 电表相关业务接口
 */
@Service("metersService")
public class MetersServiceImpl implements IMetersService {

    @Resource
    private MetersDao metersDao;

    @Resource
    private TimeTagDao timeTagDao;

    public List<Meters> getMetersList() {
        return metersDao.getMetersList();
    }

    @Override
    public List<ReadingTimeTag> getReadingTimeTagList() {
        return timeTagDao.getReadingTimeTagList();
    }

    @Override
    public List<MeterTimeTag> getMeterTimeTagList() {
        return timeTagDao.getMeterTimeTagList();
    }

    @Override
    public MeterConfigItem getMeterConfigItem(Meters meter, Integer dataItem) {
        return metersDao.getMeterConfigItem(meter,dataItem);
    }
}
