package tmg.za.client.FileEditor;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>FileService</code>.
 */
public interface FileServiceAsync {
	void openFile(String fileName, AsyncCallback<String> callback) throws IllegalArgumentException;

	void saveFile(String fileName, String content, AsyncCallback<String> callback) throws IllegalArgumentException;

}
