package kamathadarsh.Conduit.jooqRepository;

import jakarta.transaction.Transactional;
import kamathadarsh.Conduit.Request.UserUpdateRequest;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.UserTableRecord;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.UpdateSetFirstStep;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.*;

@AllArgsConstructor
@Repository
public class JOOQUserRepository {

    private final DSLContext dslContext;


    public Optional<UserTable> findByUsername(String username){

        return Optional.ofNullable(dslContext.select(
                USER_TABLE.USERNAME,
                USER_TABLE.BIO,
                USER_TABLE.IMAGE,
                USER_TABLE.EMAIL_ID
                )
                .from(USER_TABLE)
                .where(USER_TABLE.USERNAME.eq(username)).fetchOneInto(UserTable.class));
    }

    public List<String> listOfFollowingUsernames(String followerUsername){

        return dslContext.select(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWING_USERNAME)
                .from(FOLLOWER_FOLLOWING_USER_TABLE)
                .where(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWER_USERNAME.eq(followerUsername))
                .fetchInto(String.class);
    }

    public List<String> listOfFollowersUsername(String usernameToBeFollowed){

        return dslContext.select(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWER_USERNAME)
                .from(FOLLOWER_FOLLOWING_USER_TABLE)
                .where(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWING_USERNAME.eq(usernameToBeFollowed))
                .fetchInto(String.class);
    }

    public void followUser(String followerUsername, String usernameToBeFollowed){

        dslContext.insertInto(FOLLOWER_FOLLOWING_USER_TABLE)
                .set(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWER_USERNAME, followerUsername)
                .set(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWING_USERNAME,usernameToBeFollowed)
                .execute();
    }

    public void unfollowUser(String followerUsername, String usernameToBeUnfollowed){

        dslContext.deleteFrom(FOLLOWER_FOLLOWING_USER_TABLE)
                .where(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWER_USERNAME.eq(followerUsername))
                .and(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWING_USERNAME.eq(usernameToBeUnfollowed))
                .execute();
    }

    public boolean checkIfUserFollowsAnotherUser(String followerUsername, String usernameToBeFollowed){

        return dslContext.fetchExists(dslContext.select()
                .from(FOLLOWER_FOLLOWING_USER_TABLE)
                .where(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWER_USERNAME.eq(followerUsername))
                .and(FOLLOWER_FOLLOWING_USER_TABLE.FOLLOWING_USERNAME.eq(usernameToBeFollowed)));


    }

    public void createUser(UserTable newUser){

        dslContext.newRecord(USER_TABLE, newUser);

        dslContext.insertInto(USER_TABLE)
                .set(USER_TABLE.EMAIL_ID, newUser.getEmailId())
                .set(USER_TABLE.USERNAME, newUser.getUsername())
                .set(USER_TABLE.BIO, newUser.getBio())
                .set(USER_TABLE.PASSWORD, newUser.getBio())
                .set(USER_TABLE.IMAGE, newUser.getImage())
                .execute();
    }

    @Transactional
    public void updateUser(String currUserUsername, UserUpdateRequest updatedUserDetails){

        // updating all the tables with new username, in case username was changed.

        UserTableRecord updatedUser = dslContext.newRecord(USER_TABLE);
        if(updatedUserDetails.getBio() != null && !updatedUserDetails.getBio().isBlank()){
            updatedUser.set(USER_TABLE.BIO, updatedUserDetails.getBio());

        }

        if(updatedUserDetails.getImage() != null && !updatedUserDetails.getImage().isBlank()){
            updatedUser.set(USER_TABLE.IMAGE, updatedUserDetails.getImage());


        }
        if(updatedUserDetails.getPassword() != null && !updatedUserDetails.getPassword().isBlank()){
            updatedUser.set(USER_TABLE.PASSWORD, updatedUserDetails.getPassword());

        }
        if(updatedUserDetails.getEmailId() != null && !updatedUserDetails.getEmailId().isBlank()){
            updatedUser.set(USER_TABLE.EMAIL_ID, updatedUserDetails.getEmailId());

        }

        dslContext.update(USER_TABLE)
                .set(updatedUser)
                .where(USER_TABLE.USERNAME.eq(currUserUsername))
                .execute();



    }



}
