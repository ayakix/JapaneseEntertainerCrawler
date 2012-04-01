import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	final static String URL = "http://search.sponichi.co.jp/cgi-bin/spomei/?id=20438&kw=%s&cs=utf8&sakuin=1&gr=lid179&pg=%s";
	final static String INITIAL = "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわ";
	static final Pattern p = Pattern.compile("(http://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+", Pattern.CASE_INSENSITIVE);

	public static void read() {
		for(int i = 0; i < INITIAL.length(); i++) {
			int pg = 0;
			while (true) {
				String searchURLStr = String.format(URL, String.valueOf(INITIAL.charAt(i)), String.valueOf(pg++));
				List<String> searchLines = loadLines(searchURLStr, "UTF-8");
				List<List<String>> profileURLs = extractProfileURLs(searchLines);
				for (List<String> urls : profileURLs) {
					Person person = getPersonData(urls);
					downloadImg(person);
					System.out.println(person.toString());
				}
				
				if(!hasNextPage(searchLines)) break;
			}
		}
	}
	
	public static boolean hasNextPage(List<String> lines) {
		for(String line : lines) {
			if(line.contains("<div class=\"page\">") && line.contains("<span class=\"next\"><a href="))
				return true;
		}
		return false;
	}

	public static Person getPersonData(List<String> urls) {
		List<String> lines = loadLines(urls.get(0), "Shift_JIS");
		String name="", yomi="", bday="", place="", blood="", imgURL=urls.get(1);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.contains("<td class=\"content\">")) {
				line = removeHTMLTag(line);
				if (line.equals("名前")) {
					name = removeHTMLTag(lines.get(i+1));
				} else if (line.equals("読み")) {
					yomi = removeHTMLTag(lines.get(i+1));
				} else if (line.equals("生年月日")) {
					bday = removeHTMLTag(lines.get(i+1));
				} else if (line.equals("出身地")) {
					place = removeHTMLTag(lines.get(i+1));
				} else if (line.equals("血液型")) {
					blood = removeHTMLTag(lines.get(i+1));
				}
			}
		}
		return new Person(name, yomi, bday, place, blood, imgURL);
	}

	public static String removeHTMLTag(String str) {
		return str.replaceAll("<.+?>", "").trim();
	}

	public static List<String> extractURL(String line) {
		List<String> ret = new ArrayList<String>();
		Matcher m = p.matcher(line);
		if(m.find()) {
			ret.add(m.group());
			if(m.find())
				ret.add(m.group());
		}
		return (ret.size() == 2) ? ret : null;
	}
	
	public static List<List<String>> extractProfileURLs(List<String> lines) {
		List<List<String>> ret = new ArrayList<List<String>>();
		for (String line : lines) {
			if (line.contains("<p class=\"brief\">")) {
				List<String> urls = extractURL(line);
				if (urls != null) ret.add(urls);
			}
		}
		return ret;
	}

	public static List<String> loadLines(String urlStr, String charset) {
		List<String> ret = new ArrayList<String>();
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-agent","Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			while ((line = in.readLine()) != null)
				ret.add(line);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	public static void downloadImg(Person person) {
		try {
			URL url = new URL(person.getImgURL());
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-agent","Mozilla/5.0");
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File("./images/" + person.getYomi() + ".png")));
			int b;
			while ((b = in.read()) >= 0) {  
				out.write(b);
			}
			in.close();
			out.flush();
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		read();
	}
}
