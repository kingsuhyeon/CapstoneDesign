package goinmul.sportsmanage;


import goinmul.sportsmanage.domain.*;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatch;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatchTeamMercenaryMaxSize;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatchUser;
import goinmul.sportsmanage.domain.mercenaryMatch.UserStatus;
import goinmul.sportsmanage.domain.socialMatch.SocialMatch;
import goinmul.sportsmanage.domain.socialMatch.SocialUser;
import goinmul.sportsmanage.domain.teamMatch.Team;
import goinmul.sportsmanage.domain.teamMatch.TeamMatch;
import goinmul.sportsmanage.domain.teamMatch.TeamUser;
import goinmul.sportsmanage.service.GroundService;
import goinmul.sportsmanage.service.ReservationService;
import goinmul.sportsmanage.service.TeamService;
import goinmul.sportsmanage.service.UserService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryMatchService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryMatchTeamMercenaryMaxSizeService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryUserService;
import goinmul.sportsmanage.service.socialMatch.SocialMatchService;
import goinmul.sportsmanage.service.socialMatch.SocialUserService;
import goinmul.sportsmanage.service.teamMatch.TeamMatchService;
import goinmul.sportsmanage.service.teamMatch.TeamUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbInit {

    private final UserService userService;
    private final GroundService groundService;
    private final ReservationService groundReservationService;
    private final SocialUserService socialUserService;
    private final TeamService teamService;
    private final SocialMatchService socialMatchService;
    private final TeamUserService teamUserService;
    private final TeamMatchService teamMatchService;
    private final MercenaryMatchService mercenaryMatchService;
    private final MercenaryUserService mercenaryUserService;
    private final MercenaryMatchTeamMercenaryMaxSizeService mercenaryMatchTeamMercenaryMaxSizeService;

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    //@PostConstruct
    public void init() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                List<User> users = createUser();
                List<Ground> grounds = createGround();

                Team team = Team.createTeam("맨체스터");
                teamService.saveTeam(team);
                Team team2 = Team.createTeam("아스날");
                teamService.saveTeam(team2);
                Team team3 = Team.createTeam("한화");
                teamService.saveTeam(team3);

                userService.changeTeam(users.get(0), team);
                userService.changeTeam(users.get(1), team);
                userService.changeTeam(users.get(2), team2);
                userService.changeTeam(users.get(3), team2);
                userService.changeTeam(users.get(7), team3);


                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 문자열 형식 지정
                // LocalDate parseDate = LocalDate.parse("2024-05-10", formatter);

                //예약 저장하면 소셜매치 함께 저장됨. 그리고 소셜매치 신청한 user가 socialUser에 저장됨
                Reservation rv = createReservation(grounds.get(0), users.get(0), 20);
                createReservation(grounds.get(1), users.get(0), 19);
                createSocialMatchSocialUser(rv, 2, users.get(1));

                Reservation rv2 = createReservation(grounds.get(1), users.get(0), 20);
                createTeamMatchTeamUser(rv2,4, users.get(0), users.get(1), users.get(2), users.get(3));

                Reservation rv3 = createReservation(grounds.get(2), users.get(0), 20);
                MercenaryMatch mercenaryMatch = createMercenaryMatchMercenaryUser(rv3, 4, team, users.get(0));
                addMercenaryMatchMercenaryUser(rv3, mercenaryMatch, team2, users.get(2));
            }
        });
    }

    private Reservation createReservation(Ground ground, User user, int time){
        Reservation rv = Reservation.createGroundReservation(ground, user, LocalDate.now(), time);
        groundReservationService.saveGroundReservation(rv);
        return rv;
    }

    private MercenaryMatch createMercenaryMatchMercenaryUser(Reservation rv,int maxSize,Team team, User... users) {
        List<MercenaryMatchUser> mercenaryMatchUsers = new ArrayList<>();
        for (User user : users) {
            MercenaryMatchUser teamMember = MercenaryMatchUser.createMercenaryUser(user, user.getTeam(), UserStatus.TEAM);
            mercenaryMatchUsers.add(teamMember);
        }
        MercenaryMatchTeamMercenaryMaxSize mercenaryMatchTeamMercenaryMaxSize = MercenaryMatchTeamMercenaryMaxSize.createMercenaryTeam(team, 1);
        MercenaryMatch mercenaryMatch = MercenaryMatch.createMercenaryMatch(maxSize, Gender.MALE, Sports.FUTSAL, rv, mercenaryMatchUsers, mercenaryMatchTeamMercenaryMaxSize);
        mercenaryMatchService.saveMercenaryMatch(mercenaryMatch);
        return mercenaryMatch;
    }

    private void addMercenaryMatchMercenaryUser(Reservation rv, MercenaryMatch mercenaryMatch, Team team, User... users) {
        List<MercenaryMatchUser> mercenaryMatchUsers = new ArrayList<>();
        for (User user : users) {
            MercenaryMatchUser teamMember = MercenaryMatchUser.createMercenaryUser(user, user.getTeam(), UserStatus.TEAM);
            teamMember.setMercenaryMatch(mercenaryMatch);
            mercenaryMatchUsers.add(teamMember);
        }
        
        MercenaryMatchTeamMercenaryMaxSize mercenaryMatchTeamMercenaryMaxSize = MercenaryMatchTeamMercenaryMaxSize.createMercenaryTeam(mercenaryMatch, team, 1);
        mercenaryUserService.saveMercenaryUsers(mercenaryMatchUsers);
        mercenaryMatchTeamMercenaryMaxSizeService.save(mercenaryMatchTeamMercenaryMaxSize);

    }
    private void createTeamMatchTeamUser(Reservation rv, int maxSize, User... users) {
        List<TeamUser> teamUsers = new ArrayList<>();
        for (User user : users) {
            TeamUser tm_user = TeamUser.createTeamUser(user);
            teamUsers.add(tm_user);
        }

        TeamMatch tm = TeamMatch.createTeamMatch(maxSize, Gender.MALE, Sports.FUTSAL, rv, teamUsers);
        teamMatchService.saveTeamMatch(tm);
    }

    private void createSocialMatchSocialUser(Reservation rv, int maxSize, User... users) {
        SocialUser sm_user1 = SocialUser.createSocialUser(users[0]);
        SocialMatch sm = SocialMatch.createSocialMatch(maxSize, Gender.MALE, Sports.FUTSAL, sm_user1, rv);
        socialMatchService.saveSocialMatch(sm);

        for(int i =0; i<users.length; i++){
            SocialUser sm_user = SocialUser.createSocialUser(sm, users[i]); //소셜매치1에 매핑한 user2 데이터 생성
            saveSocialUser(sm_user); //소셜 매치 저장되고 실행되야함
        }

    }

    private void saveSocialUser(SocialUser ... socialUsers) {
        for (SocialUser socialUser : socialUsers) {
            socialUserService.saveSocialUser(socialUser);
        }
    }

    private void saveTeamUser(TeamUser... teamUsers) {
        for (TeamUser teamUser : teamUsers) {
           teamUserService.saveTeamUser(teamUser);
        }
    }


    private List<Ground> createGround() {
        List<Ground> groundList = new ArrayList<>();
        groundList.add(Ground.createGround("동대문 운동장", "서울"));
        groundList.add(Ground.createGround("목동 운동장", "서울"));
        groundList.add(Ground.createGround("효창 운동장", "서울"));
        groundList.add(Ground.createGround("서울 올림픽 경주 경기장", "서울"));
        groundList.add(Ground.createGround("서울 종합 운동장", "서울"));
        groundList.add(Ground.createGround("서울 올림픽 공원", "서울"));

        for (Ground ground : groundList) {
            groundService.saveGround(ground);
        }
        return groundList;
    }

    private List<User> createUser() {
        List<User> users = new ArrayList<>();

        users.add(User.createUser("kbm", "123", "김병만", "010-1234-5678", Gender.MALE));
        users.add(User.createUser("kch", "123", "김찬호", "010-1264-5678", Gender.MALE));
        users.add(User.createUser("nsh", "123", "노성현", "010-1234-5678", Gender.MALE));
        users.add(User.createUser("sms", "123", "손민수", "010-1234-5678", Gender.MALE));

        users.add(User.createUser("jjh", "123", "전지현", "010-9156-1235", Gender.FEMALE));
        users.add(User.createUser("kjh", "123", "김혜자", "010-1236-1235", Gender.FEMALE));
        users.add(User.createUser("jjm", "123", "전지민", "010-1234-5678", Gender.MALE));
        users.add(User.createUser("jjm2", "123", "전지민2", "010-1234-5678", Gender.MALE));
        for (User user : users) {
            userService.saveUser(user);
        }

        return users;
    }


}
