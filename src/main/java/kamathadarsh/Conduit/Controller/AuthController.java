package kamathadarsh.Conduit.Controller;


import jakarta.validation.Valid;
import kamathadarsh.Conduit.CustomValidationAnnotations.ValidImage;
import kamathadarsh.Conduit.Request.CreateUserRequest;
import kamathadarsh.Conduit.Request.LoginUserRequest;
import kamathadarsh.Conduit.Response.CustomResponse;
import kamathadarsh.Conduit.Response.FailureResponse;
import kamathadarsh.Conduit.Response.SuccessfulLoginResponse;
import kamathadarsh.Conduit.Service.UserService;
import kamathadarsh.Conduit.security.utils.JWTUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;

    @PostMapping("/auth/register")
    public ResponseEntity<CustomResponse> register(@RequestPart("profilePicture") @ValidImage
                                                  MultipartFile profilePicture,
                                                  @RequestPart("createUserRequest") @Valid CreateUserRequest createUserRequest){

        CustomResponse response = userService.createUser(createUserRequest, profilePicture);

        HttpStatus statusOfRequest = (response instanceof FailureResponse)? HttpStatus.NOT_FOUND:HttpStatus.OK;

        return ResponseEntity.status(statusOfRequest).body(response);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<CustomResponse> login(@RequestBody LoginUserRequest loginUserRequest) throws Exception {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getUsername(), loginUserRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String JWTToken = jwtUtils.generateJWTToken(authentication);

        return ResponseEntity.status(HttpStatus.OK).body(new SuccessfulLoginResponse(JWTToken, HttpStatus.OK));

    }
}
