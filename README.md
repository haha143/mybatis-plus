# mybatis-plus
mybatis-plus的小demo

**养成习惯,先赞后看!!!!**
@[TOC](Mybatis-plus学习笔记)

学习使用第三方组件心得:

- **导入相应的依赖**:这里很简单只要在pom文件中粘贴即可
- **研究依赖如何配置**:这里主要就是编写插件特定的config类
- **代码如何编写**:这里主要可以参考官网教程或B站上的视频或csdn上的一些博客
- **提高扩展技术能力**:不能仅仅满足在官网上的快速入门,一定要多尝试,多写bug,因为出了bug解决bug的过程才能让你更加有印象

# 1. 前言

因为公司的技术主管推荐我使用mybatis-plus插件之后,自己就跟着网上的教程学习了一下,学完之后,我尼玛是真的香
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092219533710.jpeg#pic_center)


这里我就先粘贴官网上面说的那些优点

- **无侵入**：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑
- **损耗小**：启动即会自动注入基本 CURD，性能基本无损耗，直接面向对象操作
- **强大的 CRUD 操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持 Lambda 形式调用**：通过 Lambda 表达式，方便的编写各类查询条件，无需再担心字段写错
- **支持主键自动生成**：支持多达 4 种主键策略（内含分布式唯一 ID 生成器 - Sequence），可自由配置，完美解决主键问题
- **支持 ActiveRecord 模式**：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可进行强大的 CRUD 操作
- **支持自定义全局通用操作**：支持全局通用方法注入（ Write once, use anywhere ）
- **内置代码生成器**：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用
- **内置分页插件**：基于 MyBatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通 List 查询
- **分页插件支持多种数据库**：支持 MySQL、MariaDB、Oracle、DB2、H2、HSQL、SQLite、Postgre、SQLServer 等多种数据库
- **内置性能分析插件**：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能快速揪出慢查询
- **内置全局拦截插件**：提供全表 delete 、 update 操作智能分析阻断，也可自定义拦截规则，预防误操作

之后我就说一下  **我自己的切身体会**  吧

- **方便!方便!方便!....**

  真的太尼玛方便了,之前我们使用mybatis的时候就需要自己手动的去编写各种各样的SQL语句,并且还需要编写  **pojo-dao-service-controller** 这样的层级结构,使得我们的开发有一大部分的时间都是浪费在了这样的 `重复性劳动` 中,这就会严重打击我们的积极性,但是使用了mybatis-plus插件之后,是真的解放双手.
  
  我自己的理解就是导入依赖,编写mapper接口继承BaseMapper之后,基本上大部分的数据库操作就已经结束了,真的是太方便了.

- **操作非常的简单**

  这里我本来以为也要像其他插件配置各种各样的bean,config之类的文件,但是实际配置之后发现真的非常简单.

  我们只需要添加依赖

  ```java
  <!--        mybatis-plus-->
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-boot-starter</artifactId>
              <version>3.0.5</version>
          </dependency>
  ```

  创建我们的mapper接口继承BaseMapper

  ```java
  @Repository
  public interface UserMapper extends BaseMapper<User> {
  //    所有的crud已经完全写好,不需要配置一大堆文件
  }
  ```

  最后只需要创建一个config类去扫描我们的mapper接口的包就行了

  ```java
  //扫描我们的mapper文件夹
  @MapperScan("com.rang.mapper")
  @EnableTransactionManagement  //开启事务支持
  @Configuration//配置类
  public class MyBatisPlusConfig {
  }
  ```

  就上述加起来都不到15行的代码就帮我们把之前我们那么繁琐的工作就完成了,你说这简不简单,我尼玛真的太简单了

- **内置的组件多**

  这里就说我自己平常最需要用到的一些组件吧.

  **乐观锁** : 当多个用户进行操作时,加锁就显得非常重要

  **分页组件** : 这个只要你的数据量还可以,分页显示,你就少不了吧

  最后就是博主学习的教程就是 **狂神说java** 的教程,这是视频的地址:[https://www.bilibili.com/video/BV17E411N7KN](https://www.bilibili.com/video/BV17E411N7KN)
  以下的笔记中既有狂神的理念,同时也包含了我自己的一些思考.
并且我已经将整个项目上传到了我自己的GitHub上( `主要是为了练习一下如何把项目放到GitHub上面托管` )
这是链接地址:[https://github.com/haha143/mybatis-plus](https://github.com/haha143/mybatis-plus)
# 2. 创建表,连接数据库

创建数据库mybatis-plus

创建user表:

```java
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
	id BIGINT(20) NOT NULL COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	PRIMARY KEY (id)
);
--真实开发中,version(乐观锁),deleted(逻辑删除),gmt_create,gmt_modified
```

插入数据:

```java
DELETE FROM user;
INSERT INTO user (id, name, age, email) VALUES
(1, 'Jone', 18, 'test1@baomidou.com'),
(2, 'Jack', 20, 'test2@baomidou.com'),
(3, 'Tom', 28, 'test3@baomidou.com'),
(4, 'Sandy', 21, 'test4@baomidou.com'),
(5, 'Billie', 24, 'test5@baomidou.com');
```

编写项目,初始化项目

依赖:

```java
<!--        数据库驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
<!--        lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
<!--        mybatis-plus-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.5</version>
        </dependency>
```

mybatis-plus可以节省大量的代码,尽量不要同时导入mybatis和mybatis-plus

```java
#这里我主要是针对的mysql5.7的版本
spring.datasource.username=root
spring.datasource.password=mysqladmin
spring.datasource.url=jdbc:mysql://localhost:3306/mybatis_plus?useSSL=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
#mysql8的话就需要将驱动改成com.mysql.cj.jdbc.Driver,并且一定要在url里面添加时区serverTimezone这个属性,否则一定会报错
```

传统方式pojo-dao(连接mybatis,配置mapper.xml文件)-service-controller

使用mybatis-plus之后:

- pojo

  ```java
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public class User {
      private Long id;
      private String name;
      private Integer age;
      private String email;
  }
  ```
这里我使用了lombok插件,只要idea中下载一下lombok插件然后倒入依赖就能正常使用了
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922201310660.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)

```java
<!--        lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

- mapper接口

  ```java
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  import com.rang.pojo.User;
  import org.springframework.stereotype.Repository;
  
  @Repository
  public interface UserMapper extends BaseMapper<User> {
  //    所有的crud已经完全写好,不需要配置一大堆文件
  }
  ```

- 我么需要在mybatis-plus主启动类上来扫描我们的mapper接口

  ```java
  //扫描我们的mapper文件夹
  @MapperScan("com.rang.mapper")
  @SpringBootApplication
  public class MybatisPlusApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(MybatisPlusApplication.class, args);
      }
  }
  ```

- 测试类中测试

  ```java
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
  }
  ```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195451251.png#pic_center)


# 3. 配置日志

我们所有的SQL现在是不可见的,我们需要看懂是怎么执行的

```java
#配置日志
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195522411.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


# 4. crud扩展

## 4.1 插入操作

```java
@Test
    public void testinsert()
    {
        User user=new User();
        user.setName("rangrang");
        user.setAge(18);
        user.setEmail("2293557957@qq.com");
        int result=userMapper.insert(user);//帮我们自动生成id
        System.out.println(result);
        System.out.println(user);
    }
```

## 4.2 主键生成策略

```java
 @TableId(type = IdType.AUTO)//当选择是AUTO的时候,必须要保证数据库中表的该字段也是自增的,否则会报错
    private Long id;
```

```java
/**
     * 数据库ID自增
     */
    AUTO(0),
    /**
     * 该类型为未设置主键类型
     */
    NONE(1),
    /**
     * 用户输入ID
     * 该类型可以通过自己注册自动填充插件进行填充
     */
    INPUT(2),

    /* 以下3种类型、只有当插入对象ID 为空，才自动填充。 */
    /**
     * 全局唯一ID (idWorker)
     */
    ID_WORKER(3),
    /**
     * 全局唯一ID (UUID)
     */
    UUID(4),
    /**
     * 字符串全局唯一ID (idWorker 的字符串表示)
     */
    ID_WORKER_STR(5);
```

## 4.3 修改操作

```java
@Test
    public void testUpdate(){
        User user=new User();
        user.setId(5L);
        user.setName("关注公众号,谢谢");
        //user.setAge(18);
        int i=userMapper.updateById(user);
        System.out.println(i);
    }
```

可以实现动态拼接SQL
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195545930.png#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092219560524.png#pic_center)


## 4.4 自动填充

创建时间,修改时间这些操作一般是自动化完成

> 方式一:数据库级别

在表中设置create_time与update_time

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195630745.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


同步实体类后再次测试插入方法
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195648616.png#pic_center)




> 方式二:代码级别

删除之前的操作

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195707206.png#pic_center)


在实体类的属性上添加注解

```java
    @TableField(fill = FieldFill.INSERT)
    private Data createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Data UpdateTime;
```

编写处理器来处理这个注解即可!

```java
package com.rang.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component //一定不要忘记将处理器添加到IOC容器之中
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill....");
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

    //更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill....");
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
```

编写完成之后我们去重新测试

## 4.5 测试插入

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195727512.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


测试更新

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092219574373.png#pic_center)


## 4.6 乐观锁

> 乐观锁,非常乐观,干啥都不会上锁,如果出现问题,再次更新值测试
>
> 悲观锁,非常悲观,每次测试都需要上锁

乐观锁实现方式：

- 取出记录时，获取当前version
- 更新时，带上这个version
- 执行更新时， set version = newVersion where version = oldVersion
- 如果version不对，就更新失败

```java
1 查询时就先将version查出来  version=1
2,3 修改的时候将version也带上,这样就能使得资源安全
  A线程
  update set name="heihei" ,version=version + 1
  where id=3 and version=1
  B线程
  update set name="heihei" ,version=version + 1
  where id=3 and version=1
  显然这样操作之后只有A线程能够正常执行,B线程就不能执行
```

测试乐观锁

修改表结构

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195801932.png#pic_center)


修改实体类

```java
 @Version //乐观锁的Version注解
 private Integer version;
```

注册组件

```java
package com.rang.config;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//扫描我们的mapper文件夹
@MapperScan("com.rang.mapper")
@EnableTransactionManagement  //开启事务支持
@Configuration//配置类
public class MyBatisPlusConfig {
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }
}
```

测试乐观锁成功(单线程必定成功)

```java
//乐观锁成功
@Test
public void testOptimisticLocker(){
    //查询用户信息
    User user=userMapper.selectById(1L);
    //修改用户信息
    user.setName("要你命三千");
    //执行更新操作
    userMapper.updateById(user);
}
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195820931.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


**划重点!!!!**  

这里提醒大家一下,大家可能会觉得我们有必要先查出用户的信息吗,能不能直接执行更新操作的,这里我们来测试一下,我们将测试的代码改成下面这样:

```java
//乐观锁成功
    @Test
    public void testOptimisticLocker1(){
//        User user=userMapper.selectById(1L);
        User user=new User();
        user.setId(1L);
        user.setName("爱你三千遍");
        userMapper.updateById(user);
    }
```

测试之后我们发现

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020092219584174.png#pic_center)


的确是执行了更新操作的,但是我们仔细看SQL语句能够发现他并有带上我们的version字段同时进行检测,所以说这样是不能够实现乐观锁的概念的,所以说本质上我们在执行操作的时候本质上都需要先查询一下该对象本质上就是查询出该对象的version字段,这样我们之后的操作才能够将version字段同时带进去进行检测,从而达到乐观锁的目的.

测试乐观锁失败

```java 
//乐观锁失败,多线程下
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
```

[外链图片转存失败,源站可能有防盗链机制,建议将图片保存下来直接上传(img-Ag8v0lKx-1600775508479)(C:\Users\瓤瓤\AppData\Roaming\Typora\typora-user-images\1600738078637.png)]
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195858457.png#pic_center)

这里我们可以发现  **userMapper.updateById(user1)** 这个方法是没有被执行的,这里的主要原因就如下图所示:


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195911860.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)

## 4.7 查询操作

查新单个信息

```java
//查询单个用户
    @Test
    public void testSelectByid(){
       User user=userMapper.selectById(1L);
        System.out.println(user);
    }
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195942488.png#pic_center)

查询多个信息

```java
//    查询多个用户
    @Test
    public void testSelectByBatchId(){
        List<User> users=userMapper.selectBatchIds(Arrays.asList(1,2,3));
        users.forEach(System.out::println);
    }
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922195959457.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


条件查询

```java
//条件查询通过map集合封装我们的数据
@Test
public void testSelectByBatchIds(){
    HashMap<String,Object>map=new HashMap<>();
    //      自定义条件查询
    map.put("name","rangrang");
    List<User> users= userMapper.selectByMap(map);
    users.forEach(System.out::println);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200016985.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


## 4.8 分页查询

其实我自己的分页查询的真个学习历程就是这样的

一开始就是通过 `limit与offset` 两个参数来进行查询的,并且还要另外写一个SQL语句来读取数据的总量,这样就会使得我们的SQL语句显得十分的臃肿就如下图所示:

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020072116285326.png) 

之后同事介绍我使用了 `pagehelper` 这个插件之后,自己那时候也是觉得真香,还特地写了篇博客来记录了一下,大家有兴趣的可以去看看:[`前后端分离使用pagehelper`](https://blog.csdn.net/lovely__RR/article/details/107491950)

他会很明显的减少我们的SQL语句编写的工作量,不用再加上limit这些参数,直接写出select语句即可,并且不用再另外写一个查询数据总量的SQL语句,但是他本身还是需要我们传入两个必要的参数page与size,之后在controller层对他进行操作实现分页查询的功能.

但是使用了 `mybatis-plus` 之后你就知道什么叫更香了,mybatis-plus的分页插件就显得更加的简单方便,同样的我们只需要在配置类中注册分页插件,之后我们就可以直接使用了

```java 
//注册分页插件
@Bean
public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
}
```

之后我们就可以直接使用了

```java
//    测试分页查询
@Test
public void testPage()
{
    //第一个参数代表当前页
    //第二个参数代表每页显示的数据条数
    Page<User> page=new Page<>(1,5);
    userMapper.selectPage(page,null);
    page.getRecords().forEach(System.out::println);
}
```

这里Page有三个构造函数的如下图:


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200034336.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)

无参构造函数默认情况下就是显示第一页,每页显示的数据条数默认是10条

测试代码:

```java
Page<User> page=new Page<>();
```


![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200052397.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)

我们可以看到在执行分页查询之前,它先查询了数据的总条数,之后再执行的分页查询,并且可以看到显示的也的确是第一页的10条数据

之后我们测试第二种构造函数

```java
Page<User> page=new Page<>(2,5);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200110287.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


可以看到整体流程也是和上面差不多,只是显示的数据条数和第几页发生了变化.

我们再测试一下第三种构造函数

```java
Page<User> page=new Page<>(2,5,6);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200125965.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


显然分页查询仍然能够正常执行下去的,但是我们可以发现他是 **直接执行分页查询操作** ,并没有像上面两步一样先去执行一次查询数据总量的操作,这就是因为我们已经给定了total这个变量名了,所以他就不会再去执行这个操作了,并且我们不需要一定给定正确的total值,只要随便给定一个了,很明显我们一共是有11条数据的,但是我输入6,他也是能够正常执行出来的.

## 4.9 删除操作

删除单个

```java
//    测试删除
@Test
public void testDeleteById(){
    userMapper.deleteById(1307885269738921990L);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200142117.png#pic_center)


通过id删除多个

```java
//    通过id批量删除
@Test
public void testDeleteBatchId(){
    userMapper.deleteBatchIds(Arrays.asList(1307885269738921989L,1307885269738921988L));
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200159561.png#pic_center)


通过map删除

```java
//    通过map删除
@Test
public void testDeleteBymap(){
    HashMap<String,Object>map=new HashMap<>();
    map.put("name","rangrang");
    userMapper.deleteByMap(map);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200216437.png#pic_center)


## 4.10 逻辑删除

> 逻辑删除:只是改变了数据库中的某个字段,并不是真正的将数据从数据库中删除,就好比deleted字段deleted=0表示用户看不到该信息,但是管理员仍然能够看到deleted=1则表示用户和管理员都能看到,主要是为了维护数据
>
> 物理删除:直接从数据库中将数据删除,就是我们上述删除操作干的事  

这样主要是为了防止数据的丢失,就好比我们电脑中的回收站.

首先我们需要修改数据库的结构,添加deleted这个字段,并且设置默认值是0,删除的话就改为1

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200232288.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


之后我们需要修改user实体类

```java
@TableLogic //逻辑删除注解
private Integer deleted;
```

之后我们需要去注册这个组件

```java
@Bean
public ISqlInjector iSqlInjector(){
    return new LogicSqlInjector();
}
```

之后我们就需要去设置一下属性

```java
#配置逻辑删除
# 逻辑已删除值(默认为 1)
mybatis-plus.global-config.db-config.logic-delete-value: 1 
# 逻辑未删除值(默认为 0)
mybatis-plus.global-config.db-config.logic-not-delete-value: 0 
```

之后我们就可以来测试了

```java
//    测试删除
@Test
public void testDeleteById(){
    userMapper.deleteById(1L);
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200249752.png#pic_center)


这里我们可以发现删除操作已经从之前的delete转换成了update操作了,既然这样我们再来看看这条数据我们还能不能查询出来

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200304531.png#pic_center)


显然到这里我们就能发现数据没有查询出来,查询操作执行的时候就会再带上一个条件那就是deleted=0这个条件

# 5. 性能分析插件

导入插件

```java
    //性能分析插件
    @Bean
    @Profile({"dev","test"})//设置dev test环境开发  主要就是为了保证我们的开发效率
    public PerformanceInterceptor performanceInterceptor(){
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        performanceInterceptor.setMaxTime(1);//设置最大的超时时间,超过1秒就不执行
        performanceInterceptor.setFormat(true);//SQL语句格式化显示
        return performanceInterceptor;
    }
```

因为我们设置了只有在开发与测试环境中才生效,所以我们需要去配置文件里面加上开发环境参数

```java
#设置开发环境
spring.profiles.active=dev
```

这样我们就能够正常使用该组件了

测试使用

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200321206.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


显然我们的程序是没有错的,但是还是报错了,提示我们的SQL语句执行的时间比我们设定的最大超时时间要长,所以报错了.

这里我们重新设置一下超时时间再来测试一下

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200922200336505.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)


这样我们就不报错,这主要就是帮助我们后序的SQL语句优化.

# 6. 条件构造器

 测试一

```java
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
```

 测试二

```java
@Test
public void test02(){
    //查询name=tom的用户,
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper
        .eq("name","tom");
    System.out.println(userMapper.selectOne(wrapper));//查询一个数据
}
```

 测试三

```java
@Test
public void test03(){
    //查询age在15到20的用户,
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper
        .between("age",15,20);
    System.out.println(userMapper.selectCount(wrapper));//查询的结果数
}
```

 测试四

```java
@Test
public void test04(){
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper.inSql("id","select id from user where id<3");
    List<Object> objects=userMapper.selectObjs(wrapper);
    objects.forEach(System.out::println);
}
```

 测试五

```java
@Test
public void test05(){
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper
        .notLike("name","e")
        .likeRight("email","test");
    List<Map<String,Object>> maps=userMapper.selectMaps(wrapper);
    maps.forEach(System.out::println);
}
```

 测试六

```java
@Test
public void test06(){
    QueryWrapper<User> wrapper = new QueryWrapper<>();
    wrapper
        .orderByDesc("id");
    List<Map<String,Object>> maps=userMapper.selectMaps(wrapper);
    maps.forEach(System.out::println);
}
```

# 7. 代码自动生成器

```java
public class RangCode {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("jobob");
        gc.setOpen(false);
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/ant?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("密码");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        pc.setParent("com.baomidou.ant");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
        strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
```
都看到这儿了,如果觉得对你有帮助的话,可以关注我的公众号,新人up需要你的帮助!!!
![在这里插入图片描述](https://img-blog.csdnimg.cn/20201029164019538.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xvdmVseV9fUlI=,size_16,color_FFFFFF,t_70#pic_center)





