package kamathadarsh.Conduit.Exception;

public class ArticleNotFoundException extends Throwable{

    public ArticleNotFoundException() {
        super();
    }

    public ArticleNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
