package dny.apps.tiaw.service;

import dny.apps.tiaw.domain.models.service.RoleServiceModel;

import java.util.Set;

public interface RoleService {
    void seedRolesInDb();
    
    Set<RoleServiceModel> findAllRoles();

    RoleServiceModel findByAuthority(String authority);
}
