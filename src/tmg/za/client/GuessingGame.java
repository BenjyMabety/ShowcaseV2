package tmg.za.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
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
	Button shareButton;
	@UiField
	TextBox tbMagic;
	@UiField
	Label labelFeed;
	@UiField
	Label labelAttempts;

	private PushButton pbGuess;

	private double magic = 0;
	private int attempts = 0;
	private int maxAttempts = 6;

	/**
	 * Guess the magic number between 1 and 100
	 */
	public GuessingGame() {
		setWidget(uiBinder.createAndBindUi(this));
		tbMagic.getElement().setAttribute("type", "number");
		tbMagic.getElement().setAttribute("min", "1");
		tbMagic.getElement().setAttribute("max", "100");
		tbMagic.getElement().setAttribute("step", "1");
		pbGuess = new PushButton("Guessing Game");
		closeButton.setText("Close");
		enterButton.setText("Enter");
		shareButton.setText("Share");
		magic = (int) (Math.random() * 100) + 1;

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
				enterButton.setEnabled(true);
				shareButton.setVisible(false);
				magic = (int) (Math.random() * 100) + 1;

			}
		});
		shareButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				openShareMenu();
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
		if (!enterButton.isEnabled()) {
			return;
		}

		attempts++;
		labelAttempts.setText(attempts + "/" + maxAttempts);

		String result = evaluateResponse();
		if ("MAGIC!".equals(result)) {
			enterButton.setEnabled(false);
			labelFeed.setText("MAGIC! You got it in " + attempts + " attempts.");
			shareButton.setVisible(true);
		} else if (attempts >= maxAttempts) {
			enterButton.setEnabled(false);
			labelFeed.setText("Game Over! The magic number was " + (int) magic + ".");
		} else {
			labelFeed.setText(result);
		}
	}

	/**
	 * Evaluate magic
	 * 
	 * @return result
	 */
	public String evaluateResponse() {
		String raw = tbMagic.getValue();
		if (raw == null || raw.trim().isEmpty()) {
			return "Enter a number between 1 and 100";
		}
		double guess;
		try {
			guess = Double.parseDouble(raw.trim());
		} catch (NumberFormatException ex) {
			return "Enter a valid number";
		}
		if (guess < 1 || guess > 100) {
			return "Keep it between 1 and 100";
		}
		if (guess > magic) {
			return "Lower";
		} else if (guess < magic) {
			return "Higher";
		} else {
			return "MAGIC!";
		}
	}

	private String buildShareText() {
		return "I guessed the Magic Number (" + (int) magic + ") in " + attempts + "/" + maxAttempts
				+ " attempts on Ben's Java GWT Showcase!";
	}

	private void openShareMenu() {
		String rawText = buildShareText();
		String currentUrl = Window.Location.getHref();
		String encodedText = URL.encodeQueryString(rawText);
		String encodedUrl = URL.encodeQueryString(currentUrl);

		com.google.gwt.user.client.ui.PopupPanel sharePopup = new com.google.gwt.user.client.ui.PopupPanel(true);
		com.google.gwt.user.client.ui.VerticalPanel box = new com.google.gwt.user.client.ui.VerticalPanel();
		box.setSpacing(6);

		com.google.gwt.user.client.ui.Anchor shareWhatsApp = new com.google.gwt.user.client.ui.Anchor("WhatsApp", true,
				"https://wa.me/?text=" + URL.encodeQueryString(rawText + " " + currentUrl));
		shareWhatsApp.getElement().setAttribute("target", "_blank");
		shareWhatsApp.getElement().setAttribute("rel", "noopener noreferrer");

		com.google.gwt.user.client.ui.Anchor shareX = new com.google.gwt.user.client.ui.Anchor("X", true,
				"https://twitter.com/intent/tweet?text=" + encodedText + "&url=" + encodedUrl);
		shareX.getElement().setAttribute("target", "_blank");
		shareX.getElement().setAttribute("rel", "noopener noreferrer");

		box.add(shareWhatsApp);
		box.add(shareX);

		sharePopup.setWidget(box);
		sharePopup.showRelativeTo(shareButton);
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
