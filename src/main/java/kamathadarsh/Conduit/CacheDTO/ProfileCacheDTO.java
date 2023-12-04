package kamathadarsh.Conduit.CacheDTO;

import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Response.ProfileResponse;

import java.util.HashSet;
import java.util.Set;

public class ProfileCacheDTO {

    private String username;

    private String bio;

    private String image;

    private Set<String> followerUserUsernameList;

    public ProfileCacheDTO(User user){

        this.username = user.getUsername();
        this.bio = user.getBio();
        this.image = user.getImage();
        this.followerUserUsernameList = user.getFollowerUserUsernameList();
    }

    public ProfileResponse convertToProfileResponse(String currUserUsername){

        ProfileResponse profileResponse = new ProfileResponse();

        profileResponse.setBio(bio);
        profileResponse.setImage(image);
        profileResponse.setUsername(username);
        profileResponse.setFollowing(followerUserUsernameList.contains(currUserUsername));

        return profileResponse;

    }
}
