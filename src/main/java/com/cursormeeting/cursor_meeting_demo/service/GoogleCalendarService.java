package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.CalendarEvent;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.api.client.util.DateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {
    
    @Value("${google.calendar.api-key}")
    private String apiKey;
    
    @Value("${google.calendar.application-name}")
    private String applicationName;
    
    private final CalendarEventService calendarEventService;
    
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    public List<CalendarEvent> syncUserCalendar(String userId, String calendarId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Calendar service = getCalendarService();
            
            // Google Calendar에서 이벤트 가져오기
            Events events = service.events().list(calendarId)
                .setTimeMin(new DateTime(startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setTimeMax(new DateTime(endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
            
            List<CalendarEvent> syncedEvents = new ArrayList<>();
            
            for (Event event : events.getItems()) {
                CalendarEvent calendarEvent = convertGoogleEventToCalendarEvent(event, userId);
                calendarEventService.createEvent(calendarEvent);
                syncedEvents.add(calendarEvent);
            }
            
            return syncedEvents;
            
        } catch (Exception e) {
            log.error("Google Calendar 동기화 중 오류 발생", e);
            throw new RuntimeException("Google Calendar 동기화 실패", e);
        }
    }
    
    private Calendar getCalendarService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
            .setApplicationName(applicationName)
            .setGoogleClientRequestInitializer(request -> request.put("key", apiKey))
            .build();
    }
    
    private CalendarEvent convertGoogleEventToCalendarEvent(Event googleEvent, String userId) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle(googleEvent.getSummary());
        event.setDescription(googleEvent.getDescription());
        event.setExternalEventId(googleEvent.getId());
        event.setSource("GOOGLE");
        event.setEventType("PERSONAL");
        event.setIsAllDay(false);
        event.setLocation(googleEvent.getLocation());
        
        // 시작 시간 설정
        if (googleEvent.getStart().getDateTime() != null) {
            DateTime startDateTime = googleEvent.getStart().getDateTime();
            event.setStartTime(LocalDateTime.parse(startDateTime.toString().replace("T", " ").substring(0, 19)));
        } else {
            // 종일 이벤트
            DateTime startDate = googleEvent.getStart().getDate();
            event.setStartTime(LocalDateTime.parse(startDate.toString() + "T00:00:00"));
            event.setIsAllDay(true);
        }
        
        // 종료 시간 설정
        if (googleEvent.getEnd().getDateTime() != null) {
            DateTime endDateTime = googleEvent.getEnd().getDateTime();
            event.setEndTime(LocalDateTime.parse(endDateTime.toString().replace("T", " ").substring(0, 19)));
        } else {
            // 종일 이벤트
            DateTime endDate = googleEvent.getEnd().getDate();
            event.setEndTime(LocalDateTime.parse(endDate.toString() + "T00:00:00"));
        }
        
        return event;
    }
    
    public Event createGoogleCalendarEvent(String calendarId, CalendarEvent event) {
        try {
            Calendar service = getCalendarService();
            
            Event googleEvent = new Event();
            googleEvent.setSummary(event.getTitle());
            googleEvent.setDescription(event.getDescription());
            googleEvent.setLocation(event.getLocation());
            
            // 시작 시간 설정
            com.google.api.services.calendar.model.EventDateTime startDateTime = 
                new com.google.api.services.calendar.model.EventDateTime();
            String startTimeStr = event.getStartTime().toString().replace(" ", "T");
            startDateTime.setDateTime(new DateTime(startTimeStr));
            startDateTime.setTimeZone(ZoneId.systemDefault().getId());
            googleEvent.setStart(startDateTime);
            
            // 종료 시간 설정
            com.google.api.services.calendar.model.EventDateTime endDateTime = 
                new com.google.api.services.calendar.model.EventDateTime();
            String endTimeStr = event.getEndTime().toString().replace(" ", "T");
            endDateTime.setDateTime(new DateTime(endTimeStr));
            endDateTime.setTimeZone(ZoneId.systemDefault().getId());
            googleEvent.setEnd(endDateTime);
            
            return service.events().insert(calendarId, googleEvent).execute();
            
        } catch (Exception e) {
            log.error("Google Calendar 이벤트 생성 중 오류 발생", e);
            throw new RuntimeException("Google Calendar 이벤트 생성 실패", e);
        }
    }
} 