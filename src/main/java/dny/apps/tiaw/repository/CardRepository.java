package dny.apps.tiaw.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Card;

public interface CardRepository extends JpaRepository<Card, String> {
	Optional<Card> findByName(String name);
}
