package tmg.za.client.SpaceForce;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;

import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

public class Asteroid extends Movable {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();
	int hits = 0;

	public Asteroid() {
		// image.setResource(resources.asteroid());
		image = new Image(resources.asteroid());
		mass = 3;
		rightStep = 0;
		topStep = 0;
		image.setVisible(false);
	}

	@Override
	public void moveX(int value) {

		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setTop(getTopStep() - 60, Unit.PX);
		image.getElement().getStyle().setLeft(getRightStep() + value, Unit.PX);
		image.setVisible(true);
		setRightStep(getRightStep() + value);
		setTopStep(getTopStep());

	}

	@Override
	public void moveY(int value) {

	}

	@Override
	public Image getImage() {
		return image;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits += hits;
	}

}
