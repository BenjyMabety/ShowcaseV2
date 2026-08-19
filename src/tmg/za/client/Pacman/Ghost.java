package tmg.za.client.Pacman;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;

import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

public class Ghost extends Movable {

	public enum GhostType {
		BLACK, WHITE, GREY, BLUE
	}

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();

	private GhostType type;
	private Image black_ghost;
	private Image white_ghost;
	private Image grey_ghost;
	private Image blue_ghost;

	public static final int GHOST_SIZE = 24;
	public static final int GHOST_OFFSET = (30 - GHOST_SIZE) / 2; // 3px centering offset

	public Ghost(GhostType type) {
		this.type = type;
		rightStep = 30;
		topStep = 30;
		mass = 3;
		distance = 0;

		black_ghost = createSizedImage(resources.black_ghost());
		white_ghost = createSizedImage(resources.white_ghost());
		grey_ghost = createSizedImage(resources.grey_ghost());
		blue_ghost = createSizedImage(resources.blue_ghost());
	}

	public GhostType getType() {
		return type;
	}

	public void setType(GhostType type) {
		this.type = type;
	}

	public void updatePosition() {
		Image current = getImage();
		if (current != null) {
			current.getElement().getStyle().setPosition(Position.ABSOLUTE);
			current.getElement().getStyle().setLeft(getRightStep() + GHOST_OFFSET, Unit.PX);
			current.getElement().getStyle().setTop(getTopStep() + GHOST_OFFSET, Unit.PX);
		}
	}

	@Override
	public void moveX(int value) {
		setRightStep(getRightStep() + value);
		updatePosition();
	}

	@Override
	public void moveY(int value) {
		setTopStep(getTopStep() + value);
		updatePosition();
	}

	@Override
	public Image getImage() {
		if (type == GhostType.WHITE) {
			return white_ghost;
		} else if (type == GhostType.GREY) {
			return grey_ghost;
		} else if (type == GhostType.BLUE) {
			return blue_ghost;
		} else {
			return black_ghost;
		}
	}

	private Image createSizedImage(com.google.gwt.resources.client.ImageResource res) {
		Image img = new Image(res);
		img.setPixelSize(GHOST_SIZE, GHOST_SIZE);
		return img;
	}
}