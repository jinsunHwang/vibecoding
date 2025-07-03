package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.OAuthAccessToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OAuthAccessTokenMapper {
    OAuthAccessToken selectByUserId(@Param("userId") String userId);
    void insert(OAuthAccessToken token);
    void update(OAuthAccessToken token);
} 