package tmg.za.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import tmg.za.client.FileEditor.FileService;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class FileServiceImpl extends RemoteServiceServlet implements FileService {

	@Override
	public String openFile(String fileName) throws IllegalArgumentException {
		File file = new File(fileName);
		String data = "";

		try {
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				data += parse(myReader.nextLine() + "\n");
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "File Not Found:" + fileName;
		}
		return data;
	}

	private String parse(String line) {
		if (line.equals("")) {
			return "\n";
		}
		return line;
	}

	@Override
	public String saveFile(String fileName, String content) throws IllegalArgumentException {
		try {
			FileWriter myWriter = new FileWriter(fileName + ".html");
			myWriter.write(content);
			myWriter.close();
			return "Successfully wrote to the file.";
		} catch (IOException e) {

			e.printStackTrace();
			return "An error occurred.";
		}
	}
}
