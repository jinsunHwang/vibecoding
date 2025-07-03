package com.cursormeeting.cursor_meeting_demo.controller;

import com.cursormeeting.cursor_meeting_demo.domain.MeetingRoom;
import com.cursormeeting.cursor_meeting_demo.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {
    private final MeetingRoomService meetingRoomService;

    @GetMapping
    public ResponseEntity<List<MeetingRoom>> getAllMeetingRooms() {
        return ResponseEntity.ok(meetingRoomService.getMeetingRoomsWithCurrentStatus());
    }

    @GetMapping("/available")
    public ResponseEntity<List<MeetingRoom>> getAvailableMeetingRooms() {
        return ResponseEntity.ok(meetingRoomService.getAvailableMeetingRooms());
    }

    @PostMapping
    public ResponseEntity<MeetingRoom> createMeetingRoom(@Valid @RequestBody MeetingRoom meetingRoom) {
        // name/location/capacity 그대로 받고, isAvailable은 무조건 true
        meetingRoom.setIsAvailable(true);
        // description 필드는 무시, location만 사용
        MeetingRoom created = meetingRoomService.createMeetingRoom(meetingRoom);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingRoom> getMeetingRoomById(@PathVariable Long id) {
        MeetingRoom room = meetingRoomService.getMeetingRoomById(id);
        if (room == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(room);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id, @RequestParam boolean isActive) {
        meetingRoomService.toggleActive(id, isActive);
        return ResponseEntity.ok().build();
    }
} 