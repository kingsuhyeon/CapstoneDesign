package goinmul.sportsmanage.repository;


import goinmul.sportsmanage.domain.Ground;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroundRepository {

    private final EntityManager em;

    public void save(Ground ground) {
        em.persist(ground);
    }

    public Ground findOne(Long id) {
        return em.find(Ground.class, id);
    }

    public List<Ground> findAll() {
        return em.createQuery("select g from Ground g", Ground.class).getResultList();
    }

    public List<Ground> findAllByLocation(String location) {
        return em.createQuery("select g from Ground g where g.location = :location", Ground.class).setParameter("location", location)
                .getResultList();
    }




    /*
    public Ground findGroundByLoginId(String){

        List<Ground> users = em.createQuery("select g from Ground g where g.id = :id", Ground.class).setParameter("id", id).getResultList();

        User user = null;
        if(!users.isEmpty())
            user = users.get(0);

        return user;
    }
*/
}
