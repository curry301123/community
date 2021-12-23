package com.nowcode.commuity.mapper;


import com.nowcode.commuity.domain.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginMapper {

    @Insert({"insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id") //自动生成id，并将其赋给实体类中的属性id
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({"select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update login_ticket set status = #{status} where ticket = #{ticket}"})
    int UpdateStatus(String ticket,int status);

    @Select({"select id,user_id,ticket,status,expired from login_ticket where user_id = #{userId}"})
    LoginTicket selectByUserId(int userId);


}
