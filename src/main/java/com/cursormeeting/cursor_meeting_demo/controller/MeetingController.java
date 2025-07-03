package com.cursormeeting.cursor_meeting_demo.controller;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import com.cursormeeting.cursor_meeting_demo.dto.MeetingDto;
import com.cursormeeting.cursor_meeting_demo.dto.MeetingSuggestionDto;
import com.cursormeeting.cursor_meeting_demo.service.MeetingService;
import com.cursormeeting.cursor_meeting_demo.service.MeetingSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {
    
    private final MeetingService meetingService;
    private final MeetingSuggestionService meetingSuggestionService;
    
    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(meeting);
    }
    
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Meeting>> getMeetingsByOrganizerId(@PathVariable Long organizerId) {
        return ResponseEntity.ok(meetingService.getMeetingsByOrganizerId(organizerId));
    }
    
    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<Meeting>> getMeetingsByParticipantId(@PathVariable Long participantId) {
        return ResponseEntity.ok(meetingService.getMeetingsByParticipantId(participantId));
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Meeting>> getMeetingsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(meetingService.getMeetingsByRoomId(roomId));
    }
    
    @GetMapping("/timerange")
    public ResponseEntity<List<Meeting>> getMeetingsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(meetingService.getMeetingsByTimeRange(startTime, endTime));
    }
    
    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@Valid @RequestBody MeetingDto meetingDto) {
        try {
            Meeting meeting = meetingService.createMeeting(meetingDto);
            return ResponseEntity.ok(meeting);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @Valid @RequestBody MeetingDto meetingDto) {
        try {
            Meeting meeting = meetingService.updateMeeting(id, meetingDto);
            return ResponseEntity.ok(meeting);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        try {
            meetingService.deleteMeeting(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/conflicts")
    public ResponseEntity<List<Meeting>> getConflicts(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(meetingService.getConflicts(roomId, startTime, endTime));
    }

    @GetMapping("/today")
    public ResponseEntity<List<Meeting>> getTodayMeetings() {
        return ResponseEntity.ok(meetingService.getMeetingsForToday());
    }

    @GetMapping("/week")
    public ResponseEntity<List<Meeting>> getWeekMeetings() {
        return ResponseEntity.ok(meetingService.getMeetingsForThisWeek());
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<Meeting>> getCalendarMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Meeting>> getMyMeetings(@RequestParam(required = false) Long userId) {
        if (userId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(meetingService.getMeetingsByParticipantId(userId));
    }

    @PostMapping("/suggestions")
    public ResponseEntity<List<MeetingSuggestionService.MeetingSuggestion>> suggestMeetingTimes(
            @Valid @RequestBody MeetingSuggestionDto suggestionDto) {
        try {
            List<MeetingSuggestionService.MeetingSuggestion> suggestions = 
                meetingSuggestionService.suggestMeetingTimes(suggestionDto);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 