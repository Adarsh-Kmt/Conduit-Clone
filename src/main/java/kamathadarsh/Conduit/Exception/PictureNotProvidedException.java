package kamathadarsh.Conduit.Exception;

public class PictureNotProvidedException extends Throwable{
    public PictureNotProvidedException() {
        super();
    }

    public PictureNotProvidedException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
