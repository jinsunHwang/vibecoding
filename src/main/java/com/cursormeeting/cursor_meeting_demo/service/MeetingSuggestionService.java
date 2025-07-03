package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import com.cursormeeting.cursor_meeting_demo.domain.MeetingRoom;
import com.cursormeeting.cursor_meeting_demo.dto.MeetingSuggestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingSuggestionService {
    
    private final MeetingService meetingService;
    private final MeetingRoomService meetingRoomService;
    private final UserService userService;
    
    public List<MeetingSuggestion> suggestMeetingTimes(MeetingSuggestionDto suggestionDto) {
        List<MeetingSuggestion> suggestions = new ArrayList<>();
        String preferredTime = suggestionDto.getPreferredTime();
        // 참가자들의 기존 회의 일정 가져오기 (Google Calendar 대신 기존 회의 데이터 사용)
        List<Meeting> existingMeetings = meetingService.getMeetingsByTimeRange(
            suggestionDto.getPreferredStartTime(),
            suggestionDto.getPreferredEndTime()
        );
        // 참가자별로 필터링
        List<Meeting> participantMeetings = existingMeetings.stream()
            .filter(meeting -> {
                if (suggestionDto.getParticipantIds().contains(meeting.getOrganizerId())) {
                    return true;
                }
                return false;
            })
            .collect(Collectors.toList());
        // 회의실 목록 가져오기
        List<MeetingRoom> availableRooms = getAvailableRooms(suggestionDto);
        // 시간대별로 빈 시간 찾기
        Map<LocalDateTime, List<MeetingRoom>> availableSlots = findAvailableTimeSlots(
            suggestionDto.getParticipantIds(),
            participantMeetings,
            availableRooms,
            suggestionDto.getPreferredStartTime(),
            suggestionDto.getPreferredEndTime(),
            suggestionDto.getDurationMinutes()
        );
        // 추천 시간 생성 (오전/오후/상관없음 분기)
        for (Map.Entry<LocalDateTime, List<MeetingRoom>> entry : availableSlots.entrySet()) {
            LocalDateTime startTime = entry.getKey();
            List<MeetingRoom> rooms = entry.getValue();
            int hour = startTime.getHour();
            boolean isMorning = hour >= 9 && hour < 12;
            boolean isAfternoon = hour >= 14 && hour < 17;
            boolean add = false;
            if ("MORNING".equals(preferredTime)) {
                add = isMorning;
            } else if ("AFTERNOON".equals(preferredTime)) {
                add = isAfternoon;
            } else if (preferredTime == null || "".equals(preferredTime)) {
                add = isMorning || isAfternoon;
            }
            if (!add) continue;
            for (MeetingRoom room : rooms) {
                MeetingSuggestion suggestion = new MeetingSuggestion();
                suggestion.setStartTime(startTime);
                suggestion.setEndTime(startTime.plusMinutes(suggestionDto.getDurationMinutes()));
                suggestion.setRoom(room);
                suggestion.setScore(calculateScore(startTime, room, suggestionDto));
                suggestion.setPreferredTime(isMorning ? "MORNING" : (isAfternoon ? "AFTERNOON" : ""));
                suggestions.add(suggestion);
            }
        }
        suggestions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return suggestions.stream().limit(10).collect(Collectors.toList());
    }
    
    private List<MeetingRoom> getAvailableRooms(MeetingSuggestionDto suggestionDto) {
        List<MeetingRoom> rooms = meetingRoomService.getAvailableMeetingRooms();
        
        // 용량 필터링
        if (suggestionDto.getMinCapacity() != null) {
            rooms = rooms.stream()
                .filter(room -> room.getCapacity() >= suggestionDto.getMinCapacity())
                .collect(Collectors.toList());
        }
        
        // 장비 필터링
        if (suggestionDto.getRequiredEquipment() != null && !suggestionDto.getRequiredEquipment().isEmpty()) {
            for (String equipment : suggestionDto.getRequiredEquipment()) {
                rooms = rooms.stream()
                    .filter(room -> room.getEquipment() != null && room.getEquipment().contains(equipment))
                    .collect(Collectors.toList());
            }
        }
        
        return rooms;
    }
    
    private Map<LocalDateTime, List<MeetingRoom>> findAvailableTimeSlots(
            List<Long> participantIds,
            List<Meeting> meetings,
            List<MeetingRoom> rooms,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer durationMinutes) {
        
        Map<LocalDateTime, List<MeetingRoom>> availableSlots = new HashMap<>();
        
        // 30분 단위로 시간대 생성
        LocalDateTime currentTime = startTime;
        while (currentTime.plusMinutes(durationMinutes).isBefore(endTime)) {
            final LocalDateTime slotEnd = currentTime.plusMinutes(durationMinutes);
            final LocalDateTime finalCurrentTime = currentTime;
            
            // 모든 참가자가 해당 시간에 일정이 없는지 확인
            boolean allParticipantsAvailable = participantIds.stream().allMatch(participantId ->
                meetings.stream().noneMatch(meeting ->
                    meeting.getOrganizerId().equals(participantId) &&
                    meeting.getStartTime().isBefore(slotEnd) &&
                    meeting.getEndTime().isAfter(finalCurrentTime)
                )
            );
            
            if (allParticipantsAvailable) {
                // 해당 시간에 사용 가능한 회의실 찾기
                List<MeetingRoom> availableRooms = rooms.stream()
                    .filter(room -> {
                        List<Meeting> conflicts = meetingService.getConflicts(
                            room.getId(), finalCurrentTime, slotEnd);
                        return conflicts.isEmpty();
                    })
                    .collect(Collectors.toList());
                
                if (!availableRooms.isEmpty()) {
                    availableSlots.put(currentTime, availableRooms);
                }
            }
            
            currentTime = currentTime.plusMinutes(30);
        }
        
        return availableSlots;
    }
    
    private double calculateScore(LocalDateTime startTime, MeetingRoom room, MeetingSuggestionDto suggestionDto) {
        double score = 0.0;
        
        // 오전 회의 선호 (9-12시)
        LocalTime time = startTime.toLocalTime();
        if (time.isAfter(LocalTime.of(9, 0)) && time.isBefore(LocalTime.of(12, 0))) {
            score += 10.0;
        }
        
        // 오후 회의 (14-17시)
        else if (time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(17, 0))) {
            score += 5.0;
        }
        
        // 점심시간 피하기 (12-13시)
        else if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(13, 0))) {
            score -= 5.0;
        }
        
        // 회의실 용량 최적화
        if (suggestionDto.getMinCapacity() != null) {
            int capacityDiff = room.getCapacity() - suggestionDto.getMinCapacity();
            if (capacityDiff <= 2) {
                score += 5.0; // 적절한 크기
            } else if (capacityDiff > 10) {
                score -= 2.0; // 너무 큰 회의실
            }
        }
        
        // 선호 시간대와의 근접성
        if (suggestionDto.getPreferredStartTime() != null) {
            long hoursDiff = Math.abs(java.time.Duration.between(
                suggestionDto.getPreferredStartTime(), startTime).toHours());
            score += Math.max(0, 10 - hoursDiff);
        }
        
        return score;
    }
    
    public static class MeetingSuggestion {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private MeetingRoom room;
        private double score;
        private String preferredTime;
        
        // Getters and Setters
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public MeetingRoom getRoom() { return room; }
        public void setRoom(MeetingRoom room) { this.room = room; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        public String getPreferredTime() { return preferredTime; }
        public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }
    }
} 