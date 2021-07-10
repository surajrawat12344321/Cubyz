package cubyz.utils.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Log {
	

	static private DateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	static private DateFormat logFileFormat = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
	
	static private FileOutputStream latestLogOutput,currentLogOutput;
	static {
		try {
			File logsFolder = new File("logs");
			if(!logsFolder.exists()) {
				logsFolder.mkdirs();
			}
			
			latestLogOutput = new FileOutputStream("logs/latest.log");
			currentLogOutput = new FileOutputStream("logs/" + logFileFormat.format(Calendar.getInstance().getTime()) + ".log");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//methods
	static public void info(Object object) {
		log("info",object);
	}
	static public void warning(Object object) {
		log("warning",object,true);
	}
	static public void severe(Object object) {
		log("severe",object,true);
	}
	
	static public void log(String prefix,Object object) {
		log(prefix,object,false);
	}
	
	static public void log(String prefix,Object object,boolean red) {
		Date date = new Date(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder();
		
		sb.append("[" + format.format(date) + " | " + prefix + " | " + Thread.currentThread().getName() + "] ");
		sb.append(toString(object) + "\n");
	
		if(red)
			System.err.print(sb.toString());
		else
			System.out.print(sb.toString());
		
		if (latestLogOutput != null) {
			try {
				latestLogOutput.write(sb.toString().getBytes("UTF-8"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}
		if (currentLogOutput != null) {
			try {
				currentLogOutput.write(sb.toString().getBytes("UTF-8"));
			} catch (Exception e) {
				throw new Error(e);
			}
		}
	}
	
	
	static private String toString(Object object) {
		if(object instanceof Throwable) {

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			((Throwable)object).printStackTrace(pw);
			return sw.toString();
		}
		return object.toString();
	}
	
}
