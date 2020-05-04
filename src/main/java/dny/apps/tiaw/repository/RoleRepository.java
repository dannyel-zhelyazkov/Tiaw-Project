package dny.apps.tiaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dny.apps.tiaw.domain.entities.Role;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByAuthority(String authority);
}
