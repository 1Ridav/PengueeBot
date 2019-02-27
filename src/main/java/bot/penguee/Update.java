/***
 * *
 * 
 * TESTING CLASS, WILL CHANGE SOON
 * 
 * 
 */

package bot.penguee;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Update {
	public final static String version = "1.10.36";
	private static String versionURL = "https://raw.githubusercontent.com/1Ridav/PengueeBot/master/Install/version";
	private static String downloadURL = "https://github.com/1Ridav/PengueeBot/releases/download/v";
	private static String newsURL = "https://raw.githubusercontent.com/1Ridav/PengueeBot/master/Install/news";

	public Update() {
		// TODO Auto-generated constructor stub
	}

	private static String getLatestVersionID(String link) throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();

		return result.toString();
	}

	public static void update() {
		try {
			Desktop.getDesktop().browse(
					new URL(downloadURL + getLatestVersionID(versionURL) + "/penguee-latest-release.zip").toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean available() {
		try {
			String result = getLatestVersionID(versionURL).replace('.', '0');
			String myVersion = version.replace('.', '0');
			if (Long.parseLong(myVersion) < Long.parseLong(result))
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to check for updates");
		}
		return false;
	}

	private static String getNews(String link) throws IOException {
		StringBuilder result = new StringBuilder();
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);
		rd.close();

		return result.toString();
	}

	public static String getNews() {
		try {
			return getNews(newsURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error occured while loading news";
	}


}
