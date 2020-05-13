package dny.apps.tiaw.domain.entities;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.Set;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
	private String username;
	private String password;
	private String email;
	private GameAcc gameAcc;

	private Set<Role> authorities;

	public User() {}

	@Override
	@Column(name = "username", nullable = false, unique = true, updatable = false)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	@Column(name = "password", nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	@Column(name = "email", nullable = false, unique = true)
	public void setEmail(String email) {
		this.email = email;
	}
	@OneToOne
	public GameAcc getGameAcc() {
		return gameAcc;
	}
	public void setGameAcc(GameAcc gameAcc) {
		this.gameAcc = gameAcc;
	}
	@Override
	@ManyToMany(fetch = FetchType.EAGER)
	public Set<Role> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Role> authorities) {
		this.authorities = authorities;
	}
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	@Transient
	public boolean isEnabled() {
		return true;
	}
}
