package com.cursormeeting.cursor_meeting_demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String role;
    private Long teamId;
    private String googleCalendarId;
    private String outlookCalendarId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Team 정보 (JOIN용)
    private Team team;
} 