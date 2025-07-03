-- 테스트용 사용자 데이터
INSERT INTO users (email, password, name, phone_number, role, team_id, google_calendar_id, outlook_calendar_id, created_at, updated_at) VALUES
('admin@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '관리자', '010-1234-5678', 'ADMIN', 1, NULL, NULL, NOW(), NOW()),
('user1@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '김철수', '010-1111-1111', 'USER', 1, NULL, NULL, NOW(), NOW()),
('user2@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '이영희', '010-2222-2222', 'USER', 1, NULL, NULL, NOW(), NOW()),
('user3@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '박민수', '010-3333-3333', 'USER', 2, NULL, NULL, NOW(), NOW()),
('user4@company.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '최지영', '010-4444-4444', 'USER', 2, NULL, NULL, NOW(), NOW());

-- 테스트용 팀 데이터
INSERT INTO teams (name, description, created_at, updated_at) VALUES
('개발팀', '소프트웨어 개발 담당', NOW(), NOW()),
('디자인팀', 'UI/UX 디자인 담당', NOW(), NOW()),
('기획팀', '서비스 기획 및 전략', NOW(), NOW());

-- 테스트용 회의실 데이터
INSERT INTO meeting_rooms (name, location, capacity, equipment, is_available, created_at, updated_at) VALUES
('회의실 A', '1층', 8, '["projector", "whiteboard"]', true, NOW(), NOW()),
('회의실 B', '2층', 12, '["projector", "whiteboard", "video"]', true, NOW(), NOW()),
('회의실 C', '3층', 6, '["whiteboard"]', true, NOW(), NOW()),
('세미나실', '지하 1층', 30, '["projector", "video", "audio"]', true, NOW(), NOW());

-- 테스트용 회의 데이터
INSERT INTO meetings (title, description, organizer_id, room_id, start_time, end_time, status, type, meeting_link, is_recurring, recurrence_pattern, recurrence_end_date, meeting_notes, created_at, updated_at) VALUES
('주간 개발 회의', '이번 주 개발 진행상황 공유', 1, 1, '2024-07-01 10:00:00', '2024-07-01 11:00:00', 'CONFIRMED', 'TEAM_MEETING', NULL, false, NULL, NULL, '개발 진행상황 점검', NOW(), NOW()),
('디자인 리뷰', '신규 기능 디자인 검토', 2, 2, '2024-07-01 14:00:00', '2024-07-01 15:30:00', 'CONFIRMED', 'REVIEW', NULL, false, NULL, NULL, 'UI/UX 디자인 검토', NOW(), NOW()),
('프로젝트 킥오프', '신규 프로젝트 시작 회의', 1, 3, '2024-07-02 09:00:00', '2024-07-02 10:00:00', 'CONFIRMED', 'PROJECT_MEETING', NULL, false, NULL, NULL, '프로젝트 계획 수립', NOW(), NOW()),
('기술 세미나', '신기술 도입 검토', 3, 4, '2024-07-02 15:00:00', '2024-07-02 17:00:00', 'CONFIRMED', 'SEMINAR', NULL, false, NULL, NULL, '신기술 발표 및 토론', NOW(), NOW());

-- 테스트용 회의 참가자 데이터
INSERT INTO meeting_participants (meeting_id, user_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 2), (2, 4), (2, 1),
(3, 1), (3, 2), (3, 3), (3, 4),
(4, 1), (4, 2), (4, 3), (4, 4);

-- 테스트용 캘린더 이벤트 데이터
INSERT INTO calendar_events (user_id, title, description, start_time, end_time, event_type, is_all_day, location, external_event_id, source, created_at, updated_at) VALUES
(1, '외부 미팅', '클라이언트와의 미팅', '2024-07-01 13:00:00', '2024-07-01 14:00:00', 'MEETING', false, '외부', NULL, 'local', NOW(), NOW()),
(2, '의료진료', '정기 건강검진', '2024-07-01 16:00:00', '2024-07-01 17:00:00', 'PERSONAL', false, '병원', NULL, 'local', NOW(), NOW()),
(3, '점심 약속', '팀원들과의 점심', '2024-07-02 12:00:00', '2024-07-02 13:00:00', 'PERSONAL', false, '회사 근처', NULL, 'local', NOW(), NOW()),
(4, '교육 세미나', '기술 세미나 참석', '2024-07-04 09:00:00', '2024-07-04 12:00:00', 'SEMINAR', false, '세미나실', NULL, 'local', NOW(), NOW()); 