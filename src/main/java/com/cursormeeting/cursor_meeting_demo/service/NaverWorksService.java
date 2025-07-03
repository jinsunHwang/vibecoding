package com.cursormeeting.cursor_meeting_demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.cursormeeting.cursor_meeting_demo.domain.OAuthAccessToken;
import com.cursormeeting.cursor_meeting_demo.mapper.OAuthAccessTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class NaverWorksService {
    
    private static final Logger logger = LoggerFactory.getLogger(NaverWorksService.class);
    
    @Value("${naver.works.client-id}")
    private String clientId;
    
    @Value("${naver.works.client-secret}")
    private String clientSecret;
    
    @Value("${naver.works.bot-id}")
    private String botId;
    
    @Value("${naver.works.bot-secret}")
    private String botSecret;
    
    @Value("${naver.works.domain-id}")
    private String domainId;
    
    @Value("${naver.works.jwt.client-id}")
    private String jwtClientId;
    
    @Value("${naver.works.jwt.service-account}")
    private String jwtServiceAccount;
    
    @Value("${naver.works.jwt.private-key-path}")
    private String jwtPrivateKeyPath;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://www.worksapis.com";
    
    @Autowired
    private OAuthAccessTokenMapper oAuthAccessTokenMapper;
    
    /**
     * 봇 토큰 발급
     */
    public String getBotToken() {
        try {
            String url = BASE_URL + "/v1.0/bots/" + botId + "/token";
            
            logger.info("봇 토큰 발급 시도 - URL: {}", url);
            logger.info("Client ID: {}", clientId);
            logger.info("Client Secret: {}", clientSecret);
            logger.info("Bot ID: {}", botId);
            logger.info("Bot Secret: {}", botSecret);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("consumerKey", clientId);
            headers.set("consumerSecret", clientSecret);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("botSecret", botSecret);
            
            logger.info("요청 헤더: {}", headers);
            logger.info("요청 바디: {}", requestBody);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            logger.info("응답 상태: {}", response.getStatusCode());
            logger.info("응답 바디: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("accessToken");
            }
            
        } catch (Exception e) {
            logger.error("봇 토큰 발급 실패", e);
        }
        
        return null;
    }
    
    /**
     * 메시지 전송
     */
    public boolean sendMessage(String accessToken, String channelId, String content) {
        try {
            String url = BASE_URL + "/v1.0/bots/" + botId + "/channels/" + channelId + "/messages";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("content", content);
            requestBody.put("contentType", "text");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("메시지 전송 실패", e);
            return false;
        }
    }
    
    /**
     * 사용자 정보 조회
     */
    public Map<String, Object> getUserInfo(String userId) {
        try {
            String token = getBotToken();
            if (token == null) {
                return null;
            }
            
            String url = BASE_URL + "/v1.0/domains/" + domainId + "/users/" + userId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("consumerKey", clientId);
            headers.set("consumerSecret", clientSecret);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
            
        } catch (Exception e) {
            logger.error("사용자 정보 조회 실패", e);
        }
        
        return null;
    }
    
    /**
     * 캘린더 이벤트 생성
     */
    public boolean createCalendarEvent(String userId, String title, String startTime, String endTime, String location) {
        try {
            String token = getBotToken();
            if (token == null) {
                return false;
            }
            
            String url = BASE_URL + "/v1.0/domains/" + domainId + "/users/" + userId + "/calendar/events";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            headers.set("consumerKey", clientId);
            headers.set("consumerSecret", clientSecret);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", title);
            requestBody.put("startTime", startTime);
            requestBody.put("endTime", endTime);
            requestBody.put("location", location);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            logger.error("캘린더 이벤트 생성 실패", e);
            return false;
        }
    }
    
    /**
     * JWT 방식 access_token 발급 (getServerToken 스타일, assertion/파라미터/로깅 명확화)
     */
    public String getJwtAccessToken() {
        try {
            // 1. private key 파일 읽기
            String privateKeyPEM = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jwtPrivateKeyPath)));
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = java.util.Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(keySpec);

            // 2. JWT 생성 (헤더/페이로드 명확히)
            long now = System.currentTimeMillis();
            Date iat = new Date(now);
            Date exp = new Date(now + 30 * 60 * 1000); // 30분
            String jwt = Jwts.builder()
                    .setHeaderParam("alg", "RS256")
                    .setHeaderParam("typ", "JWT")
                    .setIssuer(jwtClientId)
                    .setSubject(jwtServiceAccount)
                    .setAudience("https://auth.worksmobile.com/oauth2/v2.0/token")
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .claim("scope", "bot bot.message bot.read calendar calendar.read")
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();

            // 3. 파라미터 셋팅
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("assertion", jwt);
            params.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");

            // 4. 토큰 요청
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            logger.info("[JWT] access_token 요청: {} {}", params, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://auth.worksmobile.com/oauth2/v2.0/token", request, Map.class);
            logger.info("[JWT] access_token 응답: {} {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object token = response.getBody().get("access_token");
                logger.info("[JWT] access_token 발급 성공: {}", token);
                return token != null ? token.toString() : null;
            } else {
                logger.error("[JWT] access_token 발급 실패: {}", response);
            }
        } catch (Exception e) {
            logger.error("[JWT] access_token 발급 중 예외 발생", e);
        }
        return null;
    }

    public void saveOrUpdateAccessToken(String userId, String accessToken, String refreshToken, LocalDateTime expiresAt) {
        OAuthAccessToken existing = oAuthAccessTokenMapper.selectByUserId(userId);
        OAuthAccessToken token = new OAuthAccessToken();
        token.setUserId(userId);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresAt(expiresAt);
        if (existing == null) {
            oAuthAccessTokenMapper.insert(token);
        } else {
            oAuthAccessTokenMapper.update(token);
        }
    }

    public String getAccessTokenByUserId(String userId) {
        OAuthAccessToken token = oAuthAccessTokenMapper.selectByUserId(userId);
        return token != null ? token.getAccessToken() : null;
    }
} 