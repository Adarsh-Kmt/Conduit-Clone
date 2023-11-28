package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.User;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("successful user record retrieval")
    public void successful_user_record_retrieval(){

        User user = User.builder()
                .username("testuser")
                .password("1234")
                .bio("test bio")
                .emailId("test email")
                .image("test image")
                .build();

        userRepository.save(user);

        User testuser = userRepository.findByUsername("testuser").get();

        Assertions.assertEquals(testuser.getUsername(), "testuser");

    }

}