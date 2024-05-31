package goinmul.sportsmanage.domain.mercenaryMatch;


import goinmul.sportsmanage.domain.teamMatch.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class TeamWithMercenary {

    private Team team;
    private Integer teamMercenaryMaxSize;
    private List<MercenaryMatchUser> users = new ArrayList<>();

    public static TeamWithMercenary createTeamWithMercenary(Team team, Integer teamMercenaryMaxSize){
        TeamWithMercenary teams = new TeamWithMercenary();
        teams.setTeam(team);
        teams.setTeamMercenaryMaxSize(teamMercenaryMaxSize);
        return teams;
    }

}
