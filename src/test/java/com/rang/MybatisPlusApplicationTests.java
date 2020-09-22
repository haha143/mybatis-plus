package com.rang;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rang.mapper.UserMapper;
import com.rang.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class MybatisPlusApplicationTests {

//    继承了BaseMapper.所有的方法都来自于父类
//    也可以编写自己的扩展方法
    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
//        参数是一个Wrapper,条件构造器,这里暂时用不到,先填null
//        查询全部用户
        List<User>users=userMapper.selectList(null);
        users.forEach(System.out::println);
    }

    @Test
    public void testinsert()
    {
        User user=new User();
        user.setName("rangrang");
        user.setAge(18);
        user.setEmail("22935579@qq.com");
        int result=userMapper.insert(user);//帮我们自动生成id
        System.out.println(result);
        System.out.println(user);
    }

    @Test
    public void testUpdate(){
        User user=new User();
        user.setId(5L);
        user.setName("关注公众号,谢谢");
        user.setAge(80);
        int i=userMapper.updateById(user);
        System.out.println(i);
    }

    //乐观锁成功
    @Test
    public void testOptimisticLocker1(){
        User user=userMapper.selectById(1L);
//        User user=new User();
        user.setId(1L);
        user.setName("爱你三千遍");
        userMapper.updateById(user);

    }

    //乐观锁失败
    @Test
    public void testOptimisticLocker2(){
        //线程1
        User user1=userMapper.selectById(1L);
        user1.setName("要你命三千111");
        //线程2插队
        User user2=userMapper.selectById(1L);
        user2.setName("要你命三千222");
        userMapper.updateById(user2);
        userMapper.updateById(user1);//如果没有乐观锁,就会覆盖插队线程的值

    }
//查询单个用户
    @Test
    public void testSelectByid(){
       User user=userMapper.selectById(2L);
        System.out.println(user);
    }

//    查询多个用户
    @Test
    public void testSelectByBatchId(){
        List<User> users=userMapper.selectBatchIds(Arrays.asList(1,2,3));
        users.forEach(System.out::println);
    }

    //条件查询通过map集合封装我们的数据
    @Test
    public void testSelectByBatchIds(){
        HashMap<String,Object>map=new HashMap<>();
//      自定义条件查询
        map.put("name","rangrang");
        List<User> users= userMapper.selectByMap(map);
        users.forEach(System.out::println);
    }

//    测试分页查询
    @Test
    public void testPage()
    {
//        Page<User> page=new Page<>();
//        Page<User> page=new Page<>(2,5);
        Page<User> page=new Page<>(2,5,7);
        userMapper.selectPage(page,null);
        page.getRecords().forEach(System.out::println);
    }

//    测试删除
    @Test
    public void testDeleteById(){
        userMapper.deleteById(1L);
    }

//    通过id批量删除
    @Test
    public void testDeleteBatchId(){
        userMapper.deleteBatchIds(Arrays.asList(1307885269738921989L,1307885269738921988L));
    }

//    通过map删除
@Test
    public void testDeleteBymap(){
        HashMap<String,Object>map=new HashMap<>();
        map.put("name","rangrang");
        userMapper.deleteByMap(map);
    }
}




