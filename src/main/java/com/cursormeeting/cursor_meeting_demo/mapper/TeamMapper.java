package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeamMapper {
    List<Team> findAll();
    Team findById(@Param("id") Long id);
    int insert(Team team);
    int update(Team team);
    int deleteById(@Param("id") Long id);
} 