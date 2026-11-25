package tmg.za.client.SpaceForce;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

public class SpaceForce extends Movable {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();
	private int distance = 0;
	PushButton pbSpaceForce;
	private boolean holdingUp = false;
	private boolean holdingDown = false;
	private double velocityY = 0.0; // pixels per second

	private static final double MAX_SPEED_Y = 260.0; // px/sec
	private static final double ACCEL_Y = 1400.0; // px/sec^2 when held
	private static final double SWAY_DAMPING = 7.5; // higher = snappier settle, lower = longer glide

	public SpaceForce() {
		// image.setResource(resources.ship());
		image = new Image(resources.ship());
		rightStep = 0;
		topStep = 0;
		mass = 1;
		pbSpaceForce = new PushButton("Space Force");
		/*
		 * getRightButton().addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) {
		 * moveX(physics.getForce(getMass(), false, false)); } });
		 * getLeftButton().addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) {
		 * moveX(-physics.getForce(getMass(), false, false)); } });
		 */
		getUpButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveY(-physics.getForce(getMass(), false, false));

			}
		});
		getDownButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveY(physics.getForce(getMass(), false, false));

			}
		});
	}

	public void setHoldingUp(boolean holdingUp) {
		this.holdingUp = holdingUp;
	}

	public void setHoldingDown(boolean holdingDown) {
		this.holdingDown = holdingDown;
	}

	public void clearVerticalInput() {
		this.holdingUp = false;
		this.holdingDown = false;
	}

	public void resetVerticalMotion() {
		this.holdingUp = false;
		this.holdingDown = false;
		this.velocityY = 0.0;
	}

	public void updateSway(double dtSeconds) {
		int direction = 0;
		if (holdingUp) {
			direction -= 1;
		}
		if (holdingDown) {
			direction += 1;
		}

		if (direction != 0) {
			// Accelerate smoothly toward max speed in the held direction
			velocityY += direction * ACCEL_Y * dtSeconds;
			if (velocityY > MAX_SPEED_Y) {
				velocityY = MAX_SPEED_Y;
			} else if (velocityY < -MAX_SPEED_Y) {
				velocityY = -MAX_SPEED_Y;
			}
		} else {
			// Transient sway: smoothly decay velocity after release
			double factor = Math.exp(-SWAY_DAMPING * dtSeconds);
			velocityY *= factor;
			if (Math.abs(velocityY) < 1.5) {
				velocityY = 0.0;
			}
		}

		if (velocityY != 0.0) {
			int deltaY = (int) Math.round(velocityY * dtSeconds);
			if (deltaY != 0) {
				int beforeTop = getTopStep();
				moveY(deltaY);
				// If we hit the top (0) or bottom (375) clamp, cancel leftover velocity
				if (getTopStep() == beforeTop && ((deltaY < 0 && beforeTop == 0) || (deltaY > 0 && beforeTop == 375))) {
					velocityY = 0.0;
				}
			}
		}
	}

	/**
	 * @return
	 */
	public PushButton getPbSpaceForce() {
		return pbSpaceForce;
	}

	/**
	 * @param pbSpaceForce
	 */
	public void setPbSpaceForce(PushButton pbSpaceForce) {
		this.pbSpaceForce = pbSpaceForce;
	}

	@Override
	public void moveX(int value) {
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setLeft(getRightStep() + value, Unit.PX);
		setRightStep(getRightStep() + value);

	}

	@Override
	public void moveY(int value) {
		int nextTop = getTopStep() + value;
		if (nextTop < 0) {
			nextTop = 0;
		} else if (nextTop > 375) {
			nextTop = 375;
		}
		setTopStep(nextTop);
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setTop(nextTop, Unit.PX);
	}

	/**
	 * @return
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

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return image;
	}

}
