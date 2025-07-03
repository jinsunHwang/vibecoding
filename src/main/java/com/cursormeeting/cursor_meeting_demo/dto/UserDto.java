package com.cursormeeting.cursor_meeting_demo.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;
    
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    private String phoneNumber;
    private String role;
    private Long teamId;
    private String googleCalendarId;
    private String outlookCalendarId;
} 