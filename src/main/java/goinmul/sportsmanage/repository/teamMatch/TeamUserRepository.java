package goinmul.sportsmanage.repository.teamMatch;


import goinmul.sportsmanage.domain.teamMatch.TeamUser;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamUserRepository {

    private final EntityManager em;

    public void save(TeamUser teamUser) {
        em.persist(teamUser);
    }

    public TeamUser findOne(Long id) {
        return em.find(TeamUser.class, id);
    }

    public List<TeamUser> findAll() {
        return em.createQuery("select t from TeamUser t", TeamUser.class).getResultList();
    }

    public List<TeamUser> findWithUserAndTeamByTeamMatchIdIn(List<Long> teamMatchIdList) {
        return em.createQuery("select t from TeamUser t join fetch t.user join fetch t.user.team" +
                        " where t.teamMatch.id IN :id", TeamUser.class)
                .setParameter("id", teamMatchIdList)
                .getResultList();
    }


    public TeamUser finOneByTeamMatchIdAndUserId(Long teamMatchId, Long userId) {
        TeamUser teamUser = null;
        List<TeamUser> result = em.createQuery("select t from TeamUser t where t.teamMatch.id =: teamMatchId AND t.user.id =:userId", TeamUser.class)
                .setParameter("teamMatchId", teamMatchId)
                .setParameter("userId", userId)
                .getResultList();

        if (!result.isEmpty())
            teamUser = result.get(0);

        return teamUser;
    }


}
