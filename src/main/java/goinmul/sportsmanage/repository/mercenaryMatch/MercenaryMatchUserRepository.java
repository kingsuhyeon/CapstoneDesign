package goinmul.sportsmanage.repository.mercenaryMatch;


import goinmul.sportsmanage.domain.mercenaryMatch.MercenaryMatchUser;
import goinmul.sportsmanage.domain.mercenaryMatch.UserStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MercenaryMatchUserRepository {

    private final EntityManager em;

    public void save(MercenaryMatchUser mercenaryUser) {
        em.persist(mercenaryUser);
    }

    public MercenaryMatchUser findOne(Long id) {
        return em.find(MercenaryMatchUser.class, id);
    }

    public List<MercenaryMatchUser> findAll() {
        return em.createQuery("select m from MercenaryMatchUser m", MercenaryMatchUser.class).getResultList();
    }
    public List<MercenaryMatchUser> findByMercenaryMatchId(Long id) {
        return em.createQuery("select m from MercenaryMatchUser m where m.mercenaryMatch.id =: id", MercenaryMatchUser.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<MercenaryMatchUser> findMercenaryWithUserAndTeamByMercenaryMatchIdInByUserSatus(List<Long> mercenaryMatchIdList) {
        return em.createQuery("select m from MercenaryMatchUser m join fetch m.user join fetch m.user.team" +
                        " where m.status =: status AND m.mercenaryMatch.id IN :id", MercenaryMatchUser.class)
                .setParameter("status", UserStatus.MERCENARY)
                .setParameter("id", mercenaryMatchIdList)
                .getResultList();
    }

    public MercenaryMatchUser finOneByMercenaryMatchIdAndUserId(Long mercenaryMatchId, Long userId) {
        MercenaryMatchUser mercenaryUser = null;
        List<MercenaryMatchUser> result = em.createQuery("select m from MercenaryMatchUser m where m.mercenaryMatch.id =: mercenaryMatchId AND m.user.id =:userId", MercenaryMatchUser.class)
                .setParameter("mercenaryMatchId", mercenaryMatchId)
                .setParameter("userId", userId)
                .getResultList();
        if (!result.isEmpty()) {mercenaryUser = result.get(0);}
        return mercenaryUser;
    }


}
