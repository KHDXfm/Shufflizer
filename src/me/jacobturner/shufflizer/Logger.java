package me.jacobturner.shufflizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class Logger {
	private static String getDate(String dateFormat) {
		return DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.now());
	}
	
	public static void logSong(String songString) throws Exception {
		Options options = new Options();
		File logFile = new File("logs/songlog." + getDate("MM-dd-yyyy") + ".log");
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileWriter fw = new FileWriter(logFile.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(getDate(options.getValue("log_date_format")) + ": " + songString + "\n");
		bw.close();
		fw.close();
	}
	
	public static void changeNowPlayingTxt(String songString) throws Exception {
		ArrayList<String> songData = new ArrayList<String>(Arrays.asList(songString.split(",")));
		Options options = new Options();
		File txtFile = new File(options.getValue("now_playing_path"));
		if (!txtFile.exists()) {
			txtFile.createNewFile();
		}
		FileWriter fw = new FileWriter(txtFile.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("Title:" + songData.get(1) + "\n");
		bw.write("Artist:" + songData.get(0) + "\n");
		bw.close();
		fw.close();
	}
}
