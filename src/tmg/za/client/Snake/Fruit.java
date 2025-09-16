package tmg.za.client.Snake;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

public class Fruit extends Movable {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();

	public Fruit() {
		image = new Image(resources.fruit());
		mass = 3;
		rightStep = 0;
		topStep = 0;

	}

	@Override
	public void moveX(int value) {

	}

	@Override
	public void moveY(int value) {

	}

	@Override
	public Image getImage() {
		return image;
	}

}
