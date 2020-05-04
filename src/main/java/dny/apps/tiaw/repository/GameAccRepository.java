package dny.apps.tiaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.GameAcc;

public interface GameAccRepository extends JpaRepository<GameAcc, String> {
}
