package kamathadarsh.Conduit.Enum;

public enum ProfilePictureAction {

    APPEND("append"),
    DELETE("delete");

    private final String action;

    ProfilePictureAction(String action){

        this.action = action;
    }

    public String actionString(){

        return action;
    }

}
