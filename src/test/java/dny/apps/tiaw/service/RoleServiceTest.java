package dny.apps.tiaw.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import dny.apps.tiaw.domain.entities.Role;
import dny.apps.tiaw.domain.models.service.RoleServiceModel;
import dny.apps.tiaw.error.role.RoleNotFoundException;
import dny.apps.tiaw.repository.RoleRepository;

class RoleServiceTest extends BaseServiceTest {
	@Autowired
	private RoleService service;
	
	@MockBean
	private RoleRepository roleRepository;

	@Test
	void testFindAllRoles() {
		Role user = new Role("ROLE_USER");
		Role mod = new Role("ROLE_MODERATOR");
		Role admin = new Role("ROLE_ADMIN");
		Role root = new Role("ROLE_ROOT");
		
		List<Role> roles = new ArrayList<>();
		roles.add(user);
		roles.add(mod);
		roles.add(admin);
		roles.add(root);
		
		Mockito.when(this.roleRepository.findAll())
			.thenReturn(roles);
		
		Set<RoleServiceModel> roleServiceModels = this.service.findAllRoles();
		
		assertEquals(roleServiceModels.size(), 4);
	}

	@Test
	void testFindByAuthority() {
		Optional<Role> role = Optional.of(new Role("ROLE_ADMIN"));
		
		Mockito.when(this.roleRepository.findByAuthority("ROLE_ADMIN"))
			.thenReturn(role);
		
		RoleServiceModel roleServiceModel = this.service.findByAuthority("ROLE_ADMIN");
		
		assertEquals(roleServiceModel.getAuthority(), "ROLE_ADMIN");
		
		Exception exceptionRole = assertThrows(RoleNotFoundException.class, () ->
			this.service.findByAuthority("WRONG_ROLE")
		);
		
		assertEquals(exceptionRole.getMessage(), "Role with given name was not foud!");
	}

}
