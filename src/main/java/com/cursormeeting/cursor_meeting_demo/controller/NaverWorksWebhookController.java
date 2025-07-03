package com.cursormeeting.cursor_meeting_demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.cursormeeting.cursor_meeting_demo.service.NaverWorksService;

@RestController
@RequestMapping("/api/naver-works")
public class NaverWorksWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(NaverWorksWebhookController.class);
    
    @Autowired
    private NaverWorksService naverWorksService;
    
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

            if (text != null && text.startsWith("/회의 잡아줘")) {
                // JWT 방식으로 access token 발급
                String accessToken = naverWorksService.getJwtAccessToken();
                if (accessToken != null) {
                    // DB에 저장 (userId 기준)
                    naverWorksService.saveOrUpdateAccessToken(userId, accessToken, null, null);
                    naverWorksService.sendMessage(accessToken, userId, "회의가 예약되었습니다!");
                } else {
                    logger.warn("JWT access_token 발급 실패");
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
} 