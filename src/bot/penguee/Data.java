package bot.penguee;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class Data {
	String s = new File("").getAbsolutePath();
	static final String resourcesPath = new File("").getAbsolutePath() + File.separator + "frag";
	static HashMap<String, Frag> fragments = new HashMap<String, Frag>();
	private static String absPath;
	private static String colorRegex = "_[(]{2}[-]?[0-9]+[)]{2}";
	private static String colorRegex2 = ".*" + colorRegex + ".*";
	static Screen screenObject = null;
	public static String scriptFileName = "script.py";

	static JythonVM jython = null;
	public static boolean forceUseGPU = false;
	public static boolean useInternalCache = true;
	private static ArrayList<File> failedToLoadFragmentsList = null;
	public static MyQueue recentScripts = new MyQueue(7);
	public static String xmxValue;

	public Data() {
		// TODO Auto-generated constructor stub
	}

	static void loadFragments() {

		File f = new File(resourcesPath);
		Data.absPath = f.getAbsolutePath();
		loadFragments(f);
	
		if (failedToLoadFragmentsList != null) {
			System.out.println("CORE: Error occured while loading " + failedToLoadFragmentsList.size()
					+ " fragments. Here is the list:");
			for (File k : failedToLoadFragmentsList) {
				System.out.println("     " + k.getAbsolutePath());
			}
		}
	}

	private static void loadFragments(File folder) {
		System.out.println(folder.getAbsolutePath());
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadFragments(fileEntry);
			} else {// is file
				String s = fileEntry.getAbsolutePath().replace(absPath, "").replace(File.separator, ".")
						.replace(".bmp", "").replaceAll(colorRegex, "");
				s = s.substring(1);
				System.out.println(fileEntry.getName() + " " + s);
				try {
					if (fileEntry.getAbsolutePath().matches(colorRegex2))
						fragments.put(s, new FragMono(fileEntry.getAbsolutePath()));

					else
						fragments.put(s, new Frag(fileEntry.getAbsolutePath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (failedToLoadFragmentsList == null)
						failedToLoadFragmentsList = new ArrayList<File>();
					failedToLoadFragmentsList.add(fileEntry);
				}
			}
		}
	}

}
