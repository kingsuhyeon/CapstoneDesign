package goinmul.sportsmanage.repository.teamMatch;


import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.Sports;
import goinmul.sportsmanage.domain.teamMatch.TeamMatch;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMatchRepository {

    private final EntityManager em;

    public void save(TeamMatch teamMatch) {
        em.persist(teamMatch);
    }

    public TeamMatch findOne(Long id) {
        return em.find(TeamMatch.class, id);
    }

    public TeamMatch findOneWithTeamUserByTeamMatchId(Long id) {
        TeamMatch teamMatch = TeamMatch.createTeamMatch();
        List<TeamMatch> findTeamMatch = em.createQuery("select t from TeamMatch t join fetch t.teamUsers where t.id = :id", TeamMatch.class)
                .setParameter("id", id)
                .getResultList();

        if(!findTeamMatch.isEmpty()){
            teamMatch = findTeamMatch.get(0);
        }

        return teamMatch;

    }

    public List<TeamMatch> findAll() {
        return em.createQuery("select t from TeamMatch t", TeamMatch.class).getResultList();
    }

    public List<TeamMatch> findAllWithAllEntityByDateAndLocationAndGender(LocalDate date, String location, Gender gender, Sports sports) {
        return em.createQuery("select t from TeamMatch t  join fetch t.teamUsers join fetch t.reservation join fetch t.reservation.user join fetch t.reservation.user.team join fetch t.reservation.ground " +
                        "where t.reservation.reservationYmd = :date AND t.reservation.ground.location = :location AND t.gender = :gender AND t.sports =: sports" +
                        " order by t.reservation.reservationTime desc", TeamMatch.class)
                .setParameter("date", date)
                .setParameter("location", location)
                .setParameter("gender", gender)
                .setParameter("sports", sports)
                .getResultList();
    }




}
