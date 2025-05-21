package tmg.za.client.FileEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileUploader extends DialogBox {

	private static FileUploaderUiBinder uiBinder = GWT.create(FileUploaderUiBinder.class);

	private final FileServiceAsync fileService = GWT.create(FileService.class);

	interface FileUploaderUiBinder extends UiBinder<Widget, FileUploader> {
	}

	Button closeButton = new Button("Close");
	Button openButton = new Button("Open");
	VerticalPanel vp = new VerticalPanel();
	HorizontalPanel hp = new HorizontalPanel();

	private FileUpload upload = new FileUpload();

	private String data;

	/**
	 * 
	 */
	public FileUploader() {
		setWidget(uiBinder.createAndBindUi(this));
		addButtons();
		this.setWidget(vp);

		getOpenButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					if (!getUpload().getFilename().isBlank()) {
						openFile();
					} else {
						Window.alert("Please select a text file");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Add form Buttons
	 */
	private void addButtons() {
		vp.add(getUpload());
		hp.add(getOpenButton());
		hp.add(closeButton);
		vp.add(hp);
	}

	/**
	 * @throws IllegalArgumentException
	 */
	public void openFile() throws IllegalArgumentException {
		fileService.openFile(getUpload().getFilename(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				data = result;
				getOpenButton().setEnabled(false);
			}
		});
	}

	/**
	 * @return data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @return
	 */
	public Button getOpenButton() {
		return openButton;
	}

	/**
	 * @param openButton
	 */
	public void setOpenButton(Button openButton) {
		this.openButton = openButton;
	}

	/**
	 * @return
	 */
	public Button getCloseButton() {
		return closeButton;
	}

	/**
	 * @param closeButton
	 */
	public void setCloseButton(Button closeButton) {
		this.closeButton = closeButton;
	}

	/**
	 * @return
	 */
	public FileUpload getUpload() {
		return upload;
	}

	/**
	 * @param upload
	 */
	public void setUpload(FileUpload upload) {
		this.upload = upload;
	}

}
