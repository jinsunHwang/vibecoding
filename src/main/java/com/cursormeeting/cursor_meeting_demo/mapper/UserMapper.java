package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    List<User> findAll();
    User findById(@Param("id") Long id);
    User findByEmail(@Param("email") String email);
    List<User> findByTeamId(@Param("teamId") Long teamId);
    int insert(User user);
    int update(User user);
    int deleteById(@Param("id") Long id);
    List<User> findByIds(@Param("ids") List<Long> ids);
} 