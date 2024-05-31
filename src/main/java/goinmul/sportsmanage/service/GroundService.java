package goinmul.sportsmanage.service;

import goinmul.sportsmanage.domain.Ground;
import goinmul.sportsmanage.repository.GroundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroundService {

    private final GroundRepository groundRepository;

    @Transactional
    public void saveGround(Ground ground) {
        groundRepository.save(ground);
    }

    public Ground findGroundById(Long id) {
        return groundRepository.findOne(id);
    }

    public List<Ground> findGrounds() {
        return groundRepository.findAll();
    }

    public List<Ground> findGroundsById(String location) {
        return groundRepository.findAllByLocation(location);
    }


}
