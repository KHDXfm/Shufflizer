package me.jacobturner.shufflizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class FileOps {
	private static Options options = new Options();
	
	public static ArrayList<File> getMusicFileList(String genre) {
		ArrayList<File> fileList = new ArrayList<File>();
		if (genre != null) {
			try {
				fileList.addAll(Arrays.asList(new File(options.getValue("song_path") + "/" + genre).listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File pathname) {return pathname.isFile();}
				})));
			} catch (Exception e) {
				try {
					ArrayList<String> genres = getGenres();
					for (String g: genres) {
						fileList.addAll(Arrays.asList(new File(options.getValue("song_path") + "/" + g).listFiles(new FileFilter() {
						    @Override
						    public boolean accept(File pathname) {return pathname.isFile();}
						})));
					}
				} catch (Exception e2) {
					fileList.addAll(Arrays.asList(new File(options.getValue("song_path")).listFiles(new FileFilter() {
					    @Override
					    public boolean accept(File pathname) {return pathname.isFile();}
					})));
				}
			}
		} else {
			try {
				ArrayList<String> genres = getGenres();
				for (String g: genres) {
					fileList.addAll(Arrays.asList(new File(options.getValue("song_path") + "/" + g).listFiles(new FileFilter() {
					    @Override
					    public boolean accept(File pathname) {return pathname.isFile();}
					})));
				}
			} catch (Exception e) {
				fileList.addAll(Arrays.asList(new File(options.getValue("song_path")).listFiles(new FileFilter() {
				    @Override
				    public boolean accept(File pathname) {return pathname.isFile();}
				})));
			}
		}
		return fileList;
	}
	
	public static File[] getStationIDFileList() {
		return new File(options.getValue("id_path")).listFiles();
	}
	
	public static String genreCheck() throws Exception {
		String genre = "";
		String line = "";
		String cvsSplitBy = ",";
		String currentDayOfWeek = DateTimeFormatter.ofPattern("EEEE").format(LocalDateTime.now());
		String currentTimeString = DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now());
		File scheduleFile = new File("schedule.csv");
		if (scheduleFile.exists()) {
			FileReader fr = new FileReader(scheduleFile);
			BufferedReader br = new BufferedReader(fr);
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
			fr.close();
			br.close();
		}
		return genre; 
	}
	
	public static ArrayList<String> getGenres() throws Exception {
		ArrayList<String> genres = new ArrayList<String>();
		String line = "";
		String cvsSplitBy = ",";
		File scheduleFile = new File("schedule.csv");
		if (scheduleFile.exists()) {
			FileReader fr = new FileReader(scheduleFile);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				String[] genreLine = line.split(cvsSplitBy);
				genres.add(genreLine[3]);
			}
			fr.close();
			br.close();
		}
		return genres;
	}
}
