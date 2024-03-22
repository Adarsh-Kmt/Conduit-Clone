package kamathadarsh.Conduit.security.service;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.Conduit.jooqRepository.JOOQUserRepository;
import kamathadarsh.Conduit.security.securityDTO.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JOOQUserRepository jooqUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserTable> userExists = jooqUserRepository.getSecurityInformation(username);


        if(userExists.isEmpty()){
            throw new UsernameNotFoundException("user with username " + username + " not found.");
        }

        System.out.println("user found with username " + userExists.get().getUsername());
        return new CustomUserDetails(userExists.get());

    }
}
