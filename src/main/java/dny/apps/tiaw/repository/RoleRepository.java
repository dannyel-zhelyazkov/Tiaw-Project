package dny.apps.tiaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByAuthority(String authority);
}
