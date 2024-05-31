package goinmul.sportsmanage.controller;


import goinmul.sportsmanage.domain.*;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatch;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatchTeamMercenaryMaxSize;
import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatchUser;
import goinmul.sportsmanage.domain.mercenaryMatch.UserStatus;
import goinmul.sportsmanage.domain.socialMatch.SocialMatch;
import goinmul.sportsmanage.domain.socialMatch.SocialUser;
import goinmul.sportsmanage.domain.teamMatch.TeamMatch;
import goinmul.sportsmanage.domain.teamMatch.TeamUser;
import goinmul.sportsmanage.service.GroundService;
import goinmul.sportsmanage.service.ReservationService;
import goinmul.sportsmanage.service.SportsService;
import goinmul.sportsmanage.service.UserService;
import goinmul.sportsmanage.service.mercenaryMatch.MercenaryMatchService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final SportsService sportsService;
    private final GroundService groundService;
    private final UserService userService;
    private final ReservationService groundReservationService;
    private final SocialUserService socialUserService;
    private final SocialMatchService socialMatchService;
    private final TeamMatchService teamMatchService;
    private final TeamUserService teamUserService;
    private final MercenaryMatchService mercenaryMatchService;

    @GetMapping("/reservation/{sports}")
    public String reservationForm(@PathVariable String sports, Model model, HttpServletRequest request) {
        SportsInfo sportsInfo = sportsService.getSportsInfo("futsal");
        List<Ground> grounds = groundService.findGrounds();
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        List<User> teamMembers = null;
        if (sessionUser.getTeam() != null)
            teamMembers = userService.findUsersByTeamId(sessionUser.getTeam().getId());

        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("grounds", grounds);
        model.addAttribute("MALE", Gender.MALE);
        model.addAttribute("FEMALE", Gender.FEMALE);
        return "groundReservationForm";

    }

    @PostMapping("/reservation/{sports}")
    public String reservation(@RequestParam Long groundId, @RequestParam String date, @RequestParam int time,
                              @RequestParam String match, @RequestParam String gender, @PathVariable String sports,
                              @RequestParam int maxSize, @RequestParam(required = false) List<Long> userIdList,
                              @RequestParam(required = false) Integer mercenarySize, Model model, HttpServletRequest request) {

        SportsInfo sportsInfo = sportsService.getSportsInfo("futsal");
        model.addAttribute("sportsInfo", sportsInfo);
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "/pleaseLogin";
        }
        Ground ground = groundService.findGroundById(groundId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 문자열 형식 지정
        LocalDate parseDate = LocalDate.parse(date, formatter);
        Gender genderEnum = sportsService.createGenderEnum(gender);
        Sports sportsEnum = sportsService.createSportsEnum(sports);
        Match matchEnum = sportsService.createMatchEnum(match);
        SocialUser socialUser = SocialUser.createSocialUser(sessionUser);
        Reservation groundReservation = null;
        List<User> users = new ArrayList<>();
        switch (matchEnum) {
            case SOCIAL:
                groundReservation = Reservation.createGroundReservation(ground, sessionUser, parseDate, time);
                try {
                    groundReservationService.saveGroundReservation(groundReservation);
                } catch (Exception e) {
                    model.addAttribute("message", "이미 예약된 곳입니다");
                    return "errorPage";
                }
                SocialMatch socialMatch = SocialMatch.createSocialMatch(maxSize, genderEnum, sportsEnum, socialUser, groundReservation);
                socialMatchService.saveSocialMatch(socialMatch);
                return "redirect:/social/{sports}";
            case TEAM:
                if (sessionUser.getTeam() == null) {
                    return "pleaseJoinTeam";
                }
                if (userIdList == null) {
                    String message = maxSize / 2 + "명을 뽑아주세요";
                    model.addAttribute("message", message);
                    return "errorPage";
                }
                //성별 체크하는 코드
                users = userService.findUserByIdIn(userIdList);
                for (User user : users) {
                    if (user.getGender() != genderEnum) {
                        model.addAttribute("message", "모집 성별과 안 맞는 멤버를 선택했어요");
                        return "errorPage";
                    }
                }
                if (maxSize / 2 != userIdList.size()) {
                    String message = maxSize / 2 + "명을 뽑아주세요";
                    model.addAttribute("message", message);
                    return "errorPage";
                }
                groundReservation = Reservation.createGroundReservation(ground, sessionUser, parseDate, time);
                try {
                    groundReservationService.saveGroundReservation(groundReservation);
                } catch (Exception e) {
                    model.addAttribute("message", "이미 예약된 곳입니다");
                    return "errorPage";
                }
                List<TeamUser> teamUsers = new ArrayList<>();
                for (User user : users) {
                    TeamUser teamUser = TeamUser.createTeamUser(user);
                    teamUsers.add(teamUser);
                }
                TeamMatch teamMatch = TeamMatch.createTeamMatch(maxSize, genderEnum, sportsEnum, groundReservation, teamUsers);
                teamMatchService.saveTeamMatch(teamMatch);
                model.addAttribute("message", "팀 매치 생성 완료!");
                return "okPage";
            case MERCENARY:
                if (sessionUser.getTeam() == null) {
                    return "pleaseJoinTeam";
                }
                if (userIdList == null) {
                    model.addAttribute("message", "팀원은 최소 한명은 있어야 해요.");
                    return "errorPage";
                }
                if (mercenarySize == null) {
                    model.addAttribute("message", "모집할 용병 수를 정하세요");
                    return "errorPage";
                }
                if (mercenarySize + userIdList.size() != maxSize / 2) {
                    String message = "용병과 팀원을 포함해 " + maxSize / 2 + "명을 뽑아주세요";
                    model.addAttribute("message", message);
                    return "errorPage";
                }
                //성별 체크하는 코드
                users = userService.findUserWithTeamByUserIdIn(userIdList);
                for (User user : users) {
                    if (user.getGender() != genderEnum) {
                        model.addAttribute("message", "모집 성별과 안 맞는 멤버를 선택했어요");
                        return "errorPage";
                    }
                }
                groundReservation = Reservation.createGroundReservation(ground, sessionUser, parseDate, time);
                try {
                    groundReservationService.saveGroundReservation(groundReservation);
                } catch (Exception e) {
                    model.addAttribute("message", "이미 예약된 곳입니다");
                    return "errorPage";
                }
                List<MercenaryMatchUser> mercenaryUsers = new ArrayList<>();
                for (User user : users) {
                    MercenaryMatchUser mercenaryUser = MercenaryMatchUser.createMercenaryUser(user, user.getTeam(), UserStatus.TEAM);
                    mercenaryUsers.add(mercenaryUser);
                }
                MercenaryMatchTeamMercenaryMaxSize mercenaryTeam = MercenaryMatchTeamMercenaryMaxSize.createMercenaryTeam(sessionUser.getTeam(), mercenarySize);
                MercenaryMatch mercenaryMatch = MercenaryMatch.createMercenaryMatch(maxSize, genderEnum, sportsEnum, groundReservation, mercenaryUsers, mercenaryTeam);
                mercenaryMatchService.saveMercenaryMatch(mercenaryMatch);
                model.addAttribute("message", "용병 매치 생성 완료!");
                return "okPage";
            default:
                model.addAttribute("message", "이유를 모르는 에러");
                return "errorPage";
        }


    }


}
