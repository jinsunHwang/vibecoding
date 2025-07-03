package com.cursormeeting.cursor_meeting_demo.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Team {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 