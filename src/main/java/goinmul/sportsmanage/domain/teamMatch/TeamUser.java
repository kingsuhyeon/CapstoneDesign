package goinmul.sportsmanage.domain.teamMatch;



import goinmul.sportsmanage.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_match_id")
    private TeamMatch teamMatch;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static TeamUser createTeamUser(User user){
        TeamUser teamUser = new TeamUser();
        teamUser.setUser(user);
        return teamUser;
    }


    public static TeamUser createTeamUser(TeamMatch teamMatch, User user){
        TeamUser teamUser = new TeamUser();
        teamUser.setTeamMatch(teamMatch);
        teamUser.setUser(user);
        return teamUser;
    }

}
