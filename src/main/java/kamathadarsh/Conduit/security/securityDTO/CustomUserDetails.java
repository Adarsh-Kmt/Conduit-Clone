package kamathadarsh.Conduit.security.securityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {

    private UserTable user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority("read");

        return List.of(grantedAuthority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
