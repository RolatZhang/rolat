package rolat.repository.dao;

import com.dlptech.swap.DaoSupportService;
import com.dlptech.swap.tools.SpringUtils;
import com.sgcc.sgd5000.meas.MeterConfigItem;
import com.sgcc.sgd5000.meas.Meters;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MetersDao extends BaseDao {

    public List<Meters> getMetersList(){
        String hql="from Meters";
        return (List<Meters>) dao.executeHQLQuery(hql);
    }
    public MeterConfigItem getMeterConfigItem(Meters meter,Integer dataItem){
        String hql = "from MeterConfigItem where configId = ? and dataItem = ?";
        Object[] params = new Object[] { meter.getMeterConfigId(), dataItem };
        List<MeterConfigItem> resultList = (List<MeterConfigItem>) dao.executeHQLQuery(hql, params);
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("./applicationContext.xml");

        context.setValidating(false);
        SpringUtils.installApplicationContext(context);
        DaoSupportService dao= SpringUtils.getDaoService();
        String hql = "from MeterConfigItem";

        List<MeterConfigItem> resultList = (List<MeterConfigItem>) dao.executeHQLQuery(hql);
                


    }
}
