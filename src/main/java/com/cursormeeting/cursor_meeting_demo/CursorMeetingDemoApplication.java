package com.cursormeeting.cursor_meeting_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cursormeeting.cursor_meeting_demo.mapper")
public class CursorMeetingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursorMeetingDemoApplication.class, args);
	}

}
