package com.cursormeeting.cursor_meeting_demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CalendarEvent {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String eventType;
    private Boolean isAllDay;
    private String location;
    private String externalEventId;
    private String source;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // JOIN 정보
    private User user;
} 