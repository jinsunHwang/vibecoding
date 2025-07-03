package com.cursormeeting.cursor_meeting_demo.service;

import com.cursormeeting.cursor_meeting_demo.domain.Team;
import com.cursormeeting.cursor_meeting_demo.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {
    
    private final TeamMapper teamMapper;
    
    public List<Team> getAllTeams() {
        return teamMapper.findAll();
    }
    
    public Team getTeamById(Long id) {
        return teamMapper.findById(id);
    }
    
    public Team createTeam(Team team) {
        teamMapper.insert(team);
        return team;
    }
    
    public Team updateTeam(Long id, Team team) {
        Team existingTeam = teamMapper.findById(id);
        if (existingTeam == null) {
            throw new RuntimeException("팀을 찾을 수 없습니다.");
        }
        
        team.setId(id);
        teamMapper.update(team);
        return team;
    }
    
    public void deleteTeam(Long id) {
        teamMapper.deleteById(id);
    }
} 