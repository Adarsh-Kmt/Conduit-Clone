package kamathadarsh.Conduit.security.securityDTO;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority {

    private String authorityString;
    @Override
    public String getAuthority() {
        return authorityString;
    }
}
