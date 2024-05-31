package goinmul.sportsmanage.service;


import goinmul.sportsmanage.domain.Reservation;
import goinmul.sportsmanage.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository groundReservationRepository;

    @Transactional
    public void saveGroundReservation(Reservation groundReservation) {
        groundReservationRepository.save(groundReservation);
    }

    public Reservation findGroundReservationById(Long id) {
        return groundReservationRepository.findOne(id);
    }

    public List<Reservation> findGroundReservations() {
        return groundReservationRepository.findAll();
    }

    public List<Reservation> findGroundReservationsByDateAndLocationAndGender() {
        return groundReservationRepository.findAll();
    }

    public List<Reservation> findAllWithGroundWithSocialMatch() {
        return groundReservationRepository.findAllWithGroundWithSocialMatch();
    }

    /*
    public List<Reservation> findReservationWithGroundWithSocialMatchByDateByLocationByGender(LocalDate date, String location, Gender gender, Sports sports) {
        return groundReservationRepository.findAllWithGroundWithSocialMatchByDateByLocationByGender(date, location, gender, sports);
    }

    public List<Reservation> findReservationWithGroundWithTeamMatchByDateByLocationByGender(LocalDate date, String location, Gender gender, Sports sports) {
        return groundReservationRepository.findAllWithGroundWithTeamMatchByDateByLocationByGender(date, location, gender, sports);
    }

     */
}
