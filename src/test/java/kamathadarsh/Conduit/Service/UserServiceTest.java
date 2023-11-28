package kamathadarsh.Conduit.Service;

import jakarta.transaction.Transactional;
import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.UserUpdateRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.ProfileResponse;
import kamathadarsh.Conduit.Response.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    void setUp() {


        User testuser1 = User.builder()
                .username("testuser1")
                .followingUserUsernameList(new HashSet<>())
                .build();

        User testuser2 = User.builder()
                .username("testuser2")
                .followingUserUsernameList(new HashSet<>())
                .build();

        Mockito.when(userRepository.findByUsername("testuser1")).thenReturn(Optional.ofNullable(testuser1));
        Mockito.when(userRepository.findByUsername("testuser2")).thenReturn(Optional.ofNullable(testuser2));
    }

    @Test
//    @Disabled
    @DisplayName("successful follow request.")
    public void successful_follow_request(){

        User testuser1 = userRepository.findByUsername("testuser1").get();
        User testuser2 = userRepository.findByUsername("testuser2").get();

        CustomResponse response = userService.followUser("testuser1", "testuser2");

        assertTrue(response instanceof ProfileResponse);
        assertTrue(testuser1.getFollowingUserUsernameList().contains("testuser2"));

    }

    @Test
    @DisplayName("unsuccessful follow/unfollow request, user to be followed doesn't exist.")
    public void unsuccessfulFollowAndUnfollowRequest_UserToBeFollowedDoesntExist(){


        CustomResponse followResponse = userService.followUser("testuser1", "testuser3");
        CustomResponse unfollowResponse = userService.unfollowUser("testuser1", "testuser3");

        assertTrue(followResponse instanceof FailureResponse);
        assertTrue(unfollowResponse instanceof FailureResponse);
    }

    @Test
    @DisplayName("unsuccessful follow/unfollow request, follower user doesn't exist.")
    public void unsuccessfulFollowAndUnfollowRequest_FollowerUserDoesntExist(){

        CustomResponse followResponse = userService.followUser("testuser3", "testuser1");
        CustomResponse unfollowResponse = userService.unfollowUser("testuser3", "testuser1");

        assertTrue(followResponse instanceof FailureResponse);
        assertTrue(unfollowResponse instanceof FailureResponse);
    }

    @Test
//    @Disabled
    @DisplayName("successful unfollow request.")
    public void successful_unfollow_request(){

        User testuser1 = userRepository.findByUsername("testuser1").get();
        User testuser2 = userRepository.findByUsername("testuser2").get();

        userService.followUser("testuser1", "testuser2");

        assertTrue(testuser1.getFollowingUserUsernameList().contains("testuser2"));

        CustomResponse response = userService.unfollowUser("testuser1", "testuser2");

        assertTrue(response instanceof ProfileResponse);
        assertFalse(testuser1.getFollowingUserUsernameList().contains("testuser2"));


    }


    @Test
    @DisplayName("successful user update.")
    public void userUpdateRequest(){

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .user(User.builder()
                        .image("insert new image here")
                        .bio("insert new bio here")
                        .emailId("insert new email id here")
                        .build() )
                .build();

        CustomResponse response = userService.userUpdate("testuser1", userUpdateRequest);

        User testuser1 = userRepository.findByUsername("testuser1").get();

        Assertions.assertEquals(response instanceof UserResponse, true);
        Assertions.assertEquals("insert new bio here", testuser1.getBio());
        Assertions.assertEquals("insert new email id here", testuser1.getEmailId());
        Assertions.assertEquals("insert new image here", testuser1.getImage());


    }






}