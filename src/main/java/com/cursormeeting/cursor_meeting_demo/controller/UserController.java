package com.cursormeeting.cursor_meeting_demo.controller;

import com.cursormeeting.cursor_meeting_demo.domain.User;
import com.cursormeeting.cursor_meeting_demo.dto.UserDto;
import com.cursormeeting.cursor_meeting_demo.service.UserService;
import com.cursormeeting.cursor_meeting_demo.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;
import java.util.List;
import com.cursormeeting.cursor_meeting_demo.domain.Team;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final TeamService teamService;
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<User>> getUsersByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(userService.getUsersByTeamId(teamId));
    }
    
    @GetMapping("/count")
    public int getUserCount() {
        return userService.getAllUsers().size();
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            User user = userService.createUser(userDto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        if (userService.getUserByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.status(409).body("이미 사용 중인 이메일입니다.");
        }
        if (userDto.getTeamId() == null || teamService.getTeamById(userDto.getTeamId()) == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 팀입니다.");
        }
        userDto.setRole("USER");
        try {
            userService.createUser(userDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("회원가입 중 오류: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        try {
            User user = userService.updateUser(id, userDto);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }
} 