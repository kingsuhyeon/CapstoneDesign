package goinmul.sportsmanage.controller;


import goinmul.sportsmanage.domain.SportsInfo;
import goinmul.sportsmanage.domain.User;
import goinmul.sportsmanage.service.SportsService;
import goinmul.sportsmanage.service.TeamService;
import goinmul.sportsmanage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final TeamService teamService;
    private final SportsService sportsService;

    @GetMapping("/user/signup")
    public String createUserForm() {
        return "user/signup";
    }

    @PostMapping("/user/signup")
    @ResponseBody
    public String createUser(@ModelAttribute User user) {
        user.setRegdate(LocalDateTime.now());
        String result = userService.saveUser(user);

        return result;
    }

    @GetMapping("/user/signin")
    public String loginForm(Model model) {
        boolean loginResult = true; // 모델 빈 껍데기 심으려고 작성했어요.
        model.addAttribute("loginResult", loginResult);
        return "user/signin";
    }

    @PostMapping("/user/signin")
    public String login(@ModelAttribute User user, HttpServletRequest request, Model model) {
        boolean loginResult = userService.validateLoginMember(user.getLoginId(), user.getPassword());

        if (loginResult) {
            User findUser = userService.findUserWithTeamByLoginId(user.getLoginId());
            request.getSession().setAttribute("user", findUser);
            model.addAttribute("loginResult", loginResult);
            return "redirect:/";
        } else {
            String loginFailMessage = "로그인 실패";
            model.addAttribute("loginResult", loginResult);
            return "user/signin";
        }
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        return "redirect:/";
    }

    @GetMapping("/user/findid")
    public String findAccountForm(Model model) {
        boolean findidResult = true; // 모델 빈 껍데기 심으려고 작성했어요.
        model.addAttribute("findidResult", findidResult);
        return "user/findid";
    }

    @GetMapping("/user/myPage")
    public String myPage(Model model, HttpSession httpSession) {
        User sessionUser = (User) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return "pleaseLogin";
        }
        SportsInfo sportsInfo = sportsService.getSportsInfo("futsal");
        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("user", sessionUser);
        return "user/myPage";
    }


}
