package goinmul.sportsmanage.service;

import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.User;
import goinmul.sportsmanage.domain.teamMatch.Team;
import goinmul.sportsmanage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String saveUser(User user) {
        String result = validateDuplicateMember(user.getLoginId());

        if (result.equals("회원가입 성공"))
            userRepository.save(user);

        return result;
    }

    @Transactional
    public void changeTeam(User user, Team team) {
        user.changeTeam(team);
    }

    public List<User> findUserByIdIn(List<Long> userIdList) {
        return userRepository.findByUserIdIn(userIdList);
    }


    public List<User> findUserWithTeamByUserIdIn(List<Long> userIdList) {
        return userRepository.findWithTeamByUserIdIn(userIdList);
    }

    public List<User> findUsersByTeamIdAndGender(Long teamId, Gender gender) {
        return userRepository.findAllByTeamIdAndGender(teamId, gender);
    }

    public User findUserWithTeamByUserId(Long id) {
        return userRepository.findUserWithTeamByUserId(id);
    }

    public User findUserById(Long id) {
        return userRepository.findOne(id);
    }

    public User findUserWithTeamByLoginId(String loginId) {
        return userRepository.findUserWithTeamByLoginId(loginId);
    }

    public List<User> findUsersByTeamId(Long teamId) {
        return userRepository.findAllByTeamId(teamId);
    }


    //회원 가입 검증
    private String validateDuplicateMember(String loginId) {
        User user = userRepository.findUserWithTeamByLoginId(loginId);
        String result = "";
        if (user == null)
            result = "회원가입 성공";
        else
            result = "이미 사용중인 아이디입니다.";
        return result;
    }

    //로그인 검증
    public Boolean validateLoginMember(String loginId, String password) {
        User findMember = userRepository.findUserWithTeamByLoginId(loginId);
        boolean result = false;
        if (findMember != null) {
            if (findMember.getPassword().equals(password))
                result = true;
        }
        return result;
    }
}
