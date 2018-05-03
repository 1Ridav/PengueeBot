package bot.penguee.exception;

public class FragmentNotLoadedException extends Exception {
	
	// Parameterless Constructor
	public FragmentNotLoadedException() {
	}

	// Constructor that accepts a message
	public FragmentNotLoadedException(String message) {
		super(message);
	}
}