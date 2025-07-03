package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.CalendarEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CalendarEventMapper {
    List<CalendarEvent> findAll();
    CalendarEvent findById(@Param("id") Long id);
    List<CalendarEvent> findByUserId(@Param("userId") Long userId);
    List<CalendarEvent> findByTimeRange(@Param("userId") Long userId, 
                                       @Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
    List<CalendarEvent> findByUserIdsAndTimeRange(@Param("userIds") List<Long> userIds, 
                                                 @Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);
    int insert(CalendarEvent event);
    int update(CalendarEvent event);
    int deleteById(@Param("id") Long id);
    int deleteByExternalId(@Param("externalEventId") String externalEventId);
} 