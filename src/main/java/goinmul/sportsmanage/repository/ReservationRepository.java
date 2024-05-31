package goinmul.sportsmanage.repository;



import goinmul.sportsmanage.domain.Reservation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepository {

    private final EntityManager em;

    public void save(Reservation groundReservation) {
        em.persist(groundReservation);
    }

    public Reservation findOne(Long id) {
        return em.find(Reservation.class, id);
    }

    public List<Reservation> findAll() {
        return em.createQuery("select g from Reservation g", Reservation.class).getResultList();
    }

    public List<Reservation> findAllWithGroundWithSocialMatch() {
        return em.createQuery("select r from Reservation r join fetch r.ground join fetch r.socialMatch", Reservation.class).getResultList();
    }

    /*
    public List<Reservation> findAllWithGroundWithSocialMatchByDateByLocationByGender(LocalDate date, String location, Gender gender, Sports sports) {
        return em.createQuery("select r from Reservation r join fetch r.ground join fetch r.socialMatch " +
                        "where r.reservationYmd = :date AND r.ground.location = :location AND r.socialMatch.gender = :gender AND r.socialMatch.sports =: sports" +
                        " order by r.reservationTime desc ", Reservation.class)
                .setParameter("date", date)
                .setParameter("location", location)
                .setParameter("gender", gender)
                .setParameter("sports", sports)
                .getResultList();
    }

    public List<Reservation> findAllWithGroundWithTeamMatchByDateByLocationByGender(LocalDate date, String location, Gender gender, Sports sports) {
        return em.createQuery("select r from Reservation r join fetch r.ground join fetch r.teamMatch " +
                        "where r.reservationYmd = :date AND r.ground.location = :location AND r.teamMatch.gender = :gender AND r.teamMatch.sports =: sports" +
                        " order by r.reservationTime desc ", Reservation.class)
                .setParameter("date", date)
                .setParameter("location", location)
                .setParameter("gender", gender)
                .setParameter("sports", sports)
                .getResultList();
    }
*/

}