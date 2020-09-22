package com.rang;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rang.mapper.UserMapper;
import com.rang.pojo.User;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class WrapperTest {
    @Autowired
    UserMapper userMapper;

    @Test
    public void test01(){
        //查询name不为空,邮箱不为空,年龄大于12的用户,
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .isNotNull("name")
                .isNotNull("email")
                .ge("age",12);
        userMapper.selectList(wrapper).forEach(System.out::println);
    }

    @Test
    public void test02(){
        //查询name=tom的用户,
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .eq("name","tom");
        System.out.println(userMapper.selectOne(wrapper));//查询一个数据
    }

    @Test
    public void test03(){
        //查询age在15到20的用户,
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .between("age",15,20);
        System.out.println(userMapper.selectCount(wrapper));//查询的结果数
    }

    @Test
    public void test04(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.inSql("id","select id from user where id<3");
        List<Object> objects=userMapper.selectObjs(wrapper);
        objects.forEach(System.out::println);
    }

    @Test
    public void test05(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .notLike("name","e")
                .likeRight("email","test");
        List<Map<String,Object>> maps=userMapper.selectMaps(wrapper);
        maps.forEach(System.out::println);
    }

    @Test
    public void test06(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .orderByDesc("id");
        List<Map<String,Object>> maps=userMapper.selectMaps(wrapper);
        maps.forEach(System.out::println);
    }

}
