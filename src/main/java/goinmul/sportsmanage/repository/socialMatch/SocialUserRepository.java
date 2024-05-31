package goinmul.sportsmanage.repository.socialMatch;

import goinmul.sportsmanage.domain.socialMatch.SocialMatch;
import goinmul.sportsmanage.domain.socialMatch.SocialUser;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SocialUserRepository {

    private final EntityManager em;

    public void save(SocialUser socialUser) {
        em.persist(socialUser);
    }

    public SocialUser findOne(Long id) {
        return em.find(SocialUser.class, id);
    }

    public List<SocialUser> findAll() {
        return em.createQuery("select s from SocialUser s", SocialUser.class).getResultList();
    }

    public List<SocialUser> findAllBySocialMatchId(Long socialMatchId) {
        List<SocialUser> findSocialUser = em.createQuery("select s from SocialUser s where s.socialMatch.id =: socialMatchId", SocialUser.class)
                .setParameter("socialMatchId", socialMatchId)
                .getResultList();

        return findSocialUser;
    }


    public SocialUser finOneBySocialMatchIdAndUserId(Long socialMatchId, Long userId) {
        SocialUser socialUser = null;
        List<SocialUser> result = em.createQuery("select s from SocialUser s where s.socialMatch.id =: socialMatchId AND s.user.id =:userId", SocialUser.class)
                .setParameter("socialMatchId", socialMatchId)
                .setParameter("userId", userId)
                .getResultList();

        if (!result.isEmpty())
            socialUser = result.get(0);

        return socialUser;
    }


    //select from SocialUser where SocialMatch in(ALL SocialMatchId)
    public List<SocialMatch> findSocialUser() {
        List<SocialMatch> socialMatch = em.createQuery("select s from SocialMatch s", SocialMatch.class).getResultList();
        return socialMatch;
    }

    public HashMap<Long, List<SocialUser>> findSocialUserMap() {
        HashMap<Long, List<SocialUser>> map = new HashMap<>();
        List<SocialMatch> socialMatchs = em.createQuery("select s from SocialMatch s", SocialMatch.class).getResultList();

        for (SocialMatch socialMatch : socialMatchs) {
            map.put(socialMatch.getId(), socialMatch.getSocialUsers());
        }
        return map;
    }
}
