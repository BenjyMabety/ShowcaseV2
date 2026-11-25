/**
 * 
 */
package tmg.za.shared;

/**
 * Physics engine
 */
public class Physics {

	private int force = 0;
	private int gravity = 10;
	private int acceleration = 15;
	private double drag = 0.2;
	private int resistance = 0;
	private boolean friction = true;
	private int space = 100;

	public Physics() {
	}

	/**
	 * @param mass
	 * @param isFalling
	 * @param bounce
	 * @return
	 */
	public int getForce(int mass, boolean isFalling, boolean bounce) {
		if (isFalling && !bounce) {
			force = mass * gravity;
			return force;
		} else if (isFalling && bounce) {
			resistance = (int) (Integer.valueOf(mass * gravity) * drag);
			force = (mass * gravity) - resistance;
			return -force;
		}
		force = mass * acceleration;
		return force;
	}

	/**
	 * @return
	 */
	public int getGravity() {
		return gravity;
	}

	/**
	 * @param gravity
	 */
	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

	/**
	 * @return
	 */
	public boolean isFriction() {
		return friction;
	}

	/**
	 * @param friction
	 */
	public void setFriction(boolean friction) {
		this.friction = friction;
	}

	/**
	 * @return
	 */
	public int getSpace() {
		return space;
	}

	/**
	 * @param space
	 */
	public void setSpace(int space) {
		this.space = space;
	}

	/**
	 * @param m1
	 * @param m2
	 * @return
	 */
	public boolean checkCollision(Movable m1, Movable m2) {
		if (m1 == null || m2 == null || m1.getImage() == null || m2.getImage() == null) {
			return false;
		}
		if (!m1.getImage().isAttached() || !m2.getImage().isAttached()) {
			return false;
		}

		Box box1 = createHitbox(m1);
		Box box2 = createHitbox(m2);
		if (box1 == null || box2 == null) {
			return false;
		}

		return box1.left < box2.right && box1.right > box2.left && box1.top < box2.bottom && box1.bottom > box2.top;
	}

	private Box createHitbox(Movable movable) {
		int width = movable.getImage().getOffsetWidth();
		int height = movable.getImage().getOffsetHeight();
		if (width <= 0 || height <= 0) {
			return null;
		}

		int insetX;
		int insetY;
		String className = movable.getClass().getSimpleName();

		if ("Asteroid".equals(className)) {
			insetX = 55;
			insetY = 55;
		} else if ("SpaceForce".equals(className)) {
			insetX = 8;
			insetY = 8;
		} else {
			// Bullet (48x47) or other small movable
			insetX = 10;
			insetY = 10;
		}

		int left = movable.getImage().getAbsoluteLeft() + insetX;
		int top = movable.getImage().getAbsoluteTop() + insetY;
		int right = movable.getImage().getAbsoluteLeft() + width - insetX;
		int bottom = movable.getImage().getAbsoluteTop() + height - insetY;

		if (right <= left || bottom <= top) {
			return null;
		}

		return new Box(left, top, right, bottom);
	}

	private static class Box {
		final int left;
		final int top;
		final int right;
		final int bottom;

		Box(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	private int getHitboxInset(Movable m) {
		String className = m.getClass().getSimpleName();
		if ("Asteroid".equals(className)) {
			return 18;
		}
		if ("SpaceForce".equals(className)) {
			return 10;
		}
		return 4;
	}

}
