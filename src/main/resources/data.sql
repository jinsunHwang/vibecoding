CREATE TABLE teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT,
    email VARCHAR(255) NOT NULL UNIQUE,
    google_calendar_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    outlook_calendar_id VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    role ENUM('ADMIN','MANAGER','USER') DEFAULT 'USER',
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (team_id) REFERENCES teams(id)
);

CREATE TABLE meeting_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    capacity INT,
    is_active BIT DEFAULT 1,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE room_equipment (
    room_id BIGINT,
    equipment VARCHAR(255),
    PRIMARY KEY (room_id, equipment),
    FOREIGN KEY (room_id) REFERENCES meeting_rooms(id)
);

CREATE TABLE meetings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    organizer_id BIGINT,
    room_id BIGINT,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NOT NULL,
    status ENUM('CANCELLED','COMPLETED','IN_PROGRESS','SCHEDULED') DEFAULT 'SCHEDULED',
    type ENUM('HYBRID','IN_PERSON','ONLINE') DEFAULT 'IN_PERSON',
    meeting_link VARCHAR(255),
    is_recurring BIT DEFAULT 0,
    recurrence_pattern ENUM('BIWEEKLY','DAILY','MONTHLY','WEEKLY'),
    recurrence_end_date DATETIME(6),
    meeting_notes TEXT,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    FOREIGN KEY (organizer_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES meeting_rooms(id)
);

CREATE TABLE meeting_participants (
    meeting_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (meeting_id, user_id),
    FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE jwt_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    scope VARCHAR(255),
    token_type VARCHAR(50),
    expires_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
---------------------



INSERT INTO teams (created_at,updated_at,description,name) VALUES
	 ('2025-07-01 08:58:08.214083','2025-07-01 08:58:08.214083','소프트웨어 개발팀','개발팀'),
	 ('2025-07-01 08:58:08.286682','2025-07-01 08:58:08.286682','마케팅 및 홍보팀','마케팅팀');


INSERT INTO users (created_at,team_id,updated_at,email,google_calendar_id,name,outlook_calendar_id,password,phone_number,`role`) VALUES
	 ('2025-07-01 08:58:08.421555',1,'2025-07-01 08:58:08.421555','admin@vibecoding.com',NULL,'관리자',NULL,'$2a$10$oe2Bv40xbJ1Ktof5b8fNqujZLjNb2vEMrSIPh6emqtUdhf6WuNzG6','010-1234-5678','ADMIN'),
	 ('2025-07-01 08:58:08.549581',1,'2025-07-01 08:58:08.549581','john@vibecoding.com',NULL,'김철수',NULL,'$2a$10$zeXNEeg.7T6KY2.HlCIxCujbc3Qn4uN46vv1XGKNtL2VG76RjRr4.','010-2345-6789','USER'),
	 ('2025-07-01 08:58:08.675878',2,'2025-07-01 08:58:08.675878','jane@vibecoding.com',NULL,'이영희',NULL,'$2a$10$yVfgHJ4t3LBasDLKy.eYDuoyeSAKBMGInIJho1zVlL5hCuBvLk5xe','010-3456-7890','USER'),
	 ('2025-07-01 08:58:08.810938',1,'2025-07-01 08:58:08.810938','mike@vibecoding.com',NULL,'박민수',NULL,'$2a$10$NLb1YObsdJqQOsjIP3sYue.U3bdH6F7XG1b21DYrTFftDOXtRGSQW','010-4567-8901','MANAGER'),
	 ('2025-07-01 08:59:22.017812',NULL,'2025-07-01 08:59:22.017812','jinsun.hwang@solomontech.net',NULL,'test@vibecoding.com',NULL,'$2a$10$peUdX3WxZzu4W6iPHHociOpRYOA6Ifs9E6YHIOD4L4w5bA.hUacD6',NULL,'USER'),
	 ('2025-07-01 13:13:07.000000',1,'2025-07-01 13:13:07.000000','admin@company.com',NULL,'관리자',NULL,'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa','010-1234-5678','ADMIN'),
	 (NULL,1,NULL,'usertesst@vibecoding.com',NULL,'test',NULL,'$2a$10$1RS76b0G4JcAZT621Le44eTpYkqmPruHAPcicjsLBaYPc4Jeg.b4u','123','USER');


INSERT INTO room_equipment (room_id,equipment) VALUES
	 (1,'화상회의'),
	 (1,'프로젝터'),
	 (1,'화이트보드'),
	 (2,'모니터'),
	 (2,'화이트보드'),
	 (3,'화상회의'),
	 (3,'마이크'),
	 (3,'음향시설'),
	 (3,'프로젝터'),
	 (4,'모니터');
INSERT INTO room_equipment (room_id,equipment) VALUES
	 (5,'화이트보드'),
	 (5,'모니터'),
	 (14,'화이트보드'),
	 (14,'마이크'),
	 (15,'마이크'),
	 (15,'프로젝터');



INSERT INTO meetings (is_recurring,created_at,end_time,organizer_id,recurrence_end_date,room_id,start_time,updated_at,description,meeting_link,meeting_notes,title,recurrence_pattern,status,`type`) VALUES
	 (0,'2025-07-01 08:58:08.850751','2025-07-02 11:00:08.849458',4,NULL,1,'2025-07-02 10:00:08.849458','2025-07-01 08:58:08.850751','이번 주 개발 진행사항 공유',NULL,NULL,'개발팀 주간 회의',NULL,'SCHEDULED','IN_PERSON'),
	 (0,'2025-07-01 08:58:08.861490','2025-07-03 15:30:08.849458',3,NULL,2,'2025-07-03 14:00:08.849458','2025-07-01 08:58:08.861490','Q2 마케팅 전략 수립',NULL,NULL,'마케팅 전략 회의',NULL,'SCHEDULED','IN_PERSON'),
	 (0,'2025-07-01 09:03:28.868321','2025-07-16 10:00:00.000000',5,NULL,4,'2025-07-16 09:00:00.000000','2025-07-01 09:03:28.868321','asdf',NULL,NULL,'test',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,NULL,'2025-07-01 01:00:00.000000',1,NULL,NULL,'2025-07-01 00:00:00.000000',NULL,'asdfasdvasdv','',NULL,'testzzz',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 15:30:34.000000','2025-07-04 01:00:00.000000',1,NULL,3,'2025-07-04 00:00:00.000000','2025-07-01 15:30:34.000000','asdf','',NULL,'zzz',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 15:32:13.000000','2025-07-04 02:00:00.000000',1,NULL,3,'2025-07-04 01:00:00.000000','2025-07-01 15:32:13.000000','asdf','',NULL,'adsf',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 15:32:22.000000','2025-07-04 01:00:00.000000',1,NULL,NULL,'2025-07-04 00:00:00.000000','2025-07-01 15:32:22.000000','aa','',NULL,'aa',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 15:36:39.000000','2025-07-04 01:00:00.000000',1,NULL,NULL,'2025-07-04 00:00:00.000000','2025-07-01 15:36:39.000000','sg','',NULL,'r',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 15:53:13.000000','2025-07-05 01:00:00.000000',1,NULL,4,'2025-07-05 00:00:00.000000','2025-07-01 15:53:13.000000','asdfasdfas','',NULL,'asdasdf',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-01 16:01:40.000000','2025-07-01 17:01:00.000000',1,NULL,3,'2025-07-01 16:01:00.000000','2025-07-01 16:01:40.000000','ㅠㅌㅊ픁ㅊㅍ','',NULL,'ㄴㄹㅇㅎ',NULL,'SCHEDULED','IN_PERSON');
INSERT INTO meetings (is_recurring,created_at,end_time,organizer_id,recurrence_end_date,room_id,start_time,updated_at,description,meeting_link,meeting_notes,title,recurrence_pattern,status,`type`) VALUES
	 (NULL,'2025-07-02 08:58:18.000000','2025-07-02 09:58:00.000000',1,NULL,3,'2025-07-02 08:58:00.000000','2025-07-02 08:58:18.000000','asdfasdf','',NULL,'test',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 09:10:49.000000','2025-07-07 01:00:00.000000',1,NULL,NULL,'2025-07-07 00:00:00.000000','2025-07-02 09:10:49.000000','test','test.com',NULL,'aa',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 09:45:58.000000','2025-07-11 00:00:00.000000',1,NULL,4,'2025-07-09 00:00:00.000000','2025-07-02 09:45:58.000000','ㅌㅌㅌ','',NULL,'ㅋㅋ',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 12:49:43.000000','2025-07-03 23:59:00.000000',1,NULL,NULL,'2025-07-02 00:00:00.000000','2025-07-02 13:14:12.000000','ㅁㄴㅇㄹ','',NULL,'abcd',NULL,NULL,NULL),
	 (NULL,'2025-07-02 16:07:34.000000','2025-07-02 17:07:00.000000',1,NULL,3,'2025-07-02 16:07:00.000000','2025-07-02 16:07:34.000000','aa','',NULL,'aa',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 16:19:06.000000','2025-07-02 17:19:00.000000',1,NULL,NULL,'2025-07-02 16:19:00.000000','2025-07-02 16:19:06.000000','ㅁㄴㅇㄻㅇㄴㄹ','',NULL,'ㅅㅁㄴㅇㄹ',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 16:42:52.000000','2025-07-02 17:42:00.000000',1,NULL,NULL,'2025-07-02 16:42:00.000000','2025-07-02 16:42:52.000000','adsfasfsd','',NULL,'asdfa',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-02 16:43:00.000000','2025-07-03 23:59:00.000000',1,NULL,NULL,'2025-07-02 00:00:00.000000','2025-07-02 16:43:00.000000','vzxcvzcxv','',NULL,'zzz',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-07 13:48:16.000000','2025-07-07 15:00:00.000000',6,NULL,3,'2025-07-07 14:00:00.000000','2025-07-07 13:48:16.000000','Bot 예약','',NULL,'네이버웍스 예약 회의',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-07 13:51:10.000000','2025-07-07 16:00:00.000000',6,NULL,3,'2025-07-07 15:00:00.000000','2025-07-07 13:51:10.000000','Bot 예약','https://test.meeting.com',NULL,'네이버웍스 예약 회의',NULL,'SCHEDULED','ONLINE');
INSERT INTO meetings (is_recurring,created_at,end_time,organizer_id,recurrence_end_date,room_id,start_time,updated_at,description,meeting_link,meeting_notes,title,recurrence_pattern,status,`type`) VALUES
	 (NULL,'2025-07-07 14:29:29.000000','2025-07-07 10:30:00.000000',1,NULL,3,'2025-07-07 09:30:00.000000','2025-07-07 14:29:29.000000','zxcvzcxv','',NULL,'teasefdsa',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-07 14:30:08.000000','2025-07-09 23:59:00.000000',1,NULL,1,'2025-07-07 00:00:00.000000','2025-07-07 14:30:08.000000','dsvasdv','',NULL,'dfasdf',NULL,'SCHEDULED','IN_PERSON'),
	 (NULL,'2025-07-07 15:34:27.000000','2025-07-09 23:59:00.000000',17,NULL,NULL,'2025-07-08 00:00:00.000000','2025-07-07 15:34:27.000000','asdf','',NULL,'tessdf',NULL,'SCHEDULED','IN_PERSON');



INSERT INTO meeting_rooms (capacity,is_active,created_at,updated_at,location,name) VALUES
	 (10,1,'2025-07-01 08:58:08.818075','2025-07-01 08:58:08.818075','3층 301호','회의실 A'),
	 (6,1,'2025-07-01 08:58:08.829620','2025-07-02 16:15:22.000000','3층 302호','회의실 B'),
	 (20,1,'2025-07-01 08:58:08.836855','2025-07-01 08:58:08.836855','4층 401호','대회의실'),
	 (4,1,'2025-07-01 08:58:08.845411','2025-07-01 08:58:08.845411','2층 201호','소회의실'),
	 (2,1,'2025-07-01 08:58:08.845000','2025-07-02 16:15:24.000000','3층 303호','회의실 C'),
	 (2,0,'2025-07-02 16:14:26.000000','2025-07-02 16:15:14.000000','3층 305호','회의실 D'),
	 (3,0,'2025-07-02 16:14:56.000000','2025-07-02 16:15:30.000000','3층 304호','회의실');



INSERT INTO meeting_participants (meeting_id,user_id) VALUES
	 (1,1),
	 (1,2),
	 (5,2),
	 (5,3),
	 (46,3),
	 (2,4),
	 (5,4),
	 (46,6),
	 (47,6);

