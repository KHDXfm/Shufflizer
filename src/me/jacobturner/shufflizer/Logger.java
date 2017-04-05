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
		String fileName = "logs/songlog." + getDate("MM-dd-yyyy") + ".log";
		BufferedWriter bw = null;
		FileWriter fw = null;
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		bw.write(getDate(options.getValue("log_date_format")) + ": " + songString + "\n");
		if (bw != null) {bw.close();}
		if (fw != null) {fw.close();}
	}
	
	public static void changeNowPlayingTxt(String songString) throws Exception {
		ArrayList<String> songData = new ArrayList<String>(Arrays.asList(songString.split(",")));
		Options options = new Options();
		String fileName = options.getValue("now_playing_path");
		BufferedWriter bw = null;
		FileWriter fw = null;
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		fw = new FileWriter(file.getAbsoluteFile(), true);
		bw = new BufferedWriter(fw);
		bw.write("Title:" + songData.get(1) + "\n");
		bw.write("Artist:" + songData.get(0) + "\n");
		bw.write(getDate(options.getValue("log_date_format")) + ": " + songString + "\n");
		if (bw != null) {bw.close();}
		if (fw != null) {fw.close();}
	}
}
