package com.cursormeeting.cursor_meeting_demo.mapper;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingMapper {
    List<Meeting> findAll();
    Meeting findById(@Param("id") Long id);
    List<Meeting> findByOrganizerId(@Param("organizerId") Long organizerId);
    List<Meeting> findByParticipantId(@Param("participantId") Long participantId);
    List<Meeting> findByRoomId(@Param("roomId") Long roomId);
    List<Meeting> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                 @Param("endTime") LocalDateTime endTime);
    List<Meeting> findConflicts(@Param("roomId") Long roomId, 
                               @Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);
    int insert(Meeting meeting);
    int update(Meeting meeting);
    int deleteById(@Param("id") Long id);
    int insertParticipant(@Param("meetingId") Long meetingId, @Param("userId") Long userId);
    int deleteParticipants(@Param("meetingId") Long meetingId);
    Meeting findByIdWithRoomEquipment(@Param("id") Long id);
    List<Meeting> findAllWithRoomEquipment();
    List<Meeting> findByRoomIdWithRoomEquipment(@Param("roomId") Long roomId);
} 