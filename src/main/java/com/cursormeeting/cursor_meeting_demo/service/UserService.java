package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.User;
import com.cursormeeting.cursor_meeting_demo.dto.UserDto;
import com.cursormeeting.cursor_meeting_demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
    
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
    
    public User getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }
    
    public List<User> getUsersByTeamId(Long teamId) {
        return userMapper.findByTeamId(teamId);
    }
    
    public User createUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(userDto.getRole());
        user.setTeamId(userDto.getTeamId());
        user.setGoogleCalendarId(userDto.getGoogleCalendarId());
        user.setOutlookCalendarId(userDto.getOutlookCalendarId());
        
        userMapper.insert(user);
        return user;
    }
    
    public User updateUser(Long id, UserDto userDto) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setName(userDto.getName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRole(userDto.getRole());
        user.setTeamId(userDto.getTeamId());
        user.setGoogleCalendarId(userDto.getGoogleCalendarId());
        user.setOutlookCalendarId(userDto.getOutlookCalendarId());
        
        userMapper.update(user);
        return user;
    }
    
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    public List<User> getUsersByIds(List<Long> ids) {
        return userMapper.findByIds(ids);
    }
} 