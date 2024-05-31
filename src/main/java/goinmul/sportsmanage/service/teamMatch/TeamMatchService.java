package goinmul.sportsmanage.service.teamMatch;

import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.Sports;
import goinmul.sportsmanage.domain.teamMatch.TeamMatch;
import goinmul.sportsmanage.domain.teamMatch.TeamUser;
import goinmul.sportsmanage.repository.teamMatch.TeamMatchRepository;
import goinmul.sportsmanage.repository.teamMatch.TeamUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TeamMatchService {

    private final TeamMatchRepository teamMatchRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional
    public void saveTeamMatch(TeamMatch teamMatch) {
        teamMatchRepository.save(teamMatch);
    }

    public TeamMatch findTeamMatchById(Long id) {
        return teamMatchRepository.findOne(id);
    }

    public TeamMatch findTeamMatchWithTeamUserByTeamMatchId(Long id) {
        return teamMatchRepository.findOneWithTeamUserByTeamMatchId(id);
    }

    public List<TeamMatch> findTeamMatchWithAllEntityByDateAndLocationAndGender(LocalDate date, String location, Gender gender, Sports sports) {
        return teamMatchRepository.findAllWithAllEntityByDateAndLocationAndGender(date, location, gender, sports);
    }

    //teamUser가 중간에 추가되면 어떡하지?
    public List<TeamMatch> findTeamMatchWithAllEntityAndTeamListByDateAndLocationAndGender(LocalDate date, String location, Gender gender, Sports sports) {
        List<TeamMatch> teamMatchs = teamMatchRepository.findAllWithAllEntityByDateAndLocationAndGender(date, location, gender, sports);
        List<Long> teamMatchIdList = new ArrayList<>();
        for (TeamMatch teamMatch : teamMatchs) {
            teamMatchIdList.add(teamMatch.getId());
        }
        List<TeamUser> teamUsers = teamUserRepository.findWithUserAndTeamByTeamMatchIdIn(teamMatchIdList);
        Set<Long> teamIdList = new HashSet<>();
        //List<Team> teamList = new ArrayList<>();

        //데이터 많으면 연산 늘어서, Map으로 하는게 난듯
        for (TeamMatch teamMatch : teamMatchs) {
            for (TeamUser teamUser : teamUsers) {
                if (teamMatch.getId().equals(teamUser.getTeamMatch().getId()) && !teamIdList.contains(teamUser.getUser().getTeam().getId())) {
                    teamIdList.add(teamUser.getUser().getTeam().getId());
                    teamMatch.getTeamList().add(teamUser.getUser().getTeam());
                }
            }
            teamIdList.clear();
        }

        return teamMatchs;
    }

    public List<TeamMatch> findTeamMatchs() {
        return teamMatchRepository.findAll();
    }


}
