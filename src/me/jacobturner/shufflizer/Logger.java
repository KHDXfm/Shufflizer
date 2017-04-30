package me.jacobturner.shufflizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Logger {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static String getDate(String dateFormat) {
		return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now());
	}
	
	public static void logSong(String songString) throws Exception {
		Options options = new Options();
		File logFilePath = new File("logs/");
		if (!logFilePath.isDirectory()) {
			logFilePath.mkdir();
		}
		File logFile = new File("logs/songlog." + getDate("MM-dd-yyyy") + ".log");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(getDate(options.getValue("log_date_format")) + ": " + songString + LINE_SEPARATOR);
		bw.close();
		fw.close();
	}
	
	public static void changeNowPlayingTxt(String songString) throws Exception {
		ArrayList<String> songData = new ArrayList<String>(Arrays.asList(songString.split(" - ")));
		Options options = new Options();
		File txtFile = new File(options.getValue("now_playing_path"));
		if (!txtFile.exists()) {
			txtFile.createNewFile();
		}
		PrintWriter pw = new PrintWriter(txtFile.getAbsoluteFile());
		pw.write("Title: " + songData.get(0) + LINE_SEPARATOR);
		pw.write("Artist: " + songData.get(1) + LINE_SEPARATOR);
		pw.close();
	}
}
