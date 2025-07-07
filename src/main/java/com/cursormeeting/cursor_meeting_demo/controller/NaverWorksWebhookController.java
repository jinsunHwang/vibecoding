package com.cursormeeting.cursor_meeting_demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.cursormeeting.cursor_meeting_demo.service.NaverWorksService;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import java.util.concurrent.ConcurrentHashMap;
import com.cursormeeting.cursor_meeting_demo.service.UserService;
import com.cursormeeting.cursor_meeting_demo.domain.User;
import java.util.List;
import java.lang.StringBuilder;
import com.cursormeeting.cursor_meeting_demo.service.MeetingRoomService;
import com.cursormeeting.cursor_meeting_demo.service.MeetingService;
import com.cursormeeting.cursor_meeting_demo.dto.MeetingDto;
import com.cursormeeting.cursor_meeting_demo.domain.MeetingRoom;
import com.cursormeeting.cursor_meeting_demo.domain.Meeting;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/naver-works")
public class NaverWorksWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(NaverWorksWebhookController.class);
    
    @Autowired
    private NaverWorksService naverWorksService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MeetingRoomService meetingRoomService;
    
    @Autowired
    private MeetingService meetingService;
    
    // 대화 상태 관리용 enum 확장
    private enum ConversationState {
        WAIT_ATTENDEE, WAIT_DATE, WAIT_START, WAIT_END, WAIT_ROOM, WAIT_TYPE, WAIT_LINK, WAIT_CONFIRM, WAIT_RESELECT_ROOM, WAIT_RESELECT_TIME, COMPLETE, INIT
    }
    // userId별 대화 상태 및 입력값 저장 (메모리 기반)
    private final Map<String, ConversationState> userStateMap = new ConcurrentHashMap<>();
    private final Map<String, String> userAttendeeMap = new ConcurrentHashMap<>();
    private final Map<String, String> userStartMap = new ConcurrentHashMap<>();
    private final Map<String, String> userEndMap = new ConcurrentHashMap<>();
    private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();
    private final Map<String, String> userTypeMap = new ConcurrentHashMap<>();
    private final Map<String, String> userMeetingLinkMap = new ConcurrentHashMap<>();
    private final Map<String, String> userDateMap = new ConcurrentHashMap<>();

    // 회의실/유형 예시 데이터
    private static final String[] MEETING_ROOMS = {"A룸", "B룸", "C룸"};
    private static final String[] MEETING_TYPES = {"IN_PERSON", "ONLINE", "HYBRID"};
    
    // 회의 유형 영어 코드를 한글 라벨로 변환하는 메서드
    private String getMeetingTypeLabel(String typeCode) {
        switch(typeCode) {
            case "IN_PERSON": return "대면 회의";
            case "ONLINE": return "온라인 회의";
            case "HYBRID": return "하이브리드";
            default: return typeCode;
        }
    }

    private void resetUserState(String userId) {
        userStateMap.put(userId, ConversationState.INIT);
        userAttendeeMap.remove(userId);
        userStartMap.remove(userId);
        userEndMap.remove(userId);
        userRoomMap.remove(userId);
        userTypeMap.remove(userId);
        userMeetingLinkMap.remove(userId);
        userDateMap.remove(userId);
    }

    /**
     * 네이버웍스 웹훅 검증을 위한 엔드포인트
     */
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(@RequestParam("challenge") String challenge) {
        logger.info("네이버웍스 웹훅 검증 요청: {}", challenge);
        return ResponseEntity.ok(challenge);
    }
    
    /**
     * 네이버웍스에서 보내는 이벤트를 처리하는 엔드포인트
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        logger.info("네이버웍스 웹훅 수신: {}", payload);
        
        try {
            // 이벤트 타입 확인
            String eventType = (String) payload.get("type");
            if (eventType == null) {
                logger.warn("eventType(type) 필드가 null입니다. payload: {}", payload);
                return ResponseEntity.ok("OK");
            }
            
            switch (eventType) {
                case "message":
                    return handleMessageEvent(payload);
                case "bot":
                    return handleBotEvent(payload);
                default:
                    logger.info("처리되지 않은 이벤트 타입: {}", eventType);
                    return ResponseEntity.ok("OK");
            }
            
        } catch (Exception e) {
            logger.error("웹훅 처리 중 오류 발생", e);
            return ResponseEntity.ok("OK"); // 네이버웍스는 항상 200 응답을 기대
        }
    }
    
    /**
     * 메시지 이벤트 처리
     */
    private ResponseEntity<String> handleMessageEvent(Map<String, Object> payload) {
        logger.info("메시지 이벤트 처리");
        Map<String, Object> source = (Map<String, Object>) payload.get("source");
        Map<String, Object> content = (Map<String, Object>) payload.get("content");
        if (source != null && content != null) {
            String userId = (String) source.get("userId");
            String text = (String) content.get("text");
            logger.info("사용자 {}: {}", userId, text);
            ConversationState state = userStateMap.getOrDefault(userId, ConversationState.INIT);

            // 초기화 명령
            if ("초기화".equals(text)) {
                resetUserState(userId);
                naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "회의 예약이 초기화되었습니다. 다시 /회의 잡아줘 를 입력하세요.");
                return ResponseEntity.ok("OK");
            }

            // 1. /회의 잡아줘
            if (text != null && text.startsWith("/회의 잡아줘")) {
                // 참석자 선택(버튼)
                List<User> userList = userService.getAllUsers();
                Map<String, Object> flexContent = new HashMap<>();
                flexContent.put("type", "flex");
                flexContent.put("altText", "참석자를 선택하세요");
                Map<String, Object> contents = new HashMap<>();
                contents.put("type", "bubble");
                Map<String, Object> body = new HashMap<>();
                body.put("type", "box");
                body.put("layout", "vertical");
                List<Object> bodyContents = new java.util.ArrayList<>();
                Map<String, Object> title = new HashMap<>();
                title.put("type", "text");
                title.put("text", "참석자를 선택하세요 (여러 명 선택 가능)");
                bodyContents.add(title);
                for (User u : userList) {
                    Map<String, Object> btn = new HashMap<>();
                    btn.put("type", "button");
                    Map<String, Object> action = new HashMap<>();
                    String emailShort = u.getEmail();
                    if (emailShort.length() > 10) emailShort = emailShort.substring(0, 10) + "...";
                    String label = u.getName() + " (" + emailShort + ")";
                    if (label.length() > 20) label = label.substring(0, 20);
                    action.put("type", "message");
                    action.put("label", label);
                    action.put("text", "참석자추가:" + u.getEmail());
                    btn.put("action", action);
                    bodyContents.add(btn);
                }
                // 선택 완료 버튼
                Map<String, Object> doneBtn = new HashMap<>();
                doneBtn.put("type", "button");
                Map<String, Object> doneAction = new HashMap<>();
                doneAction.put("type", "message");
                doneAction.put("label", "선택 완료");
                doneAction.put("text", "참석자선택완료");
                doneBtn.put("action", doneAction);
                bodyContents.add(doneBtn);
                body.put("contents", bodyContents);
                contents.put("body", body);
                flexContent.put("contents", contents);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("content", flexContent);
                naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                userStateMap.put(userId, ConversationState.WAIT_ATTENDEE);
                userAttendeeMap.put(userId, "");
                return ResponseEntity.ok("OK");
            }

            // 2. 참석자 추가/선택 완료
            if (state == ConversationState.WAIT_ATTENDEE) {
                if (text != null && text.startsWith("참석자추가:")) {
                    String email = text.replace("참석자추가:", "").trim();
                    String prev = userAttendeeMap.getOrDefault(userId, "");
                    // 중복 방지
                    String[] prevArr = prev.isEmpty() ? new String[]{} : prev.split(",");
                    boolean already = false;
                    for (String e : prevArr) if (e.equals(email)) already = true;
                    if (!already) {
                        if (!prev.isEmpty()) prev += ",";
                        prev += email;
                        userAttendeeMap.put(userId, prev);
                        naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "현재까지 선택된 참석자: " + userAttendeeMap.get(userId));
                    }
                    return ResponseEntity.ok("OK");
                } else if ("참석자선택완료".equals(text)) {
                    userStateMap.put(userId, ConversationState.WAIT_DATE);
                    naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "회의 날짜를 입력하세요 (예: YYYY-MM-DD)");
                    return ResponseEntity.ok("OK");
                }
            }

            // 2-1. 날짜 입력
            if (state == ConversationState.WAIT_DATE) {
                userDateMap.put(userId, text);
                userStateMap.put(userId, ConversationState.WAIT_START);
                naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "시작시간을 입력하세요 (예: 10:00)");
                return ResponseEntity.ok("OK");
            }

            // 3. 시작시간 입력
            if (state == ConversationState.WAIT_START) {
                userStartMap.put(userId, text);
                userStateMap.put(userId, ConversationState.WAIT_END);
                naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "종료시간을 입력하세요 (예: 11:00)");
                return ResponseEntity.ok("OK");
            }

            // 4. 종료시간 입력
            if (state == ConversationState.WAIT_END) {
                userEndMap.put(userId, text);
                userStateMap.put(userId, ConversationState.WAIT_ROOM);
                // 실제 DB에서 회의실 목록 조회
                List<MeetingRoom> rooms = meetingRoomService.getAllMeetingRooms();
                Map<String, Object> flexContent = new HashMap<>();
                flexContent.put("type", "flex");
                flexContent.put("altText", "회의실을 선택하세요");
                Map<String, Object> contents = new HashMap<>();
                contents.put("type", "bubble");
                Map<String, Object> body = new HashMap<>();
                body.put("type", "box");
                body.put("layout", "vertical");
                List<Object> bodyContents = new java.util.ArrayList<>();
                Map<String, Object> title = new HashMap<>();
                title.put("type", "text");
                title.put("text", "회의실을 선택하세요");
                bodyContents.add(title);
                for (MeetingRoom room : rooms) {
                    Map<String, Object> btn = new HashMap<>();
                    btn.put("type", "button");
                    Map<String, Object> action = new HashMap<>();
                    action.put("type", "message");
                    String label = room.getName();
                    if (label.length() > 20) label = label.substring(0, 20);
                    action.put("label", label);
                    action.put("text", "회의실선택:" + room.getName());
                    btn.put("action", action);
                    bodyContents.add(btn);
                }
                body.put("contents", bodyContents);
                contents.put("body", body);
                flexContent.put("contents", contents);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("content", flexContent);
                naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                return ResponseEntity.ok("OK");
            }

            // 5. 회의실 선택
            if (state == ConversationState.WAIT_ROOM && text != null && text.startsWith("회의실선택:")) {
                String roomName = text.replace("회의실선택:", "");
                userRoomMap.put(userId, roomName);
                userStateMap.put(userId, ConversationState.WAIT_TYPE);
                // 회의 유형 버튼
                Map<String, Object> flexContent = new HashMap<>();
                flexContent.put("type", "flex");
                flexContent.put("altText", "회의 유형을 선택하세요");
                Map<String, Object> contents = new HashMap<>();
                contents.put("type", "bubble");
                Map<String, Object> body = new HashMap<>();
                body.put("type", "box");
                body.put("layout", "vertical");
                List<Object> bodyContents = new java.util.ArrayList<>();
                Map<String, Object> title = new HashMap<>();
                title.put("type", "text");
                title.put("text", "회의 유형을 선택하세요");
                bodyContents.add(title);
                for (String type : MEETING_TYPES) {
                    Map<String, Object> btn = new HashMap<>();
                    btn.put("type", "button");
                    Map<String, Object> action = new HashMap<>();
                    action.put("type", "message");
                    action.put("label", getMeetingTypeLabel(type));
                    action.put("text", "회의유형선택:" + type);
                    btn.put("action", action);
                    bodyContents.add(btn);
                }
                body.put("contents", bodyContents);
                contents.put("body", body);
                flexContent.put("contents", contents);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("content", flexContent);
                naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                return ResponseEntity.ok("OK");
            }

            // 6. 회의 유형 선택
            if (state == ConversationState.WAIT_TYPE && text != null && text.startsWith("회의유형선택:")) {
                String type = text.replace("회의유형선택:", "");
                userTypeMap.put(userId, type);
                
                // 선택한 회의 유형을 한글로 알려주기
                String typeLabel = getMeetingTypeLabel(type);
                naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "선택한 회의 유형: " + typeLabel);
                
                if (type.equals("ONLINE") || type.equals("HYBRID")) {
                    userStateMap.put(userId, ConversationState.WAIT_LINK);
                    naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "회의 링크를 입력하세요 (없으면 '없음' 입력)");
                    return ResponseEntity.ok("OK");
                } else {
                    userMeetingLinkMap.put(userId, "");
                    userStateMap.put(userId, ConversationState.WAIT_CONFIRM);
                }
                // 예/아니오 버튼
                Map<String, Object> flexContent = new HashMap<>();
                flexContent.put("type", "flex");
                flexContent.put("altText", "회의를 생성하시겠습니까?");
                Map<String, Object> contents = new HashMap<>();
                contents.put("type", "bubble");
                Map<String, Object> body = new HashMap<>();
                body.put("type", "box");
                body.put("layout", "vertical");
                List<Object> bodyContents = new java.util.ArrayList<>();
                Map<String, Object> title = new HashMap<>();
                title.put("type", "text");
                title.put("text", "회의를 생성하시겠습니까?");
                bodyContents.add(title);
                // 예 버튼
                Map<String, Object> yesBtn = new HashMap<>();
                yesBtn.put("type", "button");
                Map<String, Object> yesAction = new HashMap<>();
                yesAction.put("type", "message");
                yesAction.put("label", "예");
                yesAction.put("text", "회의생성:예");
                yesBtn.put("action", yesAction);
                bodyContents.add(yesBtn);
                // 아니오 버튼
                Map<String, Object> noBtn = new HashMap<>();
                noBtn.put("type", "button");
                Map<String, Object> noAction = new HashMap<>();
                noAction.put("type", "message");
                noAction.put("label", "아니오");
                noAction.put("text", "회의생성:아니오");
                noBtn.put("action", noAction);
                bodyContents.add(noBtn);
                body.put("contents", bodyContents);
                contents.put("body", body);
                flexContent.put("contents", contents);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("content", flexContent);
                naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                return ResponseEntity.ok("OK");
            }

            // 6-1. 회의 링크 입력
            if (state == ConversationState.WAIT_LINK) {
                userMeetingLinkMap.put(userId, text);
                userStateMap.put(userId, ConversationState.WAIT_CONFIRM);
                // 예/아니오 버튼(동일)
                Map<String, Object> flexContent = new HashMap<>();
                flexContent.put("type", "flex");
                flexContent.put("altText", "회의를 생성하시겠습니까?");
                Map<String, Object> contents = new HashMap<>();
                contents.put("type", "bubble");
                Map<String, Object> body = new HashMap<>();
                body.put("type", "box");
                body.put("layout", "vertical");
                List<Object> bodyContents = new java.util.ArrayList<>();
                Map<String, Object> title = new HashMap<>();
                title.put("type", "text");
                title.put("text", "회의를 생성하시겠습니까?");
                bodyContents.add(title);
                // 예 버튼
                Map<String, Object> yesBtn = new HashMap<>();
                yesBtn.put("type", "button");
                Map<String, Object> yesAction = new HashMap<>();
                yesAction.put("type", "message");
                yesAction.put("label", "예");
                yesAction.put("text", "회의생성:예");
                yesBtn.put("action", yesAction);
                bodyContents.add(yesBtn);
                // 아니오 버튼
                Map<String, Object> noBtn = new HashMap<>();
                noBtn.put("type", "button");
                Map<String, Object> noAction = new HashMap<>();
                noAction.put("type", "message");
                noAction.put("label", "아니오");
                noAction.put("text", "회의생성:아니오");
                noBtn.put("action", noAction);
                bodyContents.add(noBtn);
                body.put("contents", bodyContents);
                contents.put("body", body);
                flexContent.put("contents", contents);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("content", flexContent);
                naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                return ResponseEntity.ok("OK");
            }

            // 7. 예/아니오 처리
            if (state == ConversationState.WAIT_CONFIRM && text != null && text.startsWith("회의생성:")) {
                if (text.endsWith("예")) {
                    try {
                        MeetingDto dto = new MeetingDto();
                        dto.setTitle("네이버웍스 예약 회의");
                        dto.setDescription("Bot 예약");
                        Long organizerId = null;
                        List<User> users = userService.getAllUsers();
                        for (User u : users) {
                            if (u.getEmail().equals(userId)) {
                                organizerId = u.getId();
                                break;
                            }
                        }
                        if (organizerId == null && !users.isEmpty()) organizerId = users.get(0).getId();
                        dto.setOrganizerId(organizerId);
                        // roomName → roomId 변환
                        String roomName = userRoomMap.get(userId);
                        List<MeetingRoom> allRooms = meetingRoomService.getAllMeetingRooms();
                        Long roomId = null;
                        for (MeetingRoom r : allRooms) {
                            if (r.getName().equals(roomName)) {
                                roomId = r.getId();
                                break;
                            }
                        }
                        if (roomId == null) throw new RuntimeException("회의실을 찾을 수 없습니다.");
                        dto.setRoomId(roomId);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        String date = userDateMap.get(userId);
                        LocalDateTime start = LocalDateTime.parse(date + " " + userStartMap.get(userId), formatter);
                        LocalDateTime end = LocalDateTime.parse(date + " " + userEndMap.get(userId), formatter);
                        dto.setStartTime(start);
                        dto.setEndTime(end);
                        dto.setType(userTypeMap.get(userId));
                        dto.setMeetingLink(userMeetingLinkMap.getOrDefault(userId, ""));
                        List<Long> participantIds = new java.util.ArrayList<>();
                        String[] emails = userAttendeeMap.get(userId).split(",");
                        for (String email : emails) {
                            for (User u : users) {
                                if (u.getEmail().equals(email)) participantIds.add(u.getId());
                            }
                        }
                        dto.setParticipantIds(participantIds);
                        // 중복 체크(회의실, 시간)
                        List<Meeting> conflicts = meetingService.getConflicts(roomId, start, end);
                        if (!conflicts.isEmpty()) throw new RuntimeException("이미 해당 시간에 회의가 존재합니다.");
                        meetingService.createMeeting(dto);
                        userStateMap.put(userId, ConversationState.COMPLETE);
                        naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "회의가 성공적으로 생성되었습니다!\n참석자: " + userAttendeeMap.get(userId) + "\n날짜: " + userDateMap.get(userId) + "\n시작: " + userStartMap.get(userId) + "\n종료: " + userEndMap.get(userId) + "\n회의실: " + userRoomMap.get(userId) + "\n유형: " + getMeetingTypeLabel(userTypeMap.get(userId)) + "\n링크: " + (userMeetingLinkMap.getOrDefault(userId, "").isEmpty() ? "없음" : userMeetingLinkMap.getOrDefault(userId, "")));
                        resetUserState(userId);
                        return ResponseEntity.ok("OK");
                    } catch (Exception e) {
                        userStateMap.put(userId, ConversationState.WAIT_RESELECT_ROOM);
                        naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "이미 해당 시간에 회의가 존재합니다. 회의실 선택 또는 시간 선택을 다시 해주세요.\n[회의실 재선택] [시간 재선택]");
                        return ResponseEntity.ok("OK");
                    }
                } else {
                    // 아니오: 초기화
                    resetUserState(userId);
                    naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "회의 예약이 취소되었습니다. 다시 /회의 잡아줘 를 입력하세요.");
                    return ResponseEntity.ok("OK");
                }
            }

            // 8. 회의실/시간 재선택 분기(임시)
            if (state == ConversationState.WAIT_RESELECT_ROOM) {
                if ("회의실 재선택".equals(text)) {
                    userStateMap.put(userId, ConversationState.WAIT_ROOM);
                    // 회의실 목록 Flex 메시지 재전송(위와 동일)
                    Map<String, Object> flexContent = new HashMap<>();
                    flexContent.put("type", "flex");
                    flexContent.put("altText", "회의실을 선택하세요");
                    Map<String, Object> contents = new HashMap<>();
                    contents.put("type", "bubble");
                    Map<String, Object> body = new HashMap<>();
                    body.put("type", "box");
                    body.put("layout", "vertical");
                    List<Object> bodyContents = new java.util.ArrayList<>();
                    Map<String, Object> title = new HashMap<>();
                    title.put("type", "text");
                    title.put("text", "회의실을 선택하세요");
                    bodyContents.add(title);
                    for (String room : MEETING_ROOMS) {
                        Map<String, Object> btn = new HashMap<>();
                        btn.put("type", "button");
                        Map<String, Object> action = new HashMap<>();
                        action.put("type", "message");
                        action.put("label", room);
                        action.put("text", "회의실선택:" + room);
                        btn.put("action", action);
                        bodyContents.add(btn);
                    }
                    body.put("contents", bodyContents);
                    contents.put("body", body);
                    flexContent.put("contents", contents);
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("content", flexContent);
                    naverWorksService.sendFlexMessage(naverWorksService.getJwtAccessToken(userId), userId, requestBody);
                    return ResponseEntity.ok("OK");
                } else if ("시간 재선택".equals(text)) {
                    userStateMap.put(userId, ConversationState.WAIT_START);
                    naverWorksService.sendMessage(naverWorksService.getJwtAccessToken(userId), userId, "시작시간을 입력하세요 (예: 10:00)");
                    return ResponseEntity.ok("OK");
                }
            }
        }
        return ResponseEntity.ok("OK");
    }
    
    /**
     * 봇 이벤트 처리
     */
    private ResponseEntity<String> handleBotEvent(Map<String, Object> payload) {
        logger.info("봇 이벤트 처리");
        return ResponseEntity.ok("OK");
    }
    
    /**
     * 회의 요청 처리
     */
    private ResponseEntity<String> handleMeetingRequest(String content, String userId) {
        logger.info("회의 요청 처리: {} (사용자: {})", content, userId);
        
        // TODO: 실제 회의 생성 로직 구현
        // 1. 사용자 정보 조회
        // 2. 가능한 시간대 확인
        // 3. 회의실 예약
        // 4. 회의 생성
        // 5. 응답 메시지 전송
        
        return ResponseEntity.ok("OK");
    }

    /**
     * JWT 토큰 발급 테스트 엔드포인트 (포스트맨 테스트용)
     */
    @GetMapping("/jwt-token")
    public ResponseEntity<Map<String, Object>> getJwtToken(@RequestParam("userId") String userId) {
        logger.info("JWT 토큰 발급 요청 (userId: {})", userId);
        try {
            String accessToken = naverWorksService.getJwtAccessToken(userId);
            if (accessToken != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("access_token", accessToken);
                response.put("token_type", "Bearer");
                response.put("message", "JWT 토큰 발급 성공");
                logger.info("JWT 토큰 발급 성공");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "JWT 토큰 발급 실패");
                logger.error("JWT 토큰 발급 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            logger.error("JWT 토큰 발급 중 예외 발생", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "JWT 토큰 발급 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 봇 토큰 발급 테스트 엔드포인트 (포스트맨 테스트용)
     */
    @GetMapping("/bot-token")
    public ResponseEntity<Map<String, Object>> getBotToken() {
        logger.info("봇 토큰 발급 요청");
        
        try {
            String accessToken = naverWorksService.getBotToken();
            
            if (accessToken != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("access_token", accessToken);
                response.put("token_type", "Bearer");
                response.put("message", "봇 토큰 발급 성공");
                
                logger.info("봇 토큰 발급 성공");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "봇 토큰 발급 실패");
                
                logger.error("봇 토큰 발급 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("봇 토큰 발급 중 예외 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "봇 토큰 발급 중 오류 발생: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


} 