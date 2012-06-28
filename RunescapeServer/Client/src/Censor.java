import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author: Apache Ah64
 */
public class Censor {

	private static String censor [] = { "fuck", "nigger" };
	private static boolean on = false;
	private static String path = System.getProperty("user.home")+"/matrixCache4/censor.txt";
	
	public Censor() {
		if(!check()) {
			on = true;
		}
	}

	public static void createCensor() {
    	try {
    		File censor = new File(path);
        	BufferedWriter writer = new BufferedWriter(new FileWriter(censor));
			writer.append("");
	    	writer.close();
		} catch (IOException e) {
			//TODO
		}
	}
	
	public static void RemoveCensor() {
    	File censor = new File(path);
		censor.delete();
	}
	
	public static boolean check() {
		File censor = new File(path);
		if(censor.exists()) {
			return false;
		}
		return true;
	}

	public static String Replace(String text) {
		if(on) {
			for (String s : censor) {
				if (text.toLowerCase().contains(s)) {
					//System.out.print(s);
					int length = s.length();
					String replace = "";
					for(int i = 0; i < length; i++) {
						replace += "*";
					}
					text = formatText(text.toLowerCase().replaceAll(s, replace));
				}
			}
		}
		return text;
	}
	
	public static String formatText(String str){
		char[] arr = str.toCharArray();
		boolean cap = true;
		for (int i = 0; i < arr.length; i++) {
			if (cap == true && !Character.isWhitespace(arr[i])) {
					arr[i] =  Character.toUpperCase(arr[i]);
					cap = false;
				} else {
				if (arr[i] == '.' || arr[i] == '?' || arr[i] == '!') {
					cap = true;
				}
			}
		}
		String s = new String(arr);
		return s;
	} 
}