package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import com.cursormeeting.cursor_meeting_demo.domain.MeetingRoom;
import com.cursormeeting.cursor_meeting_demo.mapper.MeetingRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingRoomService {
    
    private final MeetingRoomMapper meetingRoomMapper;
    
    public List<MeetingRoom> getAllMeetingRooms() {
        List<MeetingRoom> rooms = meetingRoomMapper.findAll();
        for (MeetingRoom room : rooms) {
            room.setEquipmentList(meetingRoomMapper.findEquipmentByRoomId(room.getId()));
        }
        return rooms;
    }
    
    public List<MeetingRoom> getAvailableMeetingRooms() {
        List<MeetingRoom> rooms = meetingRoomMapper.findAvailable();
        for (MeetingRoom room : rooms) {
            room.setEquipmentList(meetingRoomMapper.findEquipmentByRoomId(room.getId()));
        }
        return rooms;
    }
    
    public MeetingRoom getMeetingRoomById(Long id) {
        MeetingRoom room = meetingRoomMapper.findById(id);
        if (room != null) {
            room.setEquipmentList(meetingRoomMapper.findEquipmentByRoomId(id));
        }
        return room;
    }
    
    public List<MeetingRoom> getMeetingRoomsByCapacity(Integer minCapacity) {
        return meetingRoomMapper.findByCapacity(minCapacity);
    }
    
    public MeetingRoom createMeetingRoom(MeetingRoom meetingRoom) {
        meetingRoomMapper.insert(meetingRoom);
        // 장비 등록
        if (meetingRoom.getEquipment() != null && !meetingRoom.getEquipment().isBlank()) {
            String[] equipments = meetingRoom.getEquipment().split(",");
            for (String eq : equipments) {
                if (!eq.trim().isEmpty()) {
                    meetingRoomMapper.insertRoomEquipment(meetingRoom.getId(), eq.trim());
                }
            }
        }
        return meetingRoom;
    }
    
    public MeetingRoom updateMeetingRoom(Long id, MeetingRoom meetingRoom) {
        MeetingRoom existingRoom = meetingRoomMapper.findById(id);
        if (existingRoom == null) {
            throw new RuntimeException("회의실을 찾을 수 없습니다.");
        }
        
        meetingRoom.setId(id);
        meetingRoomMapper.update(meetingRoom);
        return meetingRoom;
    }
    
    public void deleteMeetingRoom(Long id) {
        meetingRoomMapper.deleteById(id);
    }
    
    public List<MeetingRoom> getMeetingRoomsWithCurrentStatus() {
        List<MeetingRoom> rooms = meetingRoomMapper.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (MeetingRoom room : rooms) {
            room.setEquipmentList(meetingRoomMapper.findEquipmentByRoomId(room.getId()));
            // isAvailable(=is_active)는 DB 값 그대로 두고, 예약 가능 여부만 별도 필드로 관리
            boolean reservable = room.getIsAvailable() != null && room.getIsAvailable() && meetingRoomMapper.findCurrentMeetings(room.getId(), now).isEmpty();
            room.setReservable(reservable);
        }
        return rooms;
    }
    
    public void toggleActive(Long id, boolean isActive) {
        meetingRoomMapper.updateIsActive(id, isActive);
    }
} 