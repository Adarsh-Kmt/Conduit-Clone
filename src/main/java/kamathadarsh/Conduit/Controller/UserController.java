package kamathadarsh.Conduit.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kamathadarsh.Conduit.CustomValidationAnnotations.ValidImage;
import kamathadarsh.Conduit.Request.CreateUserRequest;
import kamathadarsh.Conduit.Request.UserUpdateRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@Validated
public class UserController {

    /*
    TODO:
        validate all usernames other than currUserUsername.
     */
    private final UserService userService;

    @PostMapping("/api/profiles/{toBeFollowedUsername}/follow")
    public ResponseEntity<CustomResponse> followUser(@PathVariable("toBeFollowedUsername") @NotNull
                                                     String toBeFollowedUsername){

        CustomResponse response = userService.followUser(toBeFollowedUsername);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @PostMapping("/api/profiles/{toBeUnfollowedUsername}/unfollow")
    public ResponseEntity<CustomResponse> unfollowUser(@PathVariable("toBeUnfollowedUsername") @NotNull
                                                       String toBeUnfollowedUsername){

        CustomResponse response = userService.unfollowUser(toBeUnfollowedUsername);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    @GetMapping("/api/profiles/{username}")
    public ResponseEntity<CustomResponse> getProfile(@PathVariable("username") @NotNull String username){

        CustomResponse response = userService.getProfile(username);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }

    /*
    TODO:
        validate user update request:
        1) profile picture file type
        2) emailId field (@Email)
     */

    @PutMapping("/api/user")
    public ResponseEntity<CustomResponse> updateUser(@RequestPart("newProfilePicture") @ValidImage
                                                     MultipartFile newProfilePicture,
                                                     @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest){

        CustomResponse response = userService.userUpdate(newProfilePicture,userUpdateRequest);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);

    }

    /*
    TODO:
        validate create user request.
        fields to be validated :
        1) emailId (@Email)
        2) strength of password
        3) profile picture file type.

     */
//    @PostMapping("/test/addUser")
//    public ResponseEntity<CustomResponse> addUser(@RequestPart("profilePicture") @ValidImage
//                                                      MultipartFile profilePicture,
//                                             @RequestPart("createUserRequest") @Valid CreateUserRequest createUserRequest){
//
//        CustomResponse response = userService.createUser(createUserRequest, profilePicture);
//
//        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;
//
//        return ResponseEntity.status(statusOfRequest).body(response);
//    }

    /*
    TODO:
        validate file type of profile picture sent.

     */
    @PostMapping("/test/addStockPhoto")
    public ResponseEntity<CustomResponse> addBlankProfilePicture(@RequestPart("blankProfilePicture") @ValidImage
                                                                     MultipartFile blankProfilePicture){

        CustomResponse response = userService.saveProfilePicture(blankProfilePicture, "blankProfilePicture");

        HttpStatus statusOfResponse = (response instanceof FailureResponse)?HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfResponse).body(response);
    }
}
