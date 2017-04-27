package me.jacobturner.shufflizer.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import me.jacobturner.shufflizer.Options;

public class ShufflizerOptionsController {
	@FXML
	private TextField songPath;
	@FXML
	private TextField idPath;
	@FXML
	private TextField nowPlayingPath;
	@FXML
	private TextField timeBetweenIDs;
	@FXML
	private TextField logDateFormat;
	@FXML
	private TextField stationName;
	@FXML
	private Button saveButton;
	
	public void initialize() {
		Options options = new Options();
		songPath.setText(options.getValue("song_path"));
		idPath.setText(options.getValue("id_path"));
		nowPlayingPath.setText(options.getValue("now_playing_path"));
		timeBetweenIDs.setText(options.getValue("time_between_ids"));
		logDateFormat.setText(options.getValue("log_date_format"));
		stationName.setText(options.getValue("station_name"));
		saveButton.setOnAction(event -> {
			options.setValue("song_path", songPath.getText());
			options.setValue("id_path", idPath.getText());
			options.setValue("now_playing_path", nowPlayingPath.getText());
			options.setValue("time_between_ids", timeBetweenIDs.getText());
			options.setValue("log_date_format", logDateFormat.getText());
			options.setValue("station_name", stationName.getText());
			options.close();
			try {
				Stage stage = (Stage)saveButton.getScene().getWindow();
				stage.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
