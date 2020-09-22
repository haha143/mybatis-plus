package com.rang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rang.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
//    所有的crud已经完全写好,不需要配置一大堆文件
}
