package goinmul.sportsmanage.service.mercenaryMatch;



import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.Sports;
import goinmul.sportsmanage.domain.mercenaryMatch.*;
import goinmul.sportsmanage.repository.mercenaryMatch.MercenaryMatchRepository;
import goinmul.sportsmanage.repository.mercenaryMatch.MercenaryMatchTeamMercenaryMaxSizeRepository;
import goinmul.sportsmanage.repository.mercenaryMatch.MercenaryMatchUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MercenaryMatchService {

    private final MercenaryMatchRepository mercenaryMatchRepository;
    private final MercenaryMatchUserRepository mercenaryUserRepository;
    private final MercenaryMatchTeamMercenaryMaxSizeRepository mercenaryMatchTeamMercenaryMaxSizeRepository;

    @Transactional
    public void saveMercenaryMatch(MercenaryMatch mercenaryMatch) {
        mercenaryMatchRepository.save(mercenaryMatch);
    }

    public MercenaryMatch findMercenaryMatchById(Long id) {
        return mercenaryMatchRepository.findOne(id);
    }

    // [GET] mercenary/{sports}에서 사용
    public List<MercenaryMatch> findMercenaryMatchWithAllEntityAndTeamListByDateAndLocationAndGender(LocalDate date, String location, Gender gender, Sports sports) {
        List<MercenaryMatch> mercenaryMatches = mercenaryMatchRepository.findAllWithAllEntityByDateAndLocationAndGender(date, location, gender, sports);

        //용병 골라내기
        List<MercenaryMatchUser> mercenaryMatchMercenarys = new ArrayList<>();
        for (MercenaryMatch mercenaryMatch : mercenaryMatches) {
            for (MercenaryMatchUser mercenaryUser : mercenaryMatch.getMercenaryMatchUsers()) {
                if (mercenaryUser.getStatus().equals(UserStatus.MERCENARY)) {
                    mercenaryMatchMercenarys.add(mercenaryUser);
                }
            }
        }

        //WHERE IN 쿼리에 쓸 매치 ID 리스트 만들기
        List<Long> mercenaryMatchIdList = new ArrayList<>();
        for (MercenaryMatch mercenaryMatch : mercenaryMatches) {
            mercenaryMatchIdList.add(mercenaryMatch.getId());
        }
        //mercenaryUser랑 조인돼있음.
        List<MercenaryMatchTeamMercenaryMaxSize> mercenaryMatchTeamMercenaryMaxSizes = mercenaryMatchTeamMercenaryMaxSizeRepository.findWithTeamByMercenaryMatchIdIn(mercenaryMatchIdList);

        //팀 리스트 만들기
        for (MercenaryMatch mercenaryMatch : mercenaryMatches) {
            for (MercenaryMatchTeamMercenaryMaxSize mmtms : mercenaryMatchTeamMercenaryMaxSizes) {
                TeamWithMercenary teams = TeamWithMercenary.createTeamWithMercenary(mmtms.getTeam(), mmtms.getMaxSize());
                mercenaryMatch.getTeamsWithMercenary().add(teams);
                for (MercenaryMatchUser mercenaryMatchMercenary : mercenaryMatchMercenarys) {
                    if (mercenaryMatchMercenary.getTeam().getId().equals(teams.getTeam().getId()))
                        teams.getUsers().add(mercenaryMatchMercenary);
                }
            }
        }

        return mercenaryMatches;
    }

    public MercenaryMatch findWithMercenaryMaxSizeAndMercenaryUser(Long mercenaryMatchId) {
        MercenaryMatch mercenaryMatchWithMercenaryUser = mercenaryMatchRepository.findWithMercenaryUserByMercenaryMatchId(mercenaryMatchId);
        List<MercenaryMatchTeamMercenaryMaxSize> mercenaryMaxSizeWithTeam = mercenaryMatchTeamMercenaryMaxSizeRepository.findWithTeamByMercenaryMatchId(mercenaryMatchId);

        List<Long> teamIdList = new ArrayList<>();

        List<MercenaryMatchUser> teamA = new ArrayList<>();
        List<MercenaryMatchUser> teamB = new ArrayList<>();


        //3중첩 for문: 최대 40회연산이라 개선함 (40->24)

        //최대 2회 연산
        for (MercenaryMatchTeamMercenaryMaxSize mmswm : mercenaryMaxSizeWithTeam) {
            TeamWithMercenary teamWithMercenary = TeamWithMercenary.createTeamWithMercenary(mmswm.getTeam(), mmswm.getMaxSize());
            mercenaryMatchWithMercenaryUser.getTeamsWithMercenary().add(teamWithMercenary);
            teamIdList.add(mmswm.getTeam().getId());
        }
        System.out.println("teamIdList.toString() = " + teamIdList.toString());
        System.out.println("mercenaryMatchWithMercenaryUser.getMercenaryMatchUsers().size() = " + mercenaryMatchWithMercenaryUser.getMercenaryMatchUsers().size());
        //최대 20회 연산
        for (MercenaryMatchUser mercenaryMatchUser : mercenaryMatchWithMercenaryUser.getMercenaryMatchUsers()) {
            if(mercenaryMatchUser.getStatus().equals(UserStatus.MERCENARY)) {
                System.out.println("실행 됨");
                if(mercenaryMatchUser.getTeam().getId().equals(teamIdList.get(0))) teamA.add(mercenaryMatchUser);
                else if(mercenaryMatchUser.getTeam().getId().equals(teamIdList.get(1))) teamB.add(mercenaryMatchUser);
            }
        }
        System.out.println("teamIdList.size() = " + teamIdList.size());
        System.out.println("teamA.size() = " + teamA.size());
        System.out.println("teamB.size() = " + teamB.size());
        //최대 2회 연산
        for (MercenaryMatchTeamMercenaryMaxSize mmswm : mercenaryMaxSizeWithTeam) {
            TeamWithMercenary teamWithMercenary = TeamWithMercenary.createTeamWithMercenary(mmswm.getTeam(), mmswm.getMaxSize());
            mercenaryMatchWithMercenaryUser.getTeamsWithMercenary().add(teamWithMercenary);
            if(mmswm.getTeam().getId().equals(teamIdList.get(0))){
                mercenaryMatchWithMercenaryUser.getTeamsWithMercenary().get(0).getUsers().addAll(teamA);
            }else mercenaryMatchWithMercenaryUser.getTeamsWithMercenary().get(1).getUsers().addAll(teamB);
        }

        return mercenaryMatchWithMercenaryUser;
    }

    public List<MercenaryMatch> findMercenaryMatchs() {
        return mercenaryMatchRepository.findAll();
    }


}
