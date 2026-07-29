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
				// Block going RIGHT if physically traveling LEFT
				if (!getDirection().equalsIgnoreCase(Direction.LEFT)) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.RIGHT;
				}
			}
		});

		getLeftButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Block going LEFT if physically traveling RIGHT
				if (!getDirection().equalsIgnoreCase(Direction.RIGHT)) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.LEFT;
				}
			}
		});

		getUpButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Block going UP if physically traveling DOWN
				if (!getDirection().equalsIgnoreCase(Direction.DOWN)) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.UP;
				}
			}
		});

		getDownButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Block going DOWN if physically traveling UP
				if (!getDirection().equalsIgnoreCase(Direction.UP)) {
					setPreviousDirection(getDirection());
					DIRECTION_PRESSED = Direction.DOWN;
				}
			}
		});

		getPbKeyboard().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				int keyCode = event.getNativeKeyCode();
				String currentDir = getDirection();

				if (keyCode == KeyCodes.KEY_RIGHT && !currentDir.equalsIgnoreCase(Direction.LEFT)) {
					setPreviousDirection(currentDir);
					DIRECTION_PRESSED = Direction.RIGHT;
				} else if (keyCode == KeyCodes.KEY_LEFT && !currentDir.equalsIgnoreCase(Direction.RIGHT)) {
					setPreviousDirection(currentDir);
					DIRECTION_PRESSED = Direction.LEFT;
				} else if (keyCode == KeyCodes.KEY_UP && !currentDir.equalsIgnoreCase(Direction.DOWN)) {
					setPreviousDirection(currentDir);
					DIRECTION_PRESSED = Direction.UP;
				} else if (keyCode == KeyCodes.KEY_DOWN && !currentDir.equalsIgnoreCase(Direction.UP)) {
					setPreviousDirection(currentDir);
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
	public boolean eat(Movable fruit) {
		// 1. Extract and clean the Snake Head coordinates
		String headLeftStr = this.getImage().getElement().getStyle().getLeft().replace("px", "").trim();
		String headTopStr = this.getImage().getElement().getStyle().getTop().replace("px", "").trim();
		double headX1 = headLeftStr.isEmpty() ? 0.0 : Double.valueOf(headLeftStr);
		double headY1 = headTopStr.isEmpty() ? 0.0 : Double.valueOf(headTopStr);

		// Define Head edges based on its 48x48 px dimension
		double headX2 = headX1 + 48;
		double headY2 = headY1 + 48;

		// 2. Extract and clean the Fruit coordinates
		String fruitLeftStr = fruit.getImage().getElement().getStyle().getLeft().replace("px", "").trim();
		String fruitTopStr = fruit.getImage().getElement().getStyle().getTop().replace("px", "").trim();
		double fruitX1 = fruitLeftStr.isEmpty() ? 0.0 : Double.valueOf(fruitLeftStr);
		double fruitY1 = fruitTopStr.isEmpty() ? 0.0 : Double.valueOf(fruitTopStr);

		// Define Fruit edges based on its 18x24 px dimension
		double fruitX2 = fruitX1 + 18;
		double fruitY2 = fruitY1 + 24;

		// 3. Evaluate 2D Bounding Box Overlap Condition
		// Returns true if the rectangles overlap on both axes
		boolean xOverlap = (headX1 < fruitX2) && (headX2 > fruitX1);
		boolean yOverlap = (headY1 < fruitY2) && (headY2 > fruitY1);

		return xOverlap && yOverlap;
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

		// Read the current position of the last tail segment
		String currentLeft = lastChild.getImage().getElement().getStyle().getLeft();
		String currentTop = lastChild.getImage().getElement().getStyle().getTop();

		// If it's the very first spawn, place it right at the current segment position
		child.getImage().getElement().getStyle().setPosition(Position.ABSOLUTE);
		child.getImage().getElement().getStyle().setProperty("left", currentLeft);
		child.getImage().getElement().getStyle().setProperty("top", currentTop);

		// Initialize tracking values clean of text noise
		double leftVal = Double.valueOf(currentLeft.replace("px", ""));
		double topVal = Double.valueOf(currentTop.replace("px", ""));
		child.setLastLeftVal(leftVal);
		child.setLastTopVal(topVal);

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
		// Cascade positions from back to front
		for (int i = children.size() - 1; i >= 0; i--) {
			Snake currentChild = children.get(i);
			Movable parentNode = (i == 0) ? this : children.get(i - 1);

			// Grab parentNode's historical coordinate location before its current update
			double targetLeft = parentNode.getLastLeftVal();
			double targetTop = parentNode.getLastTopVal();

			// Apply historical parent values directly to the child styles
			currentChild.getImage().getElement().getStyle().setLeft(targetLeft, Unit.PX);
			currentChild.getImage().getElement().getStyle().setTop(targetTop, Unit.PX);

			// Store current coordinates as history for the next downstream child
			currentChild.setLastLeftVal(targetLeft);
			currentChild.setLastTopVal(targetTop);
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
		// 1. Remove all old tail segment images from the canvas layout
		for (Snake child : children) {
			child.getImage().removeFromParent();
		}
		children.clear();

		// 2. Position the main head back to the top-left corner origin
		image.getElement().getStyle().setPosition(Position.ABSOLUTE);
		image.getElement().getStyle().setLeft(0, Unit.PX);
		image.getElement().getStyle().setTop(0, Unit.PX);

		// 3. Clear out historical variables so the tail doesn't spawn elsewhere
		setLastLeftVal(0.0);
		setLastTopVal(0.0);
		setRightStep(0);
		setTopStep(0);

		// 4. Force direction back to moving right instantly so it is never stationary
		setDirection(Direction.RIGHT);
	}

}
