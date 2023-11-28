package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "select * from user_table where username = :username", nativeQuery = true)
    public Optional<User> findByUsername(String username);
}
