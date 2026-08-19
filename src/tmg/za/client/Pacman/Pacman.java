package tmg.za.client.Pacman;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;

import tmg.za.shared.Direction;
import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

public class Pacman extends Movable {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();
	private String DIRECTION_PRESSED = "";
	private boolean isClosedMouth = false;
	private com.google.gwt.user.client.Timer chompTimer;

	PushButton pbPacman;
	Image pac_right;
	Image pac_down;
	Image pac_left;
	Image pac_up;
	Image pac_closed;
	Image fruit;

	public static final int TILE_SIZE = 30;
	public static final int PACMAN_SIZE = 24;
	public static final int PACMAN_OFFSET = (TILE_SIZE - PACMAN_SIZE) / 2; // 3px offset for centering

	public Pacman() {
		rightStep = 30;
		topStep = 30;
		mass = 3;
		distance = 0;

		pbPacman = new PushButton("Pacman");

		// Initialize images with fixed 24x24 pixel dimensions
		pac_right = createSizedImage(resources.pac_right());
		pac_down = createSizedImage(resources.pac_down());
		pac_left = createSizedImage(resources.pac_left());
		pac_up = createSizedImage(resources.pac_up());
		pac_closed = createSizedImage(resources.pac_closed());
		fruit = new Image(resources.fruit());
	}

	private Image createSizedImage(com.google.gwt.resources.client.ImageResource res) {
		Image img = new Image(res);
		img.setPixelSize(PACMAN_SIZE, PACMAN_SIZE);
		return img;
	}

	public void updatePosition() {
		Image current = getImage();
		if (current != null) {
			current.getElement().getStyle().setPosition(Position.ABSOLUTE);
			// Centered position inside the 30x30 grid cell
			current.getElement().getStyle().setLeft(getRightStep() + PACMAN_OFFSET, Unit.PX);
			current.getElement().getStyle().setTop(getTopStep() + PACMAN_OFFSET, Unit.PX);
		}
	}

	/**
	 * Temporarily switches to pac_closed on collision and reverts back to active
	 * direction face.
	 */
	public void triggerChompAnimation(final Panel canvas) {
		Image oldImage = getImage();
		isClosedMouth = true;
		Image closedImage = getImage();

		if (canvas != null && oldImage != closedImage) {
			canvas.remove(oldImage);
			canvas.add(closedImage);
			updatePosition();
		}

		if (chompTimer != null) {
			chompTimer.cancel();
		}

		chompTimer = new Timer() {
			@Override
			public void run() {
				if (canvas != null) {
					canvas.remove(pac_closed);
				}
				isClosedMouth = false;
				Image openImage = getImage();
				if (canvas != null) {
					canvas.add(openImage);
					updatePosition();
				}
			}
		};
		chompTimer.schedule(180); // Closed mouth duration in ms
	}

	@Override
	public void moveX(int value) {
		// Save history
		setLastLeftVal(getRightStep());
		setLastTopVal(getTopStep());

		// Map_1_4 is 27 columns wide (0 to 26)
		int maxCols = 27;
		int maxPx = (maxCols - 1) * 30; // 780px
		int nextStep = getRightStep() + value;

		// Grid wrapping for teleporting
		if (nextStep < 0) {
			setRightStep(maxPx);
		} else if (nextStep > maxPx) {
			setRightStep(0);
		} else {
			setRightStep(nextStep);
		}

		updatePosition();
	}

	@Override
	public void moveY(int value) {
		// Save history
		setLastLeftVal(getRightStep());
		setLastTopVal(getTopStep());

		// Map_1_4 is 13 rows high (0 to 12)
		int maxRows = 13;
		int maxPx = (maxRows - 1) * 30; // 360px
		int nextStep = getTopStep() + value;

		// Grid wrapping for teleporting
		if (nextStep < 0) {
			setTopStep(maxPx);
		} else if (nextStep > maxPx) {
			setTopStep(0);
		} else {
			setTopStep(nextStep);
		}

		updatePosition();
	}

	@Override
	public Image getImage() {
		if (isClosedMouth) {
			return pac_closed;
		}
		if (getDirection().equalsIgnoreCase(Direction.DOWN)) {
			return pac_down;
		} else if (getDirection().equalsIgnoreCase(Direction.LEFT)) {
			return pac_left;
		} else if (getDirection().equalsIgnoreCase(Direction.UP)) {
			return pac_up;
		} else {
			return pac_right;
		}
	}

	public PushButton getPbPacman() {
		return pbPacman;
	}

	public String getDirection() {
		return DIRECTION_PRESSED;
	}

	public void setDirection(String direction) {
		DIRECTION_PRESSED = direction;
		updatePosition();
	}
}