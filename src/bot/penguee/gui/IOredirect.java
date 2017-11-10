package bot.penguee.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextArea;

public class IOredirect implements Runnable {
	JTextArea displayPane;
	BufferedReader reader;

	private IOredirect(JTextArea displayPane, InputStream os) {
		this.displayPane = displayPane;
		reader = new BufferedReader(new InputStreamReader(os));
	}

	public void run() {
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// displayPane.replaceSelection( line + "\n" );
				displayPane.append(line + "\n");
				displayPane.setCaretPosition(displayPane.getDocument()
						.getLength());
			}
		} catch (IOException ioe) {

		}
	}

	public static void redirectOutput(JTextArea displayPane, InputStream is,
			InputStream isErr) {
		IOredirect.redirectOut(displayPane, is);
		IOredirect.redirectErr(displayPane, isErr);
	}

	public static void redirectOut(JTextArea displayPane, InputStream is) {
		// PipedOutputStream pos = new PipedOutputStream();
		// System.setOut(new PrintStream(pos, true));

		IOredirect iOredirect = new IOredirect(displayPane, is);
		Thread t = new Thread(iOredirect);
		t.setDaemon(true);
		t.start();
	}

	public static void redirectErr(JTextArea displayPane, InputStream is) {
		// PipedOutputStream pos = new PipedOutputStream();
		// System.setErr(new PrintStream(pos, true));

		IOredirect iOredirect = new IOredirect(displayPane, is);
		Thread t = new Thread(iOredirect);
		t.setDaemon(true);
		t.start();
	}

}