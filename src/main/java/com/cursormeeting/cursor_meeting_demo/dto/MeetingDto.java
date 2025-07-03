package com.cursormeeting.cursor_meeting_demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetingDto {
    private Long id;
    
    @NotBlank(message = "회의 제목은 필수입니다.")
    private String title;
    
    private String description;
    
    @NotNull(message = "주최자는 필수입니다.")
    private Long organizerId;
    
    private Long roomId;
    
    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startTime;
    
    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalDateTime endTime;
    
    private String status;
    private String type;
    private String meetingLink;
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private String meetingNotes;
    
    // 참가자 목록
    private List<Long> participantIds;
} 