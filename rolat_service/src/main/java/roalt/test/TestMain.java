/*
package roalt.test;

import com.dlptech.swap.tools.SpringUtils;
import com.sgcc.sgd5000.meas.Meters;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import rolat.repository.service.IMetersService;

import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "./applicationContext.xml");
        SpringUtils.installApplicationContext(context);
        IMetersService metersService= (IMetersService) context.getBean("metersService");
        List<Meters> metersList=metersService.getMetersList();
        for (Meters meters : metersList) {
            System.out.println(meters.getName());
        }
        System.out.println("结束");

    }
}
*/
