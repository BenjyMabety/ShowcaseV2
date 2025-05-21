package tmg.za.client.FileEditor;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("file")
public interface FileService extends RemoteService {
	String openFile(String fileName) throws IllegalArgumentException;

	String saveFile(String fileName, String content) throws IllegalArgumentException;
}
