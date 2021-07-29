package museium;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomerDetails implements UserDetails{
	private Customer user;
	
	public CustomerDetails(Customer c) {
        this.user = c;
    }
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
	
	public String getPassword() {
        return user.getPassword();
    }

    public String getUsername() {
        return user.getEmail();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }
 
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    public boolean isEnabled() {
        return true;
    }
     
    public String getFullName() {
        return user.getFirstName() + " " + user.getLastName();
    }
}
