package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import com.cursormeeting.cursor_meeting_demo.dto.MeetingDto;
import com.cursormeeting.cursor_meeting_demo.mapper.MeetingMapper;
import com.cursormeeting.cursor_meeting_demo.mapper.MeetingRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {
    
    private final MeetingMapper meetingMapper;
    private final MeetingRoomMapper meetingRoomMapper;
    private final UserService userService;
    private final MeetingRoomService meetingRoomService;
    
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = meetingMapper.findAllWithRoomEquipment();
        for (Meeting m : meetings) {
            if (m.getRoom() != null && m.getRoom().getId() != null) {
                List<String> eq = meetingRoomMapper.findEquipmentByRoomId(m.getRoom().getId());
                m.getRoom().setEquipmentList(eq);
            }
        }
        return meetings;
    }
    
    public Meeting getMeetingById(Long id) {
        Meeting meeting = meetingMapper.findByIdWithRoomEquipment(id);
        if (meeting != null && meeting.getRoom() != null && meeting.getRoom().getId() != null) {
            List<String> eq = meetingRoomMapper.findEquipmentByRoomId(meeting.getRoom().getId());
            meeting.getRoom().setEquipmentList(eq);
        }
        return meeting;
    }
    
    public List<Meeting> getMeetingsByOrganizerId(Long organizerId) {
        // organizerId로 필터링 후 room.equipmentList 포함
        return meetingMapper.findAllWithRoomEquipment().stream()
            .filter(m -> m.getOrganizerId() != null && m.getOrganizerId().equals(organizerId))
            .toList();
    }
    
    public List<Meeting> getMeetingsByParticipantId(Long participantId) {
        // participantId로 필터링 후 room.equipmentList 포함
        return meetingMapper.findAllWithRoomEquipment().stream()
            .filter(m -> m.getParticipants() != null && m.getParticipants().stream().anyMatch(p -> p.getId().equals(participantId)))
            .toList();
    }
    
    public List<Meeting> getMeetingsByRoomId(Long roomId) {
        return meetingMapper.findByRoomIdWithRoomEquipment(roomId);
    }
    
    public List<Meeting> getMeetingsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 시간 범위로 필터링 후 room.equipmentList 포함
        return meetingMapper.findAllWithRoomEquipment().stream()
            .filter(m -> m.getStartTime() != null && m.getEndTime() != null &&
                !m.getStartTime().isBefore(startTime) && !m.getEndTime().isAfter(endTime))
            .toList();
    }
    
    public Meeting createMeeting(MeetingDto meetingDto) {
        // 충돌 검사
        if (meetingDto.getRoomId() != null) {
            List<Meeting> conflicts = meetingMapper.findConflicts(
                meetingDto.getRoomId(), meetingDto.getStartTime(), meetingDto.getEndTime());
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("해당 시간에 회의실이 이미 예약되어 있습니다.");
            }
        }
        
        Meeting meeting = new Meeting();
        meeting.setTitle(meetingDto.getTitle());
        meeting.setDescription(meetingDto.getDescription());
        meeting.setOrganizerId(meetingDto.getOrganizerId());
        meeting.setRoomId(meetingDto.getRoomId());
        meeting.setStartTime(meetingDto.getStartTime());
        meeting.setEndTime(meetingDto.getEndTime());
        meeting.setStatus(meetingDto.getStatus() != null ? meetingDto.getStatus() : "SCHEDULED");
        meeting.setType(meetingDto.getType() != null ? meetingDto.getType() : "IN_PERSON");
        meeting.setMeetingLink(meetingDto.getMeetingLink());
        meeting.setIsRecurring(meetingDto.getIsRecurring());
        meeting.setRecurrencePattern(meetingDto.getRecurrencePattern());
        meeting.setRecurrenceEndDate(meetingDto.getRecurrenceEndDate());
        meeting.setMeetingNotes(meetingDto.getMeetingNotes());
        
        meetingMapper.insert(meeting);
        
        // 참가자 추가
        if (meetingDto.getParticipantIds() != null) {
            for (Long participantId : meetingDto.getParticipantIds()) {
                meetingMapper.insertParticipant(meeting.getId(), participantId);
            }
        }
        
        return meeting;
    }
    
    public Meeting updateMeeting(Long id, MeetingDto meetingDto) {
        Meeting meeting = meetingMapper.findById(id);
        if (meeting == null) {
            throw new RuntimeException("회의를 찾을 수 없습니다.");
        }
        
        // 충돌 검사 (자신 제외)
        if (meetingDto.getRoomId() != null) {
            List<Meeting> conflicts = meetingMapper.findConflicts(
                meetingDto.getRoomId(), meetingDto.getStartTime(), meetingDto.getEndTime());
            conflicts.removeIf(m -> m.getId().equals(id));
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("해당 시간에 회의실이 이미 예약되어 있습니다.");
            }
        }
        
        meeting.setTitle(meetingDto.getTitle());
        meeting.setDescription(meetingDto.getDescription());
        meeting.setOrganizerId(meetingDto.getOrganizerId());
        meeting.setRoomId(meetingDto.getRoomId());
        meeting.setStartTime(meetingDto.getStartTime());
        meeting.setEndTime(meetingDto.getEndTime());
        meeting.setStatus(meetingDto.getStatus());
        meeting.setType(meetingDto.getType());
        meeting.setMeetingLink(meetingDto.getMeetingLink());
        meeting.setIsRecurring(meetingDto.getIsRecurring());
        meeting.setRecurrencePattern(meetingDto.getRecurrencePattern());
        meeting.setRecurrenceEndDate(meetingDto.getRecurrenceEndDate());
        meeting.setMeetingNotes(meetingDto.getMeetingNotes());
        
        meetingMapper.update(meeting);
        
        // 참가자 업데이트
        if (meetingDto.getParticipantIds() != null) {
            meetingMapper.deleteParticipants(meeting.getId());
            for (Long participantId : meetingDto.getParticipantIds()) {
                meetingMapper.insertParticipant(meeting.getId(), participantId);
            }
        }
        
        return meeting;
    }
    
    public void deleteMeeting(Long id) {
        meetingMapper.deleteParticipants(id);
        meetingMapper.deleteById(id);
    }
    
    public List<Meeting> getConflicts(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return meetingMapper.findConflicts(roomId, startTime, endTime);
    }
    
    public List<Meeting> getMeetingsForToday() {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        // 오늘 시작/종료가 걸쳐있는 회의도 포함
        return meetingMapper.findAllWithRoomEquipment().stream()
            .filter(m -> m.getStartTime() != null && m.getEndTime() != null &&
                m.getEndTime().isAfter(start) && m.getStartTime().isBefore(end))
            .toList();
    }

    public List<Meeting> getMeetingsForThisWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(java.time.DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(7);
        return getMeetingsByTimeRange(start, end);
    }
} 