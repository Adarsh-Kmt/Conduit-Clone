package kamathadarsh.Conduit.Controller;

import kamathadarsh.Conduit.Request.CreateUserRequest;
import kamathadarsh.Conduit.Request.UserUpdateRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/profiles/{followerUsername}/{toBeFollowedUsername}/follow")
    public ResponseEntity<CustomResponse> followUser(@PathVariable("followerUsername") String followerUsername,
                                                     @PathVariable("toBeFollowedUsername") String toBeFollowedUsername){

        CustomResponse response = userService.followUser(followerUsername, toBeFollowedUsername);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PostMapping("/api/profiles/{followerUsername}/{toBeFollowedUsername}/unfollow")
    public ResponseEntity<CustomResponse> unfollowUser(@PathVariable("followerUsername") String followerUsername,
                                                       @PathVariable("toBeFollowedUsername") String toBeFollowedUsername){

        CustomResponse response = userService.unfollowUser(followerUsername, toBeFollowedUsername);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @GetMapping("/api/profiles/{currUserUsername}/{username}")
    public ResponseEntity<CustomResponse> getProfile(@PathVariable("currUserUsername") String currUserUsername,
                                                     @PathVariable("username") String username){

        CustomResponse response = userService.getProfile(username, currUserUsername);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }


    @PutMapping("/api/{username}/user")
    public ResponseEntity<CustomResponse> updateUser(@PathVariable("username") String username,
                                                     @RequestBody UserUpdateRequest userUpdateRequest){

        CustomResponse response = userService.userUpdate(username, userUpdateRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);

    }

    @PostMapping("/test/addUser")
    public ResponseEntity<CustomResponse> addUser(@RequestPart("profilePicture") MultipartFile profilePicture,
                                             @RequestPart("createUserRequest") CreateUserRequest createUserRequest){

        CustomResponse response = userService.createUser(createUserRequest, profilePicture);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PostMapping("/test/addStockPhoto")
    public ResponseEntity<CustomResponse> addBlankProfilePicture(@RequestPart("blankProfilePicture") MultipartFile blankProfilePicture){

        CustomResponse response = userService.saveProfilePicture(blankProfilePicture, "blankProfilePicture");

        HttpStatus statusOfResponse = (response instanceof FailureResponse)?HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfResponse).body(response);
    }
}
