package week11;

/**
 * Exception for CardPile related errors.
 *
 * @author Hunter Kingsbeer & Jack Heikell
 */
public class CardPileException extends RuntimeException{

    private static final long serialVersionUID = 152959L;
    public CardPileException(String s){
        super(s);
    }

}
