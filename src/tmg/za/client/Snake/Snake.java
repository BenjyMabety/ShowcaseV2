package tmg.za.client.Snake;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

import tmg.za.shared.Direction;
import tmg.za.shared.Movable;
import tmg.za.shared.Physics;
import tmg.za.shared.StringUtils;

public class Snake extends Movable {
	tmg.za.client.Resources.Resources resources = GWT.create(tmg.za.client.Resources.Resources.class);
	Physics physics = new Physics();
	private String DIRECTION_PRESSED = "";
	private String PREVIOUS_DIRECTION = "";

	private boolean head = false;

	private PushButton pbSnake;
	private String lastLeft = "";
	private String lastTop = "";
	private double lastLeftVal = 0;
	private double lastTopVal = 0;
	private ArrayList<Snake> children = new ArrayList<Snake>();

	public Snake() {

		rightStep = 0;
		topStep = 0;
		mass = 3;
		distance = 0;
		/*
		 * childTop = 0.0; childLeft = 0.0;
		 */

		pbSnake = new PushButton("Snake");
		image = new Image(resources.snake());

		getRightButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setPreviousDirection(getDirection());
				DIRECTION_PRESSED = Direction.RIGHT;

			}
		});
		getLeftButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setPreviousDirection(getDirection());
				DIRECTION_PRESSED = Direction.LEFT;

			}
		});

		getUpButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setPreviousDirection(getDirection());
				DIRECTION_PRESSED = Direction.UP;

			}
		});

		getDownButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setPreviousDirection(getDirection());
				DIRECTION_PRESSED = Direction.DOWN;

			}
		});

		getPbKeyboard().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_RIGHT) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.RIGHT;

				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_LEFT) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.LEFT;

				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.UP;

				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.DOWN;

				}

			}
		});

	}

	@Override
	public void moveX(int value) {
		lastLeft = image.getElement().getStyle().getLeft().replace("px", "");
		lastTop = image.getElement().getStyle().getTop().replace("px", "");
		double lastLeftVal = Double.valueOf(StringUtils.getNValue(lastLeft));
		double lastTopVal = Double.valueOf(StringUtils.getNValue(lastTop));
		setLastLeftVal(lastLeftVal);
		setLastTopVal(lastTopVal);

		if (getDirection().equalsIgnoreCase(Direction.RIGHT)) {
			if (!getPreviousDirection().equals(Direction.LEFT)) {
				if (image.getAbsoluteLeft() < 1178) {
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setLeft(getRightStep() + value, Unit.PX);
					setRightStep(getRightStep() + value);
				} else {
					setRightStep(0);
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setLeft(getRightStep(), Unit.PX);
					setRightStep(getRightStep() + value);
				}
			}
		} else {
			if (!getPreviousDirection().equals(Direction.RIGHT)) {
				if (image.getAbsoluteLeft() > 308) {

					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setLeft(getRightStep() + value, Unit.PX);
					setRightStep(getRightStep() + value);
				} else {
					setRightStep(735);
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setLeft(getRightStep(), Unit.PX);
					setRightStep(getRightStep() + value);
				}
			}
		}

	}

	@Override
	public void moveY(int value) {
		lastLeft = image.getElement().getStyle().getLeft().replace("px", "");
		lastTop = image.getElement().getStyle().getTop().replace("px", "");
		double lastLeftVal = Double.valueOf(StringUtils.getNValue(lastLeft));
		double lastTopVal = Double.valueOf(StringUtils.getNValue(lastTop));
		setLastLeftVal(lastLeftVal);
		setLastTopVal(lastTopVal);

		if (getDirection().equalsIgnoreCase(Direction.DOWN)) {
			if (!getPreviousDirection().equals(Direction.UP)) {
				if (image.getAbsoluteTop() < 700) {
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setTop(getTopStep() + value, Unit.PX);
					setTopStep(getTopStep() + value);
				} else {
					setTopStep(0);
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setTop(getTopStep(), Unit.PX);
					setTopStep(getTopStep());
				}
			}
		} else {
			if (!getPreviousDirection().equals(Direction.DOWN)) {
				if (image.getAbsoluteTop() > 133) {

					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setTop(getTopStep() + value, Unit.PX);
					setTopStep(getTopStep() + value);
				} else {
					setTopStep(535);
					image.getElement().getStyle().setPosition(Position.ABSOLUTE);
					image.getElement().getStyle().setTop(getTopStep(), Unit.PX);
					setTopStep(getTopStep() + value);
				}
			}
		}
	}

	@Override
	public Image getImage() {
		return image;
	}

	/**
	 * @return
	 */
	public PushButton getPbSnake() {
		return pbSnake;
	}

	/**
	 * @param pbSnake
	 */
	public void setPbSnake(PushButton pbSnake) {
		this.pbSnake = pbSnake;
	}

	/**
	 * @return
	 */
	public String getDirection() {
		return DIRECTION_PRESSED;
	}

	/**
	 * @param direction
	 */
	public void setDirection(String direction) {
		DIRECTION_PRESSED = direction;
	}

	/**
	 * @param fruit
	 * @return
	 */
	public boolean eat(Fruit fruit) {
		if (getDirection().equalsIgnoreCase(Direction.RIGHT)) {
			if ((image.getElement().getAbsoluteRight() > fruit.getImage().getElement().getAbsoluteLeft()
					&& image.getElement().getAbsoluteBottom() > fruit.getImage().getElement().getAbsoluteTop())
					&& ((image.getElement().getAbsoluteRight() - fruit.getImage().getElement().getAbsoluteLeft() <= 30)
							&& (image.getElement().getAbsoluteBottom() - fruit.getImage().getAbsoluteTop() <= 60)))
				return true;
		} else if (getDirection().equalsIgnoreCase(Direction.LEFT)) {
			if ((image.getElement().getAbsoluteLeft() < fruit.getImage().getElement().getAbsoluteRight()
					&& image.getElement().getAbsoluteBottom() > fruit.getImage().getElement().getAbsoluteTop())
					&& ((image.getElement().getAbsoluteRight() - fruit.getImage().getElement().getAbsoluteLeft() >= -30)
							&& (image.getElement().getAbsoluteBottom() - fruit.getImage().getAbsoluteTop() <= 60)))
				return true;

		} else if (getDirection().equalsIgnoreCase(Direction.DOWN)) {
			if ((image.getElement().getAbsoluteBottom() > fruit.getImage().getElement().getAbsoluteTop())
					&& image.getElement().getAbsoluteBottom() - fruit.getImage().getAbsoluteTop() <= 30) {
				if ((image.getElement().getAbsoluteRight() > fruit.getImage().getElement().getAbsoluteRight())
						&& (image.getElement().getAbsoluteLeft() < fruit.getImage().getAbsoluteLeft())) {
					return true;
				}
			}
		} else {
			if ((image.getElement().getAbsoluteTop() < fruit.getImage().getElement().getAbsoluteBottom())
					&& (fruit.getImage().getElement().getAbsoluteBottom()
							- image.getElement().getAbsoluteTop() <= 30)) {
				if ((image.getElement().getAbsoluteRight() > fruit.getImage().getElement().getAbsoluteRight())
						&& (image.getElement().getAbsoluteLeft() < fruit.getImage().getAbsoluteLeft())) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean isHead() {
		return head;
	}

	/**
	 * @param head
	 */
	public void setHead(boolean head) {
		this.head = head;
	}

	/**
	 * @param child
	 */
	public void addTail(Snake child) {

		Snake lastChild = getLastChild();
		child.setLastLeft(lastChild.getImage().getElement().getStyle().getLeft().replace("px", ""));
		child.setLastTop(lastChild.getImage().getElement().getStyle().getTop().replace("px", ""));

		child.getImage().getElement().getStyle().setProperty("left",
				lastChild.getImage().getElement().getStyle().getLeft());
		child.getImage().getElement().getStyle().setProperty("top",
				lastChild.getImage().getElement().getStyle().getTop());
		String left = lastChild.getImage().getElement().getStyle().getLeft().replace("px", "");
		String top = lastChild.getImage().getElement().getStyle().getTop().replace("px", "");

		child.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
		// Child must follow parent
		// Up top of this method, get the index of the last entry and the child must
		// follow the last co ordinates of the parent.

		// print out lastchild last co ordinates for every child and snake's last co
		// ordinates
		// confirm all the children are spawned to follow snake
		// they should not
		child.setLastLeftVal((Double.valueOf(left)));
		child.setLastTopVal(Double.valueOf(top));

		children.add(child);

	}

	/**
	 * @return
	 */
	private Snake getLastChild() {
		if (children.isEmpty()) {
			return this;
		}
		int index = children.size() - 1;
		return children.get(index);
	}

	/**
	 * @return
	 */
	public String getLastLeft() {
		return lastLeft;
	}

	/**
	 * @param lastLeft
	 */
	public void setLastLeft(String lastLeft) {
		this.lastLeft = lastLeft;
	}

	/**
	 * @return
	 */
	public String getLastTop() {
		return lastTop;
	}

	/**
	 * @param lastTop
	 */
	public void setLastTop(String lastTop) {
		this.lastTop = lastTop;
	}

	/**
	 * @return
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
	 * @return
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

	public void move() {

		for (Snake child : children) {
			if (getDirection().equalsIgnoreCase(Direction.LEFT) || getDirection().equalsIgnoreCase(Direction.RIGHT)) {
				child.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
				child.getImage().getElement().getStyle().setLeft(getParent(child).getLastLeftVal() - 35, Unit.PX);
				child.getImage().getElement().getStyle().setTop(getParent(child).getLastTopVal(), Unit.PX);

				child.setLastLeftVal(getParent(child).getLastLeftVal() - 35);
				child.setLastTopVal(getParent(child).getLastTopVal());
			} else {

				child.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
				child.getImage().getElement().getStyle().setLeft(getParent(child).getLastLeftVal(), Unit.PX);
				child.getImage().getElement().getStyle().setTop(getParent(child).getLastTopVal() - 35, Unit.PX);

				child.setLastLeftVal(getParent(child).getLastLeftVal());
				child.setLastTopVal(getParent(child).getLastTopVal() - 35);

			}
		}

	}

	/**
	 * @param child
	 * @return
	 */
	private Snake getParent(Snake child) {
		if (children.isEmpty()) {
			return this;
		}

		int index = children.indexOf(child);
		if (index > 0) {
			return children.get(index - 1);
		}
		return this;
	}

	/**
	 * @return
	 */
	public ArrayList<Snake> getChildren() {
		return children;
	}

	/**
	 * @param children
	 */
	public void setChildren(ArrayList<Snake> children) {
		this.children = children;
	}

	/**
	 * @return
	 */
	public String getPreviousDirection() {
		return PREVIOUS_DIRECTION;
	}

	/**
	 * @param previousDirection
	 */
	public void setPreviousDirection(String previousDirection) {
		PREVIOUS_DIRECTION = previousDirection;
	}

	/**
	 * 
	 */
	public void reset() {
		for (Snake child : children) {
			child.getImage().removeFromParent();
		}
		children.clear();
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setLeft(0, Unit.PX);
		image.getElement().getStyle().setTop(0, Unit.PX);

	}

}
