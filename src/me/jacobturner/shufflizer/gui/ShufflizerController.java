package me.jacobturner.shufflizer.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import me.jacobturner.shufflizer.FileOps;
import me.jacobturner.shufflizer.Logger;
import me.jacobturner.shufflizer.Options;

public class ShufflizerController {
	@FXML
	public Button playStopButton;
	@FXML
	private Button optionsButton;
	@FXML
	private Button exitButton;
	@FXML
	public Label nowPlayingLabel;

	Options options = new Options();
	public ArrayList<File> prevMusicPlayed = new ArrayList<File>();
	public int secondsCount;
	public Random random = new Random();
	public Thread musicThread;
	public Mp3File musicMp3file;
	public File musicFile;
	public MediaPlayer musicPlayer;

	public void initialize() {
		optionsButton.setOnAction(event -> {
			Dialog<ButtonType> dialog = new Dialog<>();
			dialog.setTitle("Shufflizer Options");
			dialog.setHeaderText("Shufflizer Options");
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			TextField songPath = new TextField(options.getValue("song_path"));
			TextField idPath = new TextField(options.getValue("id_path"));
			TextField nowPlayingPath = new TextField(options.getValue("now_playing_path"));
			TextField timeBetweenIDs = new TextField(options.getValue("time_between_ids"));
			TextField logDateFormat = new TextField(options.getValue("log_date_format"));
			TextField stationName = new TextField(options.getValue("station_name"));
			grid.add(new Label("Song Path"), 0, 0);
			grid.add(songPath, 1, 0);
			grid.add(new Label("ID Path"), 0, 1);
			grid.add(idPath, 1, 1);
			grid.add(new Label("NowPlaying.txt Path"), 0, 2);
			grid.add(nowPlayingPath, 1, 2);
			grid.add(new Label("Time Between IDs (sec)"), 0, 3);
			grid.add(timeBetweenIDs, 1, 3);
			grid.add(new Label("Log Date Format"), 0, 4);
			grid.add(logDateFormat, 1, 4);
			grid.add(new Label("Station Name"), 0, 5);
			grid.add(stationName, 1, 5);
			dialog.getDialogPane().setContent(grid);
			dialog.showAndWait().ifPresent(result -> {
				if (result == saveButtonType) {
					options.setValue("song_path", songPath.getText());
					options.setValue("id_path", idPath.getText());
					options.setValue("now_playing_path", nowPlayingPath.getText());
					options.setValue("time_between_ids", timeBetweenIDs.getText());
					options.setValue("log_date_format", logDateFormat.getText());
					options.setValue("station_name", stationName.getText());
				}
			});
		});
		exitButton.setOnAction(event -> {
			Platform.exit();
			System.exit(0);
		});
		nowPlayingLabel.setText("Not playing...");
		nowPlayingLabel.setTextFill(Color.RED);
	}

	public void outputMessage(AlertType alertType, String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert alert = new Alert(alertType, message);
				alert.showAndWait();
			}
		});
	}
	
	public void updateNowPlayingText(String newLabel) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				nowPlayingLabel.setText(newLabel);
				try {
					Logger.logSong(newLabel);
					Logger.changeNowPlayingTxt(newLabel);
				} catch (Exception e) {
					e.printStackTrace();
					outputMessage(AlertType.ERROR, e.getMessage());
				}
			}
		});
	}
	
	public void updateNowPlayingColor(Color newColor) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				nowPlayingLabel.setTextFill(newColor);
			}
		});
	}
	
	public void playMusic() {
		musicThread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		    	while(true){
					if (secondsCount >= Integer.parseInt(options.getValue("time_between_ids"))) {
						File[] stationIDFileList = FileOps.getStationIDFileList();
						int idIndex = random.nextInt(stationIDFileList.length);
						File idFile = stationIDFileList[idIndex];
						Media media = new Media(idFile.toURI().toString());
						musicPlayer = new MediaPlayer(media);
						try {
							musicMp3file = new Mp3File(idFile.getAbsolutePath());
							if (musicMp3file.hasId3v1Tag()) {
								ID3v1 id3v1Tag = musicMp3file.getId3v1Tag();
								updateNowPlayingText(id3v1Tag.getTitle() + " - " + id3v1Tag.getArtist());
							} else if (musicMp3file.hasId3v2Tag()) {
								ID3v2 id3v2Tag = musicMp3file.getId3v2Tag();
								updateNowPlayingText(id3v2Tag.getTitle() + " - " + id3v2Tag.getArtist());
							} else {
								updateNowPlayingText(options.getValue("station_name") + " - Station Identification");
							}
							updateNowPlayingColor(Color.BLUE);
							secondsCount = 0;
							musicPlayer.play();
						} catch (Exception e) {
							outputMessage(AlertType.ERROR, e.getMessage());
						}
					} else {
						try {
							String genreToPlay = FileOps.genreCheck();
							ArrayList<File> musicFileList = FileOps.getMusicFileList(genreToPlay);
							noRepeatsLoop:
							while(true) {
								int musicFileIndex = random.nextInt(musicFileList.size());
								musicFile = musicFileList.get(musicFileIndex);
								if (!prevMusicPlayed.contains(musicFile)) {
									if (prevMusicPlayed.size() > 5) {
										prevMusicPlayed.remove(0);
									}
									prevMusicPlayed.add(musicFile);
									break noRepeatsLoop;
								}
							}
							Media media = new Media(musicFile.toURI().toString());
							musicPlayer = new MediaPlayer(media);
							musicMp3file = new Mp3File(musicFile.getAbsolutePath());
							if (musicMp3file.hasId3v1Tag()) {
								ID3v1 id3v1Tag = musicMp3file.getId3v1Tag();
								updateNowPlayingText(id3v1Tag.getTitle() + " - " + id3v1Tag.getArtist());
							} else if (musicMp3file.hasId3v2Tag()) {
								ID3v2 id3v2Tag = musicMp3file.getId3v2Tag();
								updateNowPlayingText(id3v2Tag.getTitle() + " - " + id3v2Tag.getArtist());
							} else {
								updateNowPlayingText(options.getValue("station_name") + " - " + options.getValue("station_name"));
							}
							updateNowPlayingColor(Color.BLACK);
							secondsCount += musicMp3file.getLengthInSeconds();
							musicPlayer.play();
						} catch (Exception e) {
							e.printStackTrace();
							outputMessage(AlertType.ERROR, e.getMessage());
						}
					}
					try {
						Thread.sleep(musicMp3file.getLengthInMilliseconds());
					} catch (InterruptedException e) {
						e.printStackTrace();
						outputMessage(AlertType.ERROR, e.getMessage());
					}
				}
		    }         
		});
		musicThread.start();
	}

	@FXML
	public void pressPlay() {
		playStopButton.setText("Stop");
		playStopButton.setOnAction(event -> pressStop());
		playMusic();
	}
	
	@FXML
	public void pressStop() {
		playStopButton.setText("Play");
		playStopButton.setOnAction(event -> pressPlay());
		musicThread.interrupt();
		nowPlayingLabel.setText("Not playing...");
		nowPlayingLabel.setTextFill(Color.RED);
	}
}