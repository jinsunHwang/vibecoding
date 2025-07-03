package com.cursormeeting.cursor_meeting_demo.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Meeting {
    private Long id;
    private String title;
    private String description;
    private Long organizerId;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String type;
    private String meetingLink;
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private String meetingNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // JOIN 정보
    private User organizer;
    private MeetingRoom room;
    private List<User> participants;
} 