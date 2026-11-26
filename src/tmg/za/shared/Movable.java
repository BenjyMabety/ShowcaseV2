package tmg.za.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Base class for movable objects
 */
public abstract class Movable extends Composite {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);

	Image up = new Image(resources.up());
	Image down = new Image(resources.down());
	Image left = new Image(resources.left());
	Image right = new Image(resources.right());
	Image keyboard = new Image(resources.keyboard());

	PushButton upButton = new PushButton(up);
	PushButton downButton = new PushButton(down);
	PushButton leftButton = new PushButton(left);
	PushButton rightButton = new PushButton(right);
	PushButton pbKeyboard = new PushButton(keyboard);

	public Image image;
	protected int mass;
	protected int rightStep;
	protected int topStep;
	protected int distance;

	// Added coordinate history fields to prevent compilation errors
	protected double lastLeftVal = 0;
	protected double lastTopVal = 0;

	/**
	 * @return upButton
	 */
	public PushButton getUpButton() {
		return upButton;
	}

	/**
	 * @return downButton
	 */
	public PushButton getDownButton() {
		return downButton;
	}

	/**
	 * @return leftButton
	 */
	public PushButton getLeftButton() {
		return leftButton;
	}

	/**
	 * @return rightButton
	 */
	public PushButton getRightButton() {
		return rightButton;
	}

	/**
	 * @return pbKeyboard
	 */
	public PushButton getPbKeyboard() {
		return pbKeyboard;
	}

	/**
	 * @param value
	 */
	public abstract void moveX(int value);

	/**
	 * @param value
	 */
	public abstract void moveY(int value);

	/**
	 * @return Image
	 */
	public abstract Image getImage();

	/**
	 * @return mass
	 */
	public int getMass() {
		return mass;
	}

	/**
	 * @param mass
	 */
	public void setMass(int mass) {
		this.mass = mass;
	}

	/**
	 * @return rightStep
	 */
	public int getRightStep() {
		return rightStep;
	}

	/**
	 * @param rightStep
	 */
	public void setRightStep(int rightStep) {
		this.rightStep = rightStep;
	}

	/**
	 * @return topStep
	 */
	public int getTopStep() {
		return topStep;
	}

	/**
	 * @param topStep
	 */
	public void setTopStep(int topStep) {
		this.topStep = topStep;
	}

	/**
	 * @return distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	// Added Explicit Getters and Setters for shared tracking logic
	/**
	 * @return lastLeftVal
	 */
	public double getLastLeftVal() {
		return lastLeftVal;
	}

	/**
	 * @param lastLeftVal
	 */
	public void setLastLeftVal(double lastLeftVal) {
		this.lastLeftVal = lastLeftVal;
	}

	/**
	 * @return lastTopVal
	 */
	public double getLastTopVal() {
		return lastTopVal;
	}

	/**
	 * @param lastTopVal
	 */
	public void setLastTopVal(double lastTopVal) {
		this.lastTopVal = lastTopVal;
	}
}
