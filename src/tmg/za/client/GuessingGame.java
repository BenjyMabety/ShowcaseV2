package tmg.za.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class GuessingGame extends DialogBox {

	private static GuessingGameUiBinder uiBinder = GWT.create(GuessingGameUiBinder.class);

	interface GuessingGameUiBinder extends UiBinder<Widget, GuessingGame> {
	}

	@UiField
	Button closeButton;
	@UiField
	Button enterButton;
	@UiField
	TextBox tbMagic;
	@UiField
	Label labelFeed;
	@UiField
	Label labelAttempts;

	private PushButton pbGuess;

	private double magic = 0;
	private int attempts = 0;

	/**
	 * Guess the magic number between 1 and 100
	 */
	public GuessingGame() {
		setWidget(uiBinder.createAndBindUi(this));
		pbGuess = new PushButton("Guessing Game");
		closeButton.setText("Close");
		enterButton.setText("Enter");
		magic = Math.round(Math.random() * 100);

		closeButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				close();
			}

			/**
			 * close and reset
			 */
			private void close() {
				hide();
				attempts = 0;
				labelAttempts.setText("");
				labelFeed.setText("");
				tbMagic.setValue("");
				magic = Math.round(Math.random() * 100);

			}
		});
		ClickHandler myHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				enter();
			}
		};
		enterButton.addClickHandler(myHandler);
		tbMagic.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					enter();
				}
			}
		});
	}

	/**
	 * Enter the input
	 */
	protected void enter() {
		labelFeed.setText("");
		labelFeed.setText(evaluateResponse());
		attempts++;
		labelAttempts.setText(attempts + "");

	}

	/**
	 * Evaluate magic
	 * 
	 * @return result
	 */
	public String evaluateResponse() {
		if (Double.valueOf(tbMagic.getValue()) > magic) {
			return "lOWER";
		} else if (Double.valueOf(tbMagic.getValue()) < magic) {
			return "Higher";
		} else {
			return "MAGIC!";
		}
	}

	/**
	 * @return
	 */
	public PushButton getPbGuess() {
		return pbGuess;
	}

	/**
	 * @param pbGuess
	 */
	public void setPbGuess(PushButton pbGuess) {
		this.pbGuess = pbGuess;
	}

}
