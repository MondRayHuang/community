package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    @Insert(
            {
                    "insert into login_ticket(user_id,ticket,status,expired)",
                    "value (#{userId},#{ticket},#{status},#{expired})"
            }
    )
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Update({
            "update login_ticket set status = #{status} where ticket = #{ticket}"
    })
    int updateStatus(String ticket, int status);

    @Select({
            "select id,user_id,status,ticket,expired",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectLoginTicket(String ticket);
}
