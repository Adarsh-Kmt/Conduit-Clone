package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Exception.UserAlreadyExistsException;
import kamathadarsh.Conduit.Exception.UserNotFoundException;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.Conduit.jooqRepository.JOOQUserRepository;

import kamathadarsh.Conduit.Request.CreateUserRequest;
import kamathadarsh.Conduit.Request.UserUpdateRequest;

import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.ProfileResponse;
import kamathadarsh.Conduit.Response.UserResponse;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {


    private final JOOQUserRepository jooqUserRepository;


    public CustomResponse followUser(String followerUsername, String toBeFollowedUsername){


        try{
            Optional<UserTable> userToBeFollowed = jooqUserRepository.findByUsername(toBeFollowedUsername);

            Optional<UserTable> userThatFollows = jooqUserRepository.findByUsername(followerUsername);

            if(!userToBeFollowed.isPresent()) throw new UserNotFoundException("user with Username " + toBeFollowedUsername + " not found.");
            if(!userThatFollows.isPresent()) throw new UserNotFoundException("user with Username " + followerUsername + " not found.");


            jooqUserRepository.followUser(followerUsername, toBeFollowedUsername);

            return new ProfileResponse(
                    toBeFollowedUsername,
                    userToBeFollowed.get().getBio(),
                    userToBeFollowed.get().getImage(),
                    true
            );



        }
        catch (UserNotFoundException e) {

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();


        }


    }

    public CustomResponse unfollowUser(String followerUsername, String toBeUnfollowedUsername) {


        try{

            Optional<UserTable> followerUserExists = jooqUserRepository.findByUsername(followerUsername);
            Optional<UserTable> userToBeFollowedExists = jooqUserRepository.findByUsername(toBeUnfollowedUsername);

            if(!followerUserExists.isPresent()) throw new UserNotFoundException("user with id " + followerUsername + " not found");
            if(!userToBeFollowedExists.isPresent()) throw new UserNotFoundException("user with id " + toBeUnfollowedUsername + " not found");

            UserTable userToBeFollowed = userToBeFollowedExists.get();


            if(!jooqUserRepository.checkIfUserFollowsAnotherUser(followerUsername, toBeUnfollowedUsername)){

                return FailureResponse.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message("you didn't follow the user in the first place.")
                        .build();
            }

            jooqUserRepository.unfollowUser(followerUsername, toBeUnfollowedUsername);

            return new ProfileResponse(
                    toBeUnfollowedUsername,
                    userToBeFollowed.getBio(),
                    userToBeFollowed.getImage(),
                    false
            );
        }
        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public CustomResponse getProfile(String username, String currUserUsername) {

        try{

            Optional<UserTable> user = jooqUserRepository.findByUsername(username);

            Optional<UserTable> currUser = jooqUserRepository.findByUsername(currUserUsername);

            if(!user.isPresent()) throw new UserNotFoundException("user with username " + username + " not found.");
            if(!currUser.isPresent()) throw new UserNotFoundException("user with username " + currUserUsername + " not found.");

            boolean isFollowing = jooqUserRepository.checkIfUserFollowsAnotherUser(currUserUsername, username);

            return new ProfileResponse(
                    username,
                    user.get().getBio(),
                    user.get().getImage(),
                    isFollowing

            );

        }
        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public CustomResponse userUpdate(String currUserUsername, UserUpdateRequest userUpdateRequest){



        try{


            Optional<UserTable> currUserExists = jooqUserRepository.findByUsername(currUserUsername);

            if(!currUserExists.isPresent()) throw new UserNotFoundException("user with username " + currUserUsername + " not found");

            String finalUsername = currUserUsername;

            jooqUserRepository.updateUser(currUserUsername, userUpdateRequest);


            UserTable updatedUser = jooqUserRepository.findByUsername(finalUsername).get();

            return UserResponse.builder()
                    .bio(updatedUser.getBio())
                    .image(updatedUser.getImage())
                    .email(updatedUser.getEmailId())
                    .username(updatedUser.getUsername())
                    .build();


        }

        catch(UserNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();

        }

    }

    public UserTable createUser(CreateUserRequest createUserRequest){


        UserTable user = new UserTable(
                createUserRequest.getUsername(),
                createUserRequest.getBio(),
                createUserRequest.getEmailId(),
                createUserRequest.getImageLink(),
                createUserRequest.getPassword()
        );
        jooqUserRepository.createUser(user);
        return user;
    }


}
