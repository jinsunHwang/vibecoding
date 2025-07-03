package com.cursormeeting.cursor_meeting_demo.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MeetingRoom {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private String equipment;  // DB에는 없음, 등록/수정 시 사용하지 않음
    private List<String> equipmentList; // room_equipment 테이블 기반 장비 목록
    private Boolean isAvailable;
    private Boolean reservable; // 현재 예약 가능 여부(DB에는 없음)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 