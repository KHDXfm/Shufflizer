package me.jacobturner.shufflizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FileOps {
	private static Options options = new Options();
	
	public static File[] getMusicFileList(String genre) {
		File[] fileList =  new File(options.getValue("song_path")).listFiles();
		if (genre != null) {
			//DO STUFF HERE
		}
		return fileList;
	}
	
	public static File[] getStationIDFileList() {
		return new File(options.getValue("id_path")).listFiles();
	}
	
	public static String genreCheck() throws Exception {
		String genre = null;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String currentDayOfWeek = DateTimeFormatter.ofPattern("EEEE").format(LocalDateTime.now());
		String currentTimeString = DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now());
		File scheduleFile = new File("schedule.csv");
		if (scheduleFile.exists()) {
			br = new BufferedReader(new FileReader("schedule.csv"));
			while ((line = br.readLine()) != null) {
				String[] genreLine = line.split(cvsSplitBy);
				if (currentDayOfWeek.equals(genreLine[0])) {
					LocalTime currentTime = LocalTime.parse(currentTimeString);
					LocalTime startTime = LocalTime.parse(genreLine[1]);
					LocalTime endTime = LocalTime.parse(genreLine[2]);
					if (startTime.isBefore(endTime)) {
						if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
							genre = genreLine[3];
						} else {
							if (startTime.isBefore(currentTime) || currentTime.isAfter(endTime)) {
								genre = genreLine[3];
							}
						}
					}
				}
					
			}
			if (br != null) {br.close();}
		}
		return genre; 
	}
}
