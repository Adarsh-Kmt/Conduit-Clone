package kamathadarsh.Conduit.Exception;

public class UserAlreadyExistsException extends Throwable{
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
