package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Exception.UserNotFoundException;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.CreateUserRequest;
import kamathadarsh.Conduit.Request.UserUpdateRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.ProfileResponse;
import kamathadarsh.Conduit.Response.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CustomResponse followUser(String followerUsername, String toBeFollowedUsername){


        try{
            Optional<User> userToBeFollowed = userRepository.findByUsername(toBeFollowedUsername);

            Optional<User> userThatFollows = userRepository.findByUsername(followerUsername);

            if(!userToBeFollowed.isPresent()) throw new UserNotFoundException("user with Username " + toBeFollowedUsername + " not found.");
            if(!userThatFollows.isPresent()) throw new UserNotFoundException("user with Username " + followerUsername + " not found.");


            Set<String> FollowingList = userThatFollows.get().getFollowingUserUsernameList();
            Set<String> FollowerList = userToBeFollowed.get().getFollowerUserUsernameList();

            FollowingList.add(toBeFollowedUsername);
            FollowerList.add(followerUsername);

            userRepository.save(userThatFollows.get());

            return new ProfileResponse(
                    toBeFollowedUsername,
                    userToBeFollowed.get().getBio(),
                    userToBeFollowed.get().getImage(),
                    true
            );



        }
        catch (UserNotFoundException e) {

            return FailureResponse.builder()
                    .Exception(e)
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();


        }


    }

    public CustomResponse unfollowUser(String followerUsername, String toBeFollowedUsername) {


        try{

            Optional<User> followerUserExists = userRepository.findByUsername(followerUsername);
            Optional<User> userToBeFollowedExists = userRepository.findByUsername(toBeFollowedUsername);

            if(!followerUserExists.isPresent()) throw new UserNotFoundException("user with id " + followerUsername + " not found");
            if(!userToBeFollowedExists.isPresent()) throw new UserNotFoundException("user with id " + toBeFollowedUsername + " not found");

            User followerUser = followerUserExists.get();
            User userToBeFollowed = userToBeFollowedExists.get();

            Set<String> followingList = followerUser.getFollowingUserUsernameList();
            Set<String> followerList = userToBeFollowed.getFollowerUserUsernameList();

            if(followingList.contains(toBeFollowedUsername) == false){

                return FailureResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("you didn't follow the user in the first place.")
                        .build();
            }
            followingList.remove(toBeFollowedUsername);
            followerList.remove(followerUsername);
            userRepository.save(followerUser);

            return new ProfileResponse(
                    toBeFollowedUsername,
                    userToBeFollowed.getBio(),
                    userToBeFollowed.getImage(),
                    false
            );
        }
        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .Exception(e)
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public CustomResponse getProfile(String username, String currUserUsername) {

        try{

            Optional<User> user = userRepository.findByUsername(username);

            Optional<User> currUser = userRepository.findByUsername(currUserUsername);

            if(!user.isPresent()) throw new UserNotFoundException("user with username " + username + " not found.");
            if(!currUser.isPresent()) throw new UserNotFoundException("user with username " + currUserUsername + " not found.");

            boolean isFollowing = currUser.get().getFollowingUserUsernameList().contains(username);

            return new ProfileResponse(
                    username,
                    user.get().getBio(),
                    user.get().getImage(),
                    isFollowing

            );

        }
        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .Exception(e)
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public CustomResponse userUpdate(String username, UserUpdateRequest userUpdateRequest){



        try{
            User updatedUserDetails = userUpdateRequest.getUser();

            Optional<User> currUserExists = userRepository.findByUsername(username);

            if(!currUserExists.isPresent()) throw new UserNotFoundException("user with username " + username + " not found");

            User currUser = currUserExists.get();

            if(updatedUserDetails.getBio() != null && !updatedUserDetails.getBio().isBlank()){

                currUser.setBio(updatedUserDetails.getBio());
            }
            if(updatedUserDetails.getUsername() != null && !updatedUserDetails.getUsername().isBlank()){

                currUser.setUsername(updatedUserDetails.getUsername());

            }
            if(updatedUserDetails.getImage() != null && !updatedUserDetails.getImage().isBlank()){

                currUser.setImage(updatedUserDetails.getImage());

            }
            if(updatedUserDetails.getPassword() != null && !updatedUserDetails.getPassword().isBlank()){

                currUser.setPassword(updatedUserDetails.getPassword());
            }
            if(updatedUserDetails.getEmailId() != null && !updatedUserDetails.getEmailId().isBlank()){

                currUser.setEmailId(updatedUserDetails.getEmailId());
            }

            userRepository.save(currUser);

            return UserResponse.builder()
                    .bio(currUser.getBio())
                    .image(currUser.getImage())
                    .email(currUser.getEmailId())
                    .username(currUser.getUsername())
                    .build();


        }

        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .Exception(e)
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();

        }

    }

    public User createUser(CreateUserRequest createUserRequest){

        User user = User.builder()
                .username(createUserRequest.getUsername())
                .followingUserUsernameList(new HashSet<>())
                .followerUserUsernameList(new HashSet<>())
                .favouriteArticleList(new HashSet<>())
                .emailId(createUserRequest.getEmailId())
                .password(createUserRequest.getPassword())
                .bio(createUserRequest.getBio())
                .image(createUserRequest.getImageLink())
                .build();

        userRepository.save(user);
        return user;
    }


}
