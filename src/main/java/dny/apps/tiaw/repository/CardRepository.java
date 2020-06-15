package dny.apps.tiaw.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Card;
import dny.apps.tiaw.domain.entities.Rarity;

public interface CardRepository extends JpaRepository<Card, String> {
	Optional<Card> findByName(String name);
	
	Page<Card> findAllByRarity(Pageable pagable, Rarity rarity);
}
