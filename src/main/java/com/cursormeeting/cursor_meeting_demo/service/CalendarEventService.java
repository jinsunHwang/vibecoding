package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.CalendarEvent;
import com.cursormeeting.cursor_meeting_demo.mapper.CalendarEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalendarEventService {
    
    private final CalendarEventMapper calendarEventMapper;
    
    public List<CalendarEvent> getAllEvents() {
        return calendarEventMapper.findAll();
    }
    
    public CalendarEvent getEventById(Long id) {
        return calendarEventMapper.findById(id);
    }
    
    public List<CalendarEvent> getEventsByUserId(Long userId) {
        return calendarEventMapper.findByUserId(userId);
    }
    
    public List<CalendarEvent> getEventsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return calendarEventMapper.findByTimeRange(userId, startTime, endTime);
    }
    
    public List<CalendarEvent> getEventsByUserIdsAndTimeRange(List<Long> userIds, LocalDateTime startTime, LocalDateTime endTime) {
        return calendarEventMapper.findByUserIdsAndTimeRange(userIds, startTime, endTime);
    }
    
    public CalendarEvent createEvent(CalendarEvent event) {
        calendarEventMapper.insert(event);
        return event;
    }
    
    public CalendarEvent updateEvent(Long id, CalendarEvent event) {
        CalendarEvent existingEvent = calendarEventMapper.findById(id);
        if (existingEvent == null) {
            throw new RuntimeException("일정을 찾을 수 없습니다.");
        }
        
        event.setId(id);
        calendarEventMapper.update(event);
        return event;
    }
    
    public void deleteEvent(Long id) {
        calendarEventMapper.deleteById(id);
    }
    
    public void deleteEventByExternalId(String externalEventId) {
        calendarEventMapper.deleteByExternalId(externalEventId);
    }
} 