package Model;

public class InvalidInputArgumentException extends Exception{
    InvalidInputArgumentException(String errorMessage){
        super(errorMessage);
    }

}
