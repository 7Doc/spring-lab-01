package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.AppUser;
import kz.iitu.spring_lab_01.catalog.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AppUserRepositoryTest {

    @Autowired
    AppUserRepository repository;

    @Autowired
    TestEntityManager em;

    @Test
    void findsUserByExactUsername() {
        em.persist(new AppUser("alice", "{noop}pw", Set.of("ROLE_USER")));
        em.persist(new AppUser("bob",   "{noop}pw", Set.of("ROLE_ADMIN")));
        em.flush();
        em.clear();

        var found = repository.findByUsername("alice");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("alice");
        assertThat(found.get().getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void doesNotFindUserWithDifferentUsername() {
        em.persist(new AppUser("alice", "{noop}pw", Set.of("ROLE_USER")));
        em.flush();
        em.clear();

        var found = repository.findByUsername("carol");

        assertThat(found).isEmpty();
    }
}