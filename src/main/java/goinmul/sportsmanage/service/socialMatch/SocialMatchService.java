package goinmul.sportsmanage.service.socialMatch;


import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.Sports;
import goinmul.sportsmanage.domain.socialMatch.SocialMatch;
import goinmul.sportsmanage.repository.socialMatch.SocialMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialMatchService {

    private final SocialMatchRepository socialMatchRepository;

    @Transactional
    public void saveSocialMatch(SocialMatch socialMatch) {
        socialMatchRepository.save(socialMatch);
    }

    public SocialMatch findSocialMatchById(Long id) {
        return socialMatchRepository.findOne(id);
    }

    public List<SocialMatch> findSocialMatchs() {
        return socialMatchRepository.findAll();
    }

    public List<SocialMatch> findSocialMatchWithReservationAndGroundByDateAndLocationAndGender(LocalDate date, String location, Gender gender, Sports sports) {
        return socialMatchRepository.findAllWithReservationAndGroundByDateAndLocationAndGender(date, location, gender, sports);
    }

    /*
    public List<SocialMatch> findGroundsById(String location) {
        return socialMatchRepository.findAllByLocation(location);
    }
    */

}
