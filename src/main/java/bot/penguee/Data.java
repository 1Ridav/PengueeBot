package bot.penguee;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bot.penguee.gui.ScriptQuickrunQueue;

public class Data {
	String s = new File("").getAbsolutePath();
	static final String resourcesPath = new File("").getAbsolutePath() + File.separator + "frag";
	private static HashMap<String, Frag> fragments = new HashMap<String, Frag>();
	private static String absPath;
	private static String colorRegex = "_[(]{2}[-]?[0-9]+[)]{2}";
	private static String colorRegex2 = ".*" + colorRegex + ".*";
	static Screen screenObject = null;
	private static String scriptFileName = "script.py";

	static JythonVM jython = null;
	private static boolean forceUseGPU = false;
	private static boolean useInternalCache = true;
	private static ArrayList<File> failedToLoadFragmentsList = null;
	private static ScriptQuickrunQueue recentScriptsList = new ScriptQuickrunQueue(7);
	private static String xmxValue;

	public Data() {
		// TODO Auto-generated constructor stub
	}
	
	public static Object[] getFragmentKeys() {
		return fragments.keySet().toArray();
	}
	
	public static String getScriptFileName() {
		return scriptFileName;
	}


	public static void setScriptFileName(String scriptFileName) {
		Data.scriptFileName = scriptFileName;
	}


	public static boolean getForceUseGPU() {
		return forceUseGPU;
	}


	public static void setForceUseGPU(boolean forceUseGPU) {
		Data.forceUseGPU = forceUseGPU;
	}


	public static boolean getUseInternalCache() {
		return useInternalCache;
	}


	public static void setUseInternalCache(boolean useInternalCache) {
		Data.useInternalCache = useInternalCache;
	}


	public static String getXmxValue() {
		return xmxValue;
	}


	public static void setXmxValue(String xmxValue) {
		Data.xmxValue = xmxValue;
	}
	
	public static ScriptQuickrunQueue getRecentScriptsList() {
		return recentScriptsList;
	}


	public static HashMap<String, Frag> fragments() {
		return fragments;
	}

	static void loadFragments(boolean log) {

		File f = new File(resourcesPath);
		Data.absPath = f.getAbsolutePath();
		loadFragments(f, true);
	
		if (failedToLoadFragmentsList != null) {
			System.out.println("CORE: Error occured while loading " + failedToLoadFragmentsList.size()
					+ " fragments. Here is the list:");
			for (File k : failedToLoadFragmentsList) {
				System.out.println("     " + k.getAbsolutePath());
			}
		}
	}
	static void loadFragments() {
		loadFragments(true);
	}

	private static void loadFragments(File folder, boolean log) {
		System.out.println(folder.getAbsolutePath());
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadFragments(fileEntry, log);
			} else {// is file
				String s = fileEntry.getAbsolutePath().replace(absPath, "").replace(File.separator, ".")
						.replace(".bmp", "").replaceAll(colorRegex, "");
				s = s.substring(1);
				if(log)
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
