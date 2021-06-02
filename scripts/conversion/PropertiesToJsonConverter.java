import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Properties;

public class PropertiesToJsonConverter {
	public static boolean isString(String str) {
		try {
			int i = Integer.decode(str);
			return false;
		} catch(Exception e) {}
		try {
			double d = Double.parseDouble(str);
			return false;
		} catch(Exception e) {}
		return !str.equals("false") && !str.equals("true") && !str.equals("null");
	}
	public static void main(String[] args) {
		File f = new File(".");
		File[] files = f.listFiles();
		for(File file : files) {
			try {
				Properties properties = new Properties();
				properties.load(new FileReader(file));
				String output = "{\n";
				boolean hadProperties = false;
				for(String key : properties.stringPropertyNames()) {
					hadProperties = true;
					String value = properties.getProperty(key);
					boolean isString = isString(value);
					output += "\t\""+key+"\" : ";
					if(isString) {
						if(value.equals("yes")) {
							output += "true";
						}
						else if(value.equals("no")) {
							output += "false";
						}
						else {
							output += "\""+value+"\"";
						}
					} else {
						output += value;
					}
					output += ",\n";
				}
				if(hadProperties) { // Remove last ',''
					output = output.substring(0, output.length()-2);
				}
				output += "\n}";
				try (PrintWriter out = new PrintWriter(file.getAbsolutePath()+".json")) {
					out.print(output);
				}
			} catch(Exception e) {}
		}
	}
}
