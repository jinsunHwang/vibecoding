# 프로젝트 개발 과정 주요 프롬프트 정리

## 프로젝트 요구사항

### 기본 프로젝트 설정
- "Spring Boot와 MyBatis를 Gradle 방식으로 회의실 예약 시스템을 만들어줘"
- "Java 기반으로 팀원들의 캘린더 일정을 분석해서 회의 시간을 자동으로 제안하는 웹앱을 개발하고 싶어"
- "MariaDB를 사용해서 회의실 이중 예약 방지 기능이 필요해"
- "Spring Security와 JWT를 사용해서 사용자 인증과 팀별 계정 관리 기능을 구현해줘"

### 데이터베이스 설계
- "users, teams, meeting_rooms, meetings, meeting_participants, room_equipment 테이블 구조를 설계해줘"
- "회의 참석자 관계 테이블(meeting_participants)이 필요할 것 같은데 어떻게 설계할까?"
- "회의실에 장비 정보도 저장하고 싶어 (JSON 형태로 equipment 필드에 저장)"
- "회의 유형(IN_PERSON/ONLINE/HYBRID)을 ENUM으로 구분해서 저장하고 싶어"
- "JWT 토큰과 OAuth 액세스 토큰을 저장할 테이블도 필요해"

### 웹 인터페이스 개발
- "Bootstrap을 사용해서 모던한 UI를 만들어줘"
- "Google Calendar의 FullCalendar를 사용해서 회의 일정을 시각화하고 싶어"
- "달력을 클릭해서 회의를 예약할 수 있게 해줘 (eventClick 핸들러 구현)"
- "회의실 현황을 실시간으로 보여주는 페이지가 필요해 (30초마다 자동 새로고침)"
- "가능한 시간 찾기 기능을 구현해줘 (참석자들의 공통 빈 시간 계산)"

### 회의 예약 로직
- "회의실 중복 예약을 방지하는 로직을 구현해줘 (findConflicts 쿼리)"
- "참석자들의 공통 빈 시간을 계산하는 알고리즘이 필요해 (MeetingSuggestionService)"
- "회의 시간 추천 기능을 만들어줘 (오전/오후 선호도 반영)"
- "회의 생성 시 참석자들에게 알림을 보내고 싶어 (Spring Mail 사용)"

### 네이버웍스 연동
- "네이버웍스 챗봇을 만들어서 회의 예약을 할 수 있게 해줘"
- "JWT 토큰 방식으로 인증을 구현하고 싶어 (RS256 알고리즘 사용)"
- "봇 토큰 발급이 안되는데 어떻게 해결할까? (consumerKey, consumerSecret 헤더 문제)"
- "웹훅 검증 엔드포인트(/webhook)를 구현해줘"

### 챗봇 개발 과정
- "챗봇에서 대화형으로 회의 예약을 할 수 있게 해줘 (ConversationState enum 사용)"
- "참석자 선택, 날짜 선택, 시간 선택, 회의실 선택 순서로 진행되게 해줘"
- "Flex 메시지로 버튼을 만들어서 사용자 경험을 개선하고 싶어"
- "회의 유형에 따라 회의 링크 입력을 추가해줘 (ONLINE/HYBRID일 때만)"

### 오류 해결 과정
- "400 Bad Request 에러가 발생하는데 어떻게 해결할까? (Request Body 구조 문제)"
- "Flex 메시지에서 action.type이 누락되었다는 오류가 나와 (action 객체 구조 수정)"
- "JWT 토큰 발급 시 필요한 파라미터가 누락된 것 같아 (client_id, client_secret, scope)"
- "회의 생성 시 DB type 컬럼에서 데이터 truncated 오류가 발생해 (ENUM 타입 매칭)"
- "회의 유형 코드와 라벨이 일치하지 않아서 문제가 생겨 (IN_PERSON ↔ 대면 회의)"

### 웹 프론트엔드 개선
- "추천 시간대를 선택했을 때 회의 예약 폼에 자동으로 반영되게 해줘 (selectTimeSlot 함수)"
- "datetime-local input 포맷 문제가 있어서 시간이 제대로 입력되지 않아 (toDatetimeLocalString 함수)"
- "회의 유형이 영어 코드로 저장되는데 한글로 표시되게 해줘 (getMeetingTypeLabel 함수)"
- "참석자 선택은 잘 되는데 시작시간, 종료시간이 적용되지 않아 (ISO 문자열 변환 문제)"

### 최종 완성 기능
- "네이버웍스 챗봇을 통한 대화형 회의 예약 플로우 완성"
- "웹 기반 회의실 예약 시스템 완성"
- "회의실 예약, 참석자 관리, 시간 추천, 회의 생성 등 통합 서비스 완성"
- "네이버웍스 Bot의 UI 한계를 고려한 대안 구현 완료"

## 주요 개발 이슈 및 해결 과정

### 인증 시스템
- JWT 방식으로 구현 (RS256 알고리즘, 30분 만료)
- 토큰 발급 및 만료 시 재발급 로직 구현 (upsert 방식)
- DB 기반 토큰 저장 및 관리 (jwt_token 테이블)

### 메시지 전송
- 네이버웍스 API 요청 구조 오류 해결 (content 객체 구조)
- Flex 메시지 action.type 누락 문제 해결 (action 객체에 type 필드 추가)
- 공식 문서에 맞는 JSON 구조로 수정

### 대화형 플로우
- 단계별 상태 관리 (참석자→날짜→시간→회의실→유형→확인)
- 메모리 기반 Map으로 사용자 상태 관리 (ConcurrentHashMap 사용)
- 중복 선택 방지 및 사용자 안내 메시지 구현

### 데이터 연동
- 회의실 목록 DB 조회 및 버튼화 (MeetingRoomService 활용)
- 회의실 이름→roomId 변환 로직 (실제 DB 데이터 기반)
- 실제 DB 기준 중복 체크 구현 (findConflicts 쿼리)

### 프론트엔드 연동
- 회의 유형 코드/라벨 일치화 (IN_PERSON/ONLINE/HYBRID ↔ 대면/온라인/하이브리드)
- 추천 시간대 선택 시 폼 자동 반영 (selectTimeSlot 함수)
- datetime-local 포맷 변환 함수 구현 (toDatetimeLocalString)

## 최종 완성된 시스템

### 네이버웍스 챗봇
- "/회의 잡아줘" 명령어로 시작
- 단계별 대화형 회의 예약 플로우 (7단계)
- Flex 메시지를 활용한 직관적인 UI
- 회의실, 시간, 참석자 중복 체크

### 웹 인터페이스
- 모던한 Bootstrap 기반 UI
- Google Calendar의 FullCalendar를 활용한 시각적 일정 관리
- 실시간 회의실 현황 표시 (30초 자동 새로고침)
- 가능한 시간 찾기 및 추천 기능

### 백엔드 시스템
- Spring Boot 기반 RESTful API
- MyBatis를 활용한 데이터베이스 연동
- JWT 기반 인증 시스템 (RS256 알고리즘)
- 회의실 중복 예약 방지 로직

### 통합 기능
- 네이버웍스 챗봇과 웹 인터페이스 연동
- 회의실 예약, 참석자 관리, 시간 추천
- 회의 생성 및 취소 기능
- 실시간 상태 업데이트

## 기술 스택

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Build Tool**: Gradle
- **Database**: MariaDB
- **ORM**: MyBatis
- **Security**: Spring Security + JWT
- **Mail**: Spring Mail (Gmail SMTP)

### Frontend
- **UI Framework**: Bootstrap
- **Calendar**: FullCalendar
- **HTTP Client**: Axios
- **Template Engine**: Thymeleaf

### External APIs
- **Chat Platform**: Naver Works Bot API
- **Calendar**: Google Calendar API
- **Authentication**: JWT (RS256)

### Infrastructure
- **Server**: Spring Boot Embedded Tomcat (Port 8443)
- **SSL**: HTTPS with PKCS12 keystore
- **Logging**: SLF4J with DEBUG level

--- 