package goinmul.sportsmanage.controller;


import goinmul.sportsmanage.domain.User;
import goinmul.sportsmanage.domain.teamMatch.Team;
import goinmul.sportsmanage.service.TeamService;
import goinmul.sportsmanage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TeamController {

    private final UserService userService;
    private final TeamService teamService;

    @GetMapping("/team")
    public String createTeamForm() {
        return "team/createTeamForm";
    }

    @PostMapping("/team")
    public String createTeam(@RequestParam String name, HttpServletRequest request, Model model) {
        String message = "";
        User sessionUser = (User) request.getSession().getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        Team team = Team.createTeam(name);
        boolean result = teamService.saveTeam(team);

        if (result) {
            userService.changeTeam(sessionUser, team);
            model.addAttribute("message", "팀 생성 완료, 생성한 팀에 자동으로 가입됩니다.");
            return "okPage";
        } else {
            model.addAttribute("message", "이미 존재하는 팀 이름입니다");
            return "errorPage";
        }
    }
}
