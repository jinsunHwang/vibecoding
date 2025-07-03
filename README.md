📌 프로젝트 전체 설명
프로젝트명:
vibe coding 회의 예약 시스템
개요:
이 프로젝트는 사내 회의실 예약과 회의 일정 관리를 위한 웹 기반 시스템입니다.
Spring Boot를 기반으로 하며, 사용자는 웹에서 회의실을 예약하고, 회의 일정을 효율적으로 관리할 수 있습니다.
주요 기능:
회의실 예약 및 일정 등록/수정/삭제
회의실, 사용자, 팀 정보 관리
회의 일정 자동화 및 알림
관리자/일반 사용자 권한 분리
RESTful API 제공
MariaDB/MyBatis 기반 데이터 관리
HTML+Bootstrap 기반의 간단한 웹 UI
기술 스택:
Java 17, Spring Boot
MyBatis, MariaDB
Gradle
HTML, Bootstrap
JWT 인증(서버-서버 방식)
기타: 로그, 예외처리, 보안 설정 등
🤖 AI 활용 내역
1. 코드 자동 생성 및 리팩토링
Spring Controller, Service, Mapper, Domain 등 주요 레이어의 CRUD 코드 자동화
반복적인 DTO/Entity 변환, 예외처리, 로깅 코드 자동 생성
DB 테이블 설계 및 MyBatis XML Mapper 자동화
2. 문서 및 README 자동 작성
프로젝트 구조, 주요 기능, 사용법 등 문서화 자동화
README, 사용법, 기술 스택 등 설명문 자동 생성
3. 에러 진단 및 해결 가이드
빌드/런타임 에러 발생 시 AI 기반 원인 분석 및 해결 방법 제시
Spring Security, DB 연결, 외부 API 연동 등에서 발생하는 문제의 원인 분석 및 코드 수정
4. API 설계 및 테스트 자동화
REST API 명세 자동 생성 및 테스트 코드 보조
API 파라미터, 응답 구조 설계 자동화
5. 코드 구조 개선 및 불필요 코드 정리
불필요한 OAuth2/SSO 인증 코드 제거
JWT 방식 인증으로 구조 단순화 및 보안 강화
6. 커밋/배포 자동화 안내
Git 커밋, push, GitHub 업로드 자동화 명령어 안내
.gitignore, 프로젝트 정리 등 실무적 배포 팁 제공
📝 요약
이 프로젝트는 사내 회의실 예약 및 일정 관리를 위한 실전형 Spring Boot 웹앱입니다.
개발 전 과정에서 AI 코딩 어시스턴트(예: ChatGPT, Copilot 등)를 적극 활용하여
코드 자동화, 문서화, 에러 진단, 구조 개선, 배포까지 빠르고 효율적으로 완성하였습니다.
네이버웍스 등 외부 서비스 연동 없이, 순수 사내 시스템으로 동작하도록 설계되어 있습니다.
