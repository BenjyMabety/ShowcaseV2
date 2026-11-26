package tmg.za.client.FileEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * File editor (Not deployed)
 */
public class FileEditor {
	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	private PushButton pbFileEditor;

	private PushButton editButton;
	private PushButton loadButton;
	private PushButton saveButton;
	private PushButton newButton;

	/**
	 * File editor constructor
	 */
	public FileEditor() {
		Image editImage = new Image(resources.edit());
		Image loadImage = new Image(resources.load());
		Image saveImage = new Image(resources.save());
		Image newImage = new Image(resources.new_file());

		pbFileEditor = new PushButton("File Editor");

		editButton = new PushButton(editImage);
		loadButton = new PushButton(loadImage);
		saveButton = new PushButton(saveImage);
		newButton = new PushButton(newImage);

		editButton.setTitle("Edit");
		saveButton.setTitle("Save");
		loadButton.setTitle("Open");
	}

	/**
	 * @return pbFileEditor
	 */
	public PushButton getPbFileEditor() {
		return pbFileEditor;
	}

	/**
	 * @param pbFileEditor
	 */
	public void setPbFileEditor(PushButton pbFileEditor) {
		this.pbFileEditor = pbFileEditor;
	}

	/**
	 * @return editButton
	 */
	public PushButton getEditButton() {
		return editButton;
	}

	/**
	 * @param editButton
	 */
	public void setEditButton(PushButton editButton) {
		this.editButton = editButton;
	}

	/**
	 * @return loadButton
	 */
	public PushButton getLoadButton() {
		return loadButton;
	}

	/**
	 * @param loadButton
	 */
	public void setLoadButton(PushButton loadButton) {
		this.loadButton = loadButton;
	}

	/**
	 * @return saveButton
	 */
	public PushButton getSaveButton() {
		return saveButton;
	}

	/**
	 * @param saveButton
	 */
	public void setSaveButton(PushButton saveButton) {
		this.saveButton = saveButton;
	}

	/**
	 * @return newButton
	 */
	public PushButton getNewButton() {
		return newButton;
	}

	/**
	 * @param newButton
	 */
	public void setNewButton(PushButton newButton) {
		this.newButton = newButton;
	}

}
