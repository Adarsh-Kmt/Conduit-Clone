package kamathadarsh.Conduit.Exception;

public class CommentNotFoundException extends Throwable{
    public CommentNotFoundException() {
        super();
    }

    public CommentNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
