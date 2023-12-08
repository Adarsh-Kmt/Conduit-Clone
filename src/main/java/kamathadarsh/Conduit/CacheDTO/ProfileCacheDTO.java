package kamathadarsh.Conduit.CacheDTO;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.Conduit.Response.ProfileResponse;


import java.util.List;


public class ProfileCacheDTO {

    private String username;

    private String bio;

    private String image;

    private List<String> followerUserUsernameList;

    public ProfileCacheDTO(UserTable user, List<String> followerUsernameList){

        this.username = user.getUsername();
        this.bio = user.getBio();
        this.image = user.getImage();
        this.followerUserUsernameList = followerUsernameList;
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
