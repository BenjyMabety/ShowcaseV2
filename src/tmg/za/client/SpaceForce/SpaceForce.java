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
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setTop(getTopStep() + value, Unit.PX);
		setTopStep(getTopStep() + value);
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
