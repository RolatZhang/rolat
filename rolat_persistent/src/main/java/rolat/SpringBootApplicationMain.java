package rolat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import rolat.entity.Menu;
import rolat.entity.SysRole;
import rolat.repository.MenuRepository;
import rolat.repository.SysRoleRepository;
import rolat.repository.specification.MyRepositoryFactoryBean;

import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass=MyRepositoryFactoryBean.class)
@ServletComponentScan //配置druid必须加的注解，如果不加，访问页面打不开，filter和servlet、listener之类的需要单独进行注册才能使用，spring boot里面提供了该注解起到注册作用
public class SpringBootApplicationMain {

    public static void main(String[] args) {
        Long startTime=System.currentTimeMillis();
        Log log=LogFactory.getLog(SpringBootApplicationMain.class);
//        SpringApplication.run(SpringBootApplicationMain.class, args);

        SpringApplication app=new SpringApplication(SpringBootApplicationMain.class);

        ApplicationContext context=app.run(args);
//        System.out.println(context.getBean("stdSchedulerFactory").toString());

       /* context.publishEvent(new RolatEvent(RolatEvent.class, "hhhhh"));
        context.publishEvent(new RolatEvent(RolatEvent.class, "aaaa"));*/
        Long endTime=System.currentTimeMillis();
        MenuRepository sysRoleRepository= (MenuRepository) context.getBean("menuRepository");
        log.info("启动服务成功，耗时："+((endTime-startTime)/1000)+" S");
        SpringBootApplicationMain SpringBootApplicationMain=new SpringBootApplicationMain();
        SpringBootApplicationMain.testTTT(sysRoleRepository);
    }

    public void testTTT(MenuRepository sysRoleRepository){
        List<Menu> aa=sysRoleRepository.findAll();
        for (Menu sysRole :aa
                ) {
            System.out.println(sysRole.getName());
        }
    }


}