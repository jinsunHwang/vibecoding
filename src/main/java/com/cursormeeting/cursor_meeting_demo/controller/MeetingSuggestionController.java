package com.cursormeeting.cursor_meeting_demo.controller;

import com.cursormeeting.cursor_meeting_demo.dto.MeetingSuggestionDto;
import com.cursormeeting.cursor_meeting_demo.service.MeetingSuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/meeting-suggestions")
@RequiredArgsConstructor
public class MeetingSuggestionController {
    
    private final MeetingSuggestionService meetingSuggestionService;
    
    @PostMapping
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

    @PostMapping("/suggestions")
    public ResponseEntity<List<MeetingSuggestionService.MeetingSuggestion>> suggestMeetingTimesAlias(
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