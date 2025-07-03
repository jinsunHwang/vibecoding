package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import com.cursormeeting.cursor_meeting_demo.domain.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingRoomMapper {
    List<MeetingRoom> findAll();
    List<MeetingRoom> findAvailable();
    MeetingRoom findById(@Param("id") Long id);
    List<MeetingRoom> findByCapacity(@Param("minCapacity") Integer minCapacity);
    int insert(MeetingRoom meetingRoom);
    int update(MeetingRoom meetingRoom);
    int deleteById(@Param("id") Long id);
    List<Meeting> findCurrentMeetings(@Param("roomId") Long roomId, @Param("currentTime") LocalDateTime currentTime);
    List<String> findEquipmentByRoomId(@Param("roomId") Long roomId);
    int insertRoomEquipment(@Param("roomId") Long roomId, @Param("equipment") String equipment);
    int updateIsActive(@Param("id") Long id, @Param("isActive") boolean isActive);
} 