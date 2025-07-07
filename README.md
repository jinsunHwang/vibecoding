# Cursor Meeting Demo (네이버웍스 챗봇 & 웹 회의실 예약 시스템)

## 프로젝트 개요

이 프로젝트는 네이버웍스 챗봇과 웹 기반 회의실 예약 시스템을 통합한 서비스입니다. 사용자는 네이버웍스 챗봇 또는 웹 UI를 통해 회의실 예약, 참석자 관리, 시간 추천, 회의 생성 등을 손쉽게 처리할 수 있습니다.

## 주요 기능

- **네이버웍스 챗봇 연동**: 네이버웍스 메시지/버튼/Flex 메시지 기반 대화형 회의 예약
- **JWT 기반 인증**: 네이버웍스 API 연동을 위한 JWT 토큰 발급 및 관리
- **OAuth2 인증**: 사용자 인증 및 토큰 관리
- **회의실/회의 예약**: 웹 UI 및 챗봇 모두에서 회의실 예약, 중복 체크, 참석자 관리, 회의 유형(대면/온라인/하이브리드) 지원
- **추천 시간대**: 참석자/회의실/선호시간대 기반 추천 시간대 자동 제안
- **DB 기반 관리**: 사용자, 회의실, 회의, 토큰 등 모든 정보 DB 관리
- **관리자/사용자 권한 분리**
- **캘린더/회의실 현황/내 회의/빠른 예약 등 다양한 웹 UI 제공**

## 기술스택

- **Backend**: Java 17, Spring Boot, Spring Security, MyBatis, JWT, MariaDB
- **Frontend**: HTML5, CSS3, Bootstrap, FullCalendar, JavaScript(ES6), Axios
- **네이버웍스 API**: 챗봇, 메시지, Flex 메시지, JWT 인증, 캘린더, 사용자 정보 등
- **AI 활용**: OpenAI GPT 기반 프롬프트 설계 및 대화형 UX 개선, 코드 자동화, 추천 로직 설계 지원

## AI 활용 내역

- 네이버웍스 챗봇 대화 플로우 설계 및 Flex 메시지 UX 개선
- 회의실/시간 추천 알고리즘 설계 및 코드 자동화
- JWT/OAuth2 인증 구조 설계 및 예외처리 자동화
- 프론트엔드 JS/HTML 자동화 및 UX 개선
- 주요 프롬프트/대화 설계 및 문서화

## 폴더 구조

- `src/main/java/com/cursormeeting/cursor_meeting_demo/controller/` : REST API, 챗봇, 인증 등 컨트롤러
- `src/main/java/com/cursormeeting/cursor_meeting_demo/service/` : 비즈니스 로직, 네이버웍스/회의/유저/토큰 서비스
- `src/main/java/com/cursormeeting/cursor_meeting_demo/domain/` : 엔티티/도메인 모델
- `src/main/java/com/cursormeeting/cursor_meeting_demo/mapper/` : MyBatis 매퍼
- `src/main/resources/mapper/` : MyBatis XML 매퍼
- `src/main/resources/templates/` : 웹 프론트엔드(HTML)
- `src/main/resources/static/` : 정적 리소스
- `src/main/resources/application.properties` : 환경설정

## 실행 방법

1. MariaDB 등 DB 준비 및 application.properties 환경설정
2. `./gradlew build` 후 실행 또는 IDE에서 Spring Boot 실행
3. 웹: https://localhost:8443 접속, 챗봇: 네이버웍스에서 연동

### ngrok 실행 방법 (네이버웍스 웹훅용)

**Windows:**
```bash
cd ngrok
.\ngrok.exe http https://localhost:8443
```

**Mac/Linux:**
```bash
cd ngrok
./ngrok http https://localhost:8443
```

**주의사항:**
- Spring Boot 애플리케이션이 먼저 실행되어야 함
- ngrok URL이 생성되면 네이버웍스 웹훅 설정에서 업데이트 필요
- 무료 버전은 세션 시간 제한이 있으며, 새 세션마다 URL이 변경될 수 있음

**네이버웍스 Bot 설정:**
1. [Naver Works Developer](https://developers.worksmobile.com/) 접속
2. Bot 설정 → Callback URL에 `{ngrok_url}/api/naver-works/webhook` 입력
   - 예: `https://abc123.ngrok.io/api/naver-works/webhook`

## 주요 참고/문서
- 네이버웍스 공식 API 문서
- OpenAI GPT 프롬프트 설계

---

본 프로젝트는 AI(예: ChatGPT) 기반 코드 자동화, 대화형 UX 설계, 프롬프트 기반 챗봇 설계 등 최신 AI 활용 사례를 적극 반영하였습니다.