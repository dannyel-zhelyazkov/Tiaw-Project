package dny.apps.tiaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Fight;

public interface FightRepository extends JpaRepository<Fight, String>{

}
