package com.cursormeeting.cursor_meeting_demo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class MeetingSuggestionDto {
    private String title;
    private String description;
    private List<Long> participantIds;
    private LocalDateTime preferredStartTime;
    private LocalDateTime preferredEndTime;
    private Integer durationMinutes;
    private List<String> requiredEquipment;
    private Integer minCapacity;
    private String meetingType;
    
    // 프론트엔드에서 보내는 데이터를 위한 필드들
    private String startDate;
    private String endDate;
    private String preferredTime;
    
    // 프론트엔드 데이터를 LocalDateTime으로 변환하는 메서드들
    public LocalDateTime getPreferredStartTime() {
        if (startDate != null) {
            LocalDate date = LocalDate.parse(startDate);
            return date.atStartOfDay();
        }
        return preferredStartTime;
    }
    
    public LocalDateTime getPreferredEndTime() {
        if (endDate != null) {
            LocalDate date = LocalDate.parse(endDate);
            return date.atTime(LocalTime.of(23, 59, 59));
        }
        return preferredEndTime;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes != null ? durationMinutes : 60;
    }
} 