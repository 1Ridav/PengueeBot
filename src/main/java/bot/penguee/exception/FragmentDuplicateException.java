package bot.penguee.exception;

public class FragmentDuplicateException extends Exception {

    // Parameterless Constructor
    public FragmentDuplicateException() {
    }

    // Constructor that accepts a message
    public FragmentDuplicateException(String message) {
        super(message);
    }
}