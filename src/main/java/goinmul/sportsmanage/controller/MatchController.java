package goinmul.sportsmanage.controller;



import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.Sports;
import goinmul.sportsmanage.domain.SportsInfo;
import goinmul.sportsmanage.domain.User;
import goinmul.sportsmanage.domain.mercenaryMatch.*;
import goinmul.sportsmanage.domain.socialMatch.SocialMatch;
import goinmul.sportsmanage.domain.socialMatch.SocialUser;
import goinmul.sportsmanage.domain.teamMatch.Team;
import goinmul.sportsmanage.domain.teamMatch.TeamMatch;
import goinmul.sportsmanage.domain.teamMatch.TeamUser;
import goinmul.sportsmanage.service.SportsService;
import goinmul.sportsmanage.service.UserService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryMatchService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryMatchTeamMercenaryMaxSizeService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryUserService;
import goinmul.sportsmanage.service.socialMatch.SocialMatchService;
import goinmul.sportsmanage.service.socialMatch.SocialUserService;
import goinmul.sportsmanage.service.teamMatch.TeamMatchService;
import goinmul.sportsmanage.service.teamMatch.TeamUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MatchController {

    private final SportsService sportsService;
    private final UserService userService;
    private final SocialUserService socialUserService;
    private final SocialMatchService socialMatchService;
    private final TeamMatchService teamMatchService;
    private final TeamUserService teamUserService;
    private final MercenaryMatchService mercenaryMatchService;
    private final MercenaryUserService mercenaryUserService;
    private final MercenaryMatchTeamMercenaryMaxSizeService mercenaryMatchTeamMercenaryMaxSizeService;

    @GetMapping("/social/{sports}")
    public String socialMatchList(@PathVariable String sports, @RequestParam(required = false) String location,
                                  @RequestParam(required = false) String date, @RequestParam(required = false) String gender, Model model) {

        if (location == null) location = "서울";
        if (date == null) date = LocalDate.now().toString();
        if (gender == null) gender = "MALE";

        Gender genderEnum = sportsService.createGenderEnum(gender);
        Sports sportsEnum = sportsService.createSportsEnum(sports);
        SportsInfo sportsInfo = sportsService.getSportsInfo(sports);

        List<SocialMatch> socialMatchWithReservationAndGround = socialMatchService.
                findSocialMatchWithReservationAndGroundByDateAndLocationAndGender(LocalDate.parse(date), location, genderEnum, sportsEnum);

        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("location", location);
        model.addAttribute("date", date);
        model.addAttribute("gender", gender);
        model.addAttribute("socialMatchWithReservationAndGround", socialMatchWithReservationAndGround);
        model.addAttribute("sports", sports); //안써도 자동으로 넣어주긴 해요

        return "match/socialMatch";
    }

    //소셜 매치 생성은 구장예약 할때 자동으로 됨
    @PostMapping("/social/{sports}")
    public String joinSocialMatch(@RequestParam Long socialMatchId, Model model, HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        SocialMatch socialMatch = socialMatchService.findSocialMatchById(socialMatchId);
        List<SocialUser> socialUsers = socialUserService.findSocialUserBySocialMatchId(socialMatchId);
        SocialUser socialUser = socialUserService.findSocialUserByUserId(socialUsers, sessionUser.getId()); //DB에서 가져오는 거 아니에요.

        if (socialMatch.getMaxSize() == socialUsers.size()) {
            model.addAttribute("message", "마감된 매치입니다.");
            return "errorPage";
        }
        if (!sessionUser.getGender().equals(socialMatch.getGender())) {
            model.addAttribute("message", "모집 성별과 불일치 합니다");
            return "errorPage";
        }
        if (socialUser != null) {
            model.addAttribute("message", "이미 신청했습니다.");
            return "errorPage";
        }

        socialUserService.saveSocialUser(SocialUser.createSocialUser(socialMatch, sessionUser));
        model.addAttribute("message", "매치 신청 완료!");
        return "okPage";
    }

    @GetMapping("/team/{sports}")
    public String teamMatchList(@PathVariable String sports, @RequestParam(required = false) String location,
                                @RequestParam(required = false) String date, @RequestParam(required = false) String gender, Model model) {

        if (location == null) location = "서울";
        if (date == null) date = LocalDate.now().toString();
        if (gender == null) gender = "MALE";

        Gender genderEnum = sportsService.createGenderEnum(gender);
        Sports sportsEnum = sportsService.createSportsEnum(sports);
        SportsInfo sportsInfo = sportsService.getSportsInfo(sports);

        //쿼리 두번 나가게 설계했어요
        List<TeamMatch> teamMatchWithReservationAndGround = teamMatchService.
                findTeamMatchWithAllEntityAndTeamListByDateAndLocationAndGender(LocalDate.parse(date), location, genderEnum, sportsEnum);

        model.addAttribute("teamMatchWithReservationAndGround", teamMatchWithReservationAndGround);
        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("location", location);
        model.addAttribute("date", date);
        model.addAttribute("gender", gender);
        model.addAttribute("sports", sports); //안써도 자동으로 넣어주긴 해요

        return "match/teamMatch";
    }

    @GetMapping("/teamMatch/{teamMatchId}/user")
    public String choiceTeamUserList(@PathVariable Long teamMatchId, HttpServletRequest request, Model model) {

        User sessionUser = (User) request.getSession().getAttribute("user");
        TeamMatch teamMatch = teamMatchService.findTeamMatchWithTeamUserByTeamMatchId(teamMatchId);
        if (sessionUser == null) {
            return "pleaseLogin";
        }

        if (teamMatch.getMaxSize() == teamMatch.getTeamUsers().size()) {
            model.addAttribute("message", "마감된 매치입니다.");
            return "errorPage";
        }
        if (sessionUser.getTeam() == null) {
            model.addAttribute("message", "팀에 가입해주세요");
            return "errorPage";
        }
        if (teamMatch.getTeamUsers().get(0).getUser().getTeam().getId().equals(sessionUser.getTeam().getId())) {
            model.addAttribute("message", "이미 팀이 신청한 경기입니다.");
            return "errorPage";
        }
        List<User> users = userService.findUsersByTeamIdAndGender(sessionUser.getTeam().getId(), teamMatch.getGender());
        model.addAttribute("users", users);
        model.addAttribute("match", teamMatch);
        return "match/choiceTeamMemberForm";

    }

    @PostMapping("/teamMatch/{teamMatchId}/user")
    public String choiceTeamUser(@PathVariable Long teamMatchId, @RequestParam(required = false) List<Long> userIdList,
                                 HttpServletRequest request, Model model) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        if (sessionUser.getTeam() == null) {
            model.addAttribute("message", "팀에 가입해주세요");
            return "errorPage";
        }
        TeamMatch teamMatch = teamMatchService.findTeamMatchById(teamMatchId);
        if (userIdList.isEmpty()) {
            String errorMessage = teamMatch.getMaxSize() / 2 + "명을 뽑아주세요";
            model.addAttribute("message", errorMessage);
            return "errorPage";
        }
        if (teamMatch.getMaxSize() / 2 != userIdList.size()) {
            String errorMessage = teamMatch.getMaxSize() / 2 + "명을 뽑아주세요";
            model.addAttribute("message", errorMessage);
            return "errorPage";
        }

        List<User> users = userService.findUserByIdIn(userIdList);
        List<TeamUser> teamUsers = new ArrayList<>();
        for (User user : users) {
            teamUsers.add(TeamUser.createTeamUser(teamMatch, user));
        }
        teamUserService.saveTeamUsers(teamUsers);

        model.addAttribute("message", "팀 매치 참가 완료!");
        return "okPage";
    }

    @GetMapping("/mercenary/{sports}")
    public String mercenaryMatchList(@PathVariable String sports, @RequestParam(required = false) String location,
                                     @RequestParam(required = false) String date, @RequestParam(required = false) String gender, Model model) {

        if (location == null) location = "서울";
        if (date == null) date = LocalDate.now().toString();
        if (gender == null) gender = "MALE";

        Gender genderEnum = sportsService.createGenderEnum(gender);
        Sports sportsEnum = sportsService.createSportsEnum(sports);
        SportsInfo sportsInfo = sportsService.getSportsInfo(sports);

        List<MercenaryMatch> mercenaryMatchWithReservation = mercenaryMatchService.
                findMercenaryMatchWithAllEntityAndTeamListByDateAndLocationAndGender(LocalDate.parse(date), location, genderEnum, sportsEnum);

        model.addAttribute("mercenaryMatchWithReservation", mercenaryMatchWithReservation);
        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("location", location);
        model.addAttribute("date", date);
        model.addAttribute("gender", gender);
        model.addAttribute("sports", sports); //안써도 자동으로 넣어주긴 해요

        return "match/mercenaryMatch";
    }

    //용병 신청 Post (용병 매치 생성은 구장예약 할때 자동으로 됨)
    @PostMapping("/mercenary/{sports}")
    public String joinMercenaryInMercenaryMatch(@RequestParam Long mercenaryMatchId, @RequestParam Long teamId,
                                                Model model, HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        MercenaryMatch mercenaryMatch = mercenaryMatchService.findWithMercenaryMaxSizeAndMercenaryUser(mercenaryMatchId);


        //용병 모집 마감시 신청 반려
        for (TeamWithMercenary teamWithMercenary : mercenaryMatch.getTeamsWithMercenary()) {
            if(teamWithMercenary.getTeam().getId().equals(teamId) && teamWithMercenary.getTeamMercenaryMaxSize().equals(teamWithMercenary.getUsers().size()) ){
                model.addAttribute("message", "용병 모집이 마감됐어요.");
                return "errorPage";
            }
        }

        //중복 신청 방지 & 용병 한 곳에만 신청 가능하게 함   - 리스트에서 본인 계정 PK값 있으면 RETURN
        for (MercenaryMatchUser mercenaryMatchUser : mercenaryMatch.getMercenaryMatchUsers()) {
            if(mercenaryMatchUser.getUser().getId().equals(sessionUser.getId())){
                model.addAttribute("message", "이미 신청했습니다.");
                return "errorPage";
            }
        }


        if (mercenaryMatch.getMaxSize() == mercenaryMatch.getMercenaryMatchUsers().size()) {
            model.addAttribute("message", "마감된 매치입니다.");
            return "errorPage";
        }
        if (!sessionUser.getGender().equals(mercenaryMatch.getGender())) {
            model.addAttribute("message", "모집 성별과 불일치 합니다");
            return "errorPage";
        }


        Team team = Team.createTeam(teamId);
        mercenaryUserService.saveMercenaryUser(MercenaryMatchUser.createMercenaryUser(mercenaryMatch, sessionUser, team, UserStatus.MERCENARY));
        model.addAttribute("message", "용병 신청 완료!");
        return "okPage";
    }

    @GetMapping("/mercenaryMatch/{mercenaryMatchId}/user")
    public String choiceMercenaryUserList(@PathVariable Long mercenaryMatchId, HttpServletRequest request, Model
            model) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        //MercenaryMatch mercenaryMatch = mercenaryMatchService.findMercenaryMatchById(mercenaryMatchId);
        //하나만 들고오는 쿼리로 수정해야함
        //List<MercenaryMatchUser> mercenaryUsers = mercenaryUserService.findMercenaryUsersByMercenaryMatchId(mercenaryMatchId);

        MercenaryMatch mercenaryMatch = mercenaryMatchService.findWithMercenaryMaxSizeAndMercenaryUser(mercenaryMatchId);

        if (sessionUser == null) {
            return "pleaseLogin";
        }
        if (mercenaryMatch.getMaxSize() == mercenaryMatch.getMercenaryMatchUsers().size()) {
            model.addAttribute("message", "마감된 매치입니다.");
            return "errorPage";
        }
        if (sessionUser.getTeam() == null) {
            model.addAttribute("message", "팀에 가입해주세요");
            return "errorPage";
        }
        //팀에 가입한 사람이 용병으로 들어올수도 있기떄문에, >= 썼어요. for문으로 하면 복잡해서
        if(mercenaryMatch.getMercenaryMatchUsers().size() >= 2 ){
            model.addAttribute("message", "이미 두 팀이 참여했어요");
            return "errorPage";
        }

        //참여 팀이 둘 이상이면 마감된 매치에서 걸려서 contains 안해도됨. 근데 contains 쓰는게 if에 안 종속적 이다.
        if (mercenaryMatch.getMercenaryMatchUsers().get(0).getUser().getTeam().getId().equals(sessionUser.getTeam().getId())) {
            model.addAttribute("message", "이미 팀이 신청한 경기입니다.");
            return "errorPage";
        }
        List<User> users = userService.findUsersByTeamIdAndGender(sessionUser.getTeam().getId(), mercenaryMatch.getGender());


        model.addAttribute("users", users);
        model.addAttribute("match", mercenaryMatch);
        return "match/choiceTeamMemberAndMercenaryForm";

    }

    @PostMapping("/mercenaryMatch/{mercenaryMatchId}/user")
    public String choiceMercenaryUser(@PathVariable Long mercenaryMatchId, @RequestParam(required = false) List<Long> userIdList,
                                      @RequestParam Integer mercenaryMaxSize,  HttpServletRequest request, Model model) {
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser.getTeam() == null) {
            return "pleaseJoinTeam";
        }
        if (userIdList == null) {
            model.addAttribute("message", "팀원은 최소 한명은 있어야 해요.");
            return "errorPage";
        }
        if (mercenaryMaxSize == null) {
            model.addAttribute("message", "모집할 용병 수를 정하세요");
            return "errorPage";
        }
        MercenaryMatch mercenaryMatch = mercenaryMatchService.findMercenaryMatchById(mercenaryMatchId);
        //참여 팀 갯수를 2개로 제한(get에서 처리하긴하는데 2중으로 해놧음)
        if(mercenaryMatch.getTeamsWithMercenary().size() >= 2){
            model.addAttribute("message", "이미 2팀이 참가했어요");
            return "errorPage";
        }
        if (mercenaryMaxSize + userIdList.size() != mercenaryMatch.getMaxSize()/2) {
            String message = "용병과 팀원을 포함해 " + mercenaryMatch.getMaxSize()/2 + "명을 뽑아주세요";
            model.addAttribute("message", message);
            return "errorPage";
        }

        List<User> users = userService.findUserWithTeamByUserIdIn(userIdList);
        List<MercenaryMatchUser> mercenaryUsers = new ArrayList<>();
        for (User user : users) {
            mercenaryUsers.add(MercenaryMatchUser.createMercenaryUser(mercenaryMatch, user, user.getTeam(), UserStatus.TEAM));
        }
        mercenaryUserService.saveMercenaryUsers(mercenaryUsers);

        MercenaryMatchTeamMercenaryMaxSize mmtmms = MercenaryMatchTeamMercenaryMaxSize.createMercenaryTeam(mercenaryMatch, sessionUser.getTeam(), mercenaryMaxSize);
        mercenaryMatchTeamMercenaryMaxSizeService.save(mmtmms);

        model.addAttribute("message", "용병 매치 참가 완료!");
        return "okPage";
    }


}
