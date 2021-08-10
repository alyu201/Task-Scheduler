package Model;

/**
 * This class handles the Invalid input argument exception.
 * @author Kelvin Shen
 *
 */

public class InvalidInputArgumentException extends Exception{
    InvalidInputArgumentException(String errorMessage){
        super(errorMessage);
    }

}
