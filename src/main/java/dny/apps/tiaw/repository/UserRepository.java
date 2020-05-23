package dny.apps.tiaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

}
