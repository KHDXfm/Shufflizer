package me.jacobturner.shufflizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Options {
	InputStream input;
	OutputStream output;
	Properties prop = new Properties();
	
	public Options() {
		try {
			input = new FileInputStream("config.properties");
			prop.load(input);
		} catch (FileNotFoundException fnf) {
			try {
				output = new FileOutputStream("config.properties");
				output.close();
				input = new FileInputStream("config.properties");
				prop.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	public String getValue(String key) {
		return prop.getProperty(key, "");
	}
	
	public void setValue(String key, String value) {
		try {
			output = new FileOutputStream("config.properties");
			prop.setProperty(key, value);
			prop.store(output, null);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
