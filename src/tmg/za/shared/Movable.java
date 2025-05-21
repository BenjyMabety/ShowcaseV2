/**
 * 
 */
package tmg.za.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * 
 */

public abstract class Movable extends Composite {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);

	Image up = new Image(resources.up());
	Image down = new Image(resources.down());
	Image left = new Image(resources.left());
	Image right = new Image(resources.right());
	Image keyboard = new Image(resources.keyboard());

	PushButton upButton = new PushButton(up);;
	PushButton downButton = new PushButton(down);;
	PushButton leftButton = new PushButton(left);
	PushButton rightButton = new PushButton(right);
	PushButton pbKeyboard = new PushButton(keyboard);

	public Image image;
	protected int mass;
	protected int rightStep;
	protected int topStep;
	protected int distance;

	/**
	 * @return
	 */
	public PushButton getUpButton() {
		return upButton;
	}

	/**
	 * @return
	 */
	public PushButton getDownButton() {
		return downButton;
	}

	/**
	 * @return
	 */
	public PushButton getLeftButton() {
		return leftButton;
	}

	/**
	 * @return
	 */
	public PushButton getRightButton() {
		return rightButton;
	}

	/**
	 * @return
	 */
	public PushButton getPbKeyboard() {
		return pbKeyboard;
	}

	/**
	 * @param value
	 */
	public abstract void moveX(int value);

	public abstract void moveY(int value);

	public abstract Image getImage();

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public int getRightStep() {
		return rightStep;
	}

	public void setRightStep(int rightStep) {
		this.rightStep = rightStep;
	}

	public int getTopStep() {
		return topStep;
	}

	public void setTopStep(int topStep) {
		this.topStep = topStep;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
