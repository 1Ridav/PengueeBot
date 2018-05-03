package bot.penguee.exception;

public class ScreenNotGrabbedException extends Exception {
	
	// Parameterless Constructor
	public ScreenNotGrabbedException() {
	}

	// Constructor that accepts a message
	public ScreenNotGrabbedException(String message) {
		super(message);
	}
}