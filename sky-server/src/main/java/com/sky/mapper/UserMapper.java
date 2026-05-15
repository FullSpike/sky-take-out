package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.DatabaseMetaData;
import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid 微信用户的openid
     * @return 用户信息
     */
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入用户
         * @param user
     */
    void insert(User user);

    /**
     * 根据用户id查询用户
     * @param id 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据用户信息查询用户数量
     * @param userMap
     * @return
     */
    Integer countByMap(Map<String, Object> userMap);
}
