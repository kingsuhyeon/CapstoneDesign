package goinmul.sportsmanage.controller;


import goinmul.sportsmanage.domain.SportsInfo;
import goinmul.sportsmanage.repository.socialMatch.SocialMatchRepository;
import goinmul.sportsmanage.service.SportsService;
import goinmul.sportsmanage.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MentoMenteeController {

    private final SportsService sportsService;
    private final UserService userService;
    private final SocialMatchRepository socialMatchRepository;

    @GetMapping("/mentoMentee")
    public String index(@RequestParam String sports, HttpServletRequest request, Model model) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        SportsInfo sportsInfo = sportsService.getSportsInfo("futsal");
        model.addAttribute("userId", userId);
        model.addAttribute("sportsInfo", sportsInfo);
        model.addAttribute("sports", sports);
        return "sports_mento";
    }



}
