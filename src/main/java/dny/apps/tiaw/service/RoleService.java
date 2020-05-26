package dny.apps.tiaw.service;

import java.util.Set;

import dny.apps.tiaw.domain.models.service.RoleServiceModel;

public interface RoleService {
    void seedRolesInDb();
    
    Set<RoleServiceModel> findAllRoles();

    RoleServiceModel findByAuthority(String authority);
}
