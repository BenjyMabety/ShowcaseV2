package tmg.za.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

import tmg.za.shared.Movable;
import tmg.za.shared.Physics;

/**
 * 
 */
public class Ball extends Movable {

	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();

	// Suspended in air and subject to gravity
	private boolean suspended = true;
	private boolean moving = false;

	private boolean stationary = false;

	PushButton pbBall;
	ToggleButton tbFriction;
	private Audio bounce;

	/**
	 * 
	 */
	public Ball() {
		image = new Image(resources.ball());
		rightStep = 0;
		topStep = 0;
		mass = 3;
		distance = 0;
		pbBall = new PushButton("Ball (Physics)");
		tbFriction = new ToggleButton("Friction (on/off)");
		tbFriction.setValue(true);
		bounce = Audio.createIfSupported();
		if (bounce != null) {
			bounce.setSrc("waves/bounce.wav");
		}

		getRightButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveX(physics.getForce(getMass(), false, false));

			}
		});
		getLeftButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				moveX(-physics.getForce(getMass(), false, false));

			}
		});

		getUpButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				doUpLogic();

			}
		});

		getDownButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				doDownLogic();

			}
		});

		getPbKeyboard().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
					moveX(physics.getForce(getMass(), false, false));
				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
					moveX(-physics.getForce(getMass(), false, false));
				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					if (!isMoving()) {
						doUpLogic();
					}
					if (isStationary()) {
						doUpLogic();
					}
				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					if (!isMoving()) {
						doDownLogic();
					}
				}
			}
		});

		getTbFriction().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				physics.setFriction(event.getValue());

			}
		});
	}

	/**
	 * @return
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * @param suspended
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * @return
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * @param moving
	 */
	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	/**
	 * Used to switch toggle on or off depending on friction setting
	 * 
	 * @param distance
	 */
	public void setRunningDistance(int distance) {
		this.distance += distance;
	}

	/**
	 * @return
	 */
	public boolean isStationary() {
		return stationary;
	}

	/**
	 * @param stationary
	 */
	public void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	/**
	 * @return
	 */
	public ToggleButton getTbFriction() {
		return tbFriction;
	}

	/**
	 * @param tbFriction
	 */
	public void setTbFriction(ToggleButton tbFriction) {
		this.tbFriction = tbFriction;
	}

	/**
	 * @return
	 */
	public PushButton getPbBall() {
		return pbBall;
	}

	/**
	 * @param pbBall
	 */
	public void setPbBall(PushButton pbBall) {
		this.pbBall = pbBall;
	}

	/**
	 * @return
	 */
	public Audio getBounce() {
		return bounce;
	}

	/**
	 * @param bounce
	 */
	public void setBounce(Audio bounce) {
		this.bounce = bounce;
	}

	/**
	 * @param value
	 */
	public void moveX(int value) {

		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setLeft(getRightStep() + value, Unit.PX);
		setRightStep(getRightStep() + value);

	}

	/**
	 * @param value
	 * @param running
	 */
	protected void moveY(int value, boolean running) {

		if (running) {
			if (isSuspended()) {
				if (image.getAbsoluteTop() <= 480) {// checks bottom edge of background Image
					moveY(value);
				} else {
					setSuspended(false);
				}
			} else {

				moveY(value);
			}
		} else {
			moveY(value);
		}

	}

	/**
	 *
	 */
	public void moveY(int value) {
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setTop(getTopStep() + value, Unit.PX);
		setTopStep(getTopStep() + value);
	}

	/**
	 * 
	 */
	protected void doUpLogic() {
		setSuspended(true);
		setMoving(false);
		setStationary(false);
		setDistance(0);
		moveY(-physics.getForce(getMass(), false, false), false);

	}

	/**
	 * 
	 */
	protected void doDownLogic() {
		if (getUpButton().isEnabled() && getDownButton().isEnabled() && !isStationary()) {
			getUpButton().setEnabled(false);
			getDownButton().setEnabled(false);
		}
		if (!isStationary()) {
			if (isSuspended()) {
				setMoving(true);
			}
			if (isSuspended() && isMoving()) {
				Timer t = new Timer() {

					@Override
					public void run() {
						if (isSuspended()) {
							moveY(physics.getForce(getMass(), isMoving(), false), isRunning());
						} else {
							if (getImage().getAbsoluteTop() >= getArc()) {
								moveY(physics.getForce(getMass(), isMoving(), true), isRunning());

							} else {
								setMoving(true);
								setSuspended(true);
							}
						}
					}

					private int getArc() {
						// Hard coded edge to 181 based on BG image used
						int bounce = 181 + getDistance();
						if (physics.isFriction()) {
							setRunningDistance(getMass());
						}
						if (getDistance() == 330) { // arbitrary bounce frequency value
							setMoving(false);
							setSuspended(false);
							setStationary(true);
							getUpButton().setEnabled(true);
							getDownButton().setEnabled(true);
							cancel();
						}
						return bounce;
					}
				};

				t.scheduleRepeating(physics.getForce(getMass(), isMoving(), false));
			} else {
				moveY(physics.getForce(getMass(), isMoving(), false), false);
			}

		}

	}

	@Override
	public Image getImage() {
		return image;
	}
}
