package dny.apps.tiaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Card;

public interface CardRepository extends JpaRepository<Card, String> {
}
