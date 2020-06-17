package dny.apps.tiaw.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dny.apps.tiaw.domain.entities.GameAcc;

public interface GameAccRepository extends JpaRepository<GameAcc, String> {
	
	@Query("select ga from GameAcc ga where ga.defenseDeck != null")
	Page<GameAcc> findAllByDefenseDeckNotNull(PageRequest pageRequest);
}
