package goinmul.sportsmanage.repository;


import goinmul.sportsmanage.domain.Gender;
import goinmul.sportsmanage.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findOne(Long id) {
        return em.find(User.class, id);
    }

    public User findUserWithTeamByUserId(Long id){
        User user = null;
        List<User> users = em.createQuery("select u from User u join fetch u.team where u.id = :id", User.class)
                .setParameter("id", id).getResultList();
        if(!users.isEmpty())
            user = users.get(0);
        return user;
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    public List<User> findByUserIdIn(List<Long> userIdList) {
        return em.createQuery("select u from User u where u.id IN :userIdList", User.class)
                .setParameter("userIdList", userIdList)
                .getResultList();
    }

    public List<User> findWithTeamByUserIdIn(List<Long> userIdList) {
        return em.createQuery("select u from User u join fetch u.team where u.id IN :userIdList", User.class)
                .setParameter("userIdList", userIdList)
                .getResultList();
    }

    public List<User> findAllByTeamId(Long teamId) {
        return em.createQuery("select u from User u where u.team.id = :teamId", User.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public List<User> findAllByTeamIdAndGender(Long teamId, Gender gender) {
        return em.createQuery("select u from User u where u.team.id = :teamId AND u.gender =:gender", User.class)
                .setParameter("teamId", teamId)
                .setParameter("gender", gender)
                .getResultList();
    }

    //외부 조인으로 해야함
    public User findUserWithTeamByLoginId(String loginId){
        List<User> users = em.createQuery("select u from User u left join fetch u.team where u.loginId = :loginId", User.class)
                .setParameter("loginId", loginId)
                .getResultList();

        User user = null;
        if(!users.isEmpty())
            user = users.get(0);

        return user;
    }

}
