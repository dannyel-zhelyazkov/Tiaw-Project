package dny.apps.tiaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import dny.apps.tiaw.domain.entities.Deck;

public interface DeckRepository extends JpaRepository<Deck, String>, CrudRepository<Deck, String> {
}
