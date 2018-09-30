package rolat.repository.service;

import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.MeterTimeTag;
import com.sgcc.sgd5000.meas.Meters;
import com.sgcc.sgd5000.meas.ReadingTimeTag;

import java.util.List;

/**
 * 电表相关业务接口
 */
public interface IMetersService {

    List<Meters> getMetersList();

    List<ReadingTimeTag> getReadingTimeTagList();

    List<MeterTimeTag> getMeterTimeTagList();

    MeterConfigItem getMeterConfigItem(Meters meter, Integer dataItem);

}
