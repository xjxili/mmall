import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.util.MD5Util;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Test1 {

    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserMapper mapper = (UserMapper) context.getBean("userMapper");
        User user = mapper.selectLogin("admin", MD5Util.MD5EncodeUtf8("9"));
        System.out.println(user);
    }
    @Test
    public void test1(){
        //Mybatis默认查询策略是List不会为空
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserMapper mapper = (UserMapper) context.getBean("userMapper");
        List list = new ArrayList();
        for (Object o : list) {
            System.out.println(o);
        }
    }


}
