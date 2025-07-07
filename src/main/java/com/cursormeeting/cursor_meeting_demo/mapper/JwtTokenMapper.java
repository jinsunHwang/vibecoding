package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.JwtToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface JwtTokenMapper {
    JwtToken selectByUserId(@Param("userId") String userId);
    void insert(JwtToken token);
    void update(JwtToken token);
} 