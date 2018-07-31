package rolat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
//@ServletComponentScan 配置druid必须加的注解，如果不加，访问页面打不开，filter和servlet、listener之类的需要单独进行注册才能使用，spring boot里面提供了该注解起到注册作用
public class SpringBootApplicationMain implements CommandLineRunner {
    private Log log=LogFactory.getLog(SpringBootApplicationMain.class);

    @Autowired
    private MenuRepository menuRepository;

    public static void main(String[] args) {


        SpringApplication app=new SpringApplication(SpringBootApplicationMain.class);

        ApplicationContext context=app.run(args);


    }


    @Override
    public void run(String... strings) throws Exception {
        log.info("服务启动成功");
        testTTT(menuRepository);
        System.out.println("阻塞主线程");
        Thread.currentThread().join();
    }

    public void testTTT(MenuRepository sysRoleRepository){
        List<Menu> aa=sysRoleRepository.findAll();
        for (Menu sysRole :aa
                ) {
            System.out.println(sysRole.getName());
        }


    }
}