package sprites;

import graphics.Animation;

/**
 * The Player.
 */
public class Player extends Creature {

	private static final float JUMP_SPEED = -.95f;
	private static final float ACCELERATION = .006f;
	private static final float STOP_MODIFIER = 3f;
	private static final float SLOW = .003f;

	private boolean onGround;

	public Player(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
	}

	public void collideHorizontal() {
		setVelocityX(0);
	}

	@Override
	public void slow() {
		if (getVelocityX() > 0) {
			float x = getVelocityX() - SLOW;
			if (x < 0)
				x = 0;
			setVelocityX(x);
		}
		if (getVelocityX() < 0) {
			float x = getVelocityX() + SLOW;
			if (x > 0)
				x = 0;
			setVelocityX(x);
		}
	}

	public void collideVertical() {
		// check if collided with ground
		if (getVelocityY() > 0) {
			onGround = true;
		}
		setVelocityY(0);
	}

	public void setY(float y) {
		// check if falling
		if (Math.round(y) > Math.round(getY())) {
			onGround = false;
		}
		super.setY(y);
	}

	public void wakeUp() {
		// do nothing
	}

	/**
	 * Makes the player jump if the player is on the ground or if forceJump is
	 * true.
	 */
	public void jump(boolean forceJump) {
		if (onGround || forceJump) {
			onGround = false;
			setVelocityY(JUMP_SPEED);
		}
	}

	public void moveLeft() {
		if (!isAlive() || isStunned())
			return;
		if (getVelocityX() > 0) {
			float diff = getVelocityX() - ACCELERATION * STOP_MODIFIER;
			if (diff > 0) {
				setVelocityX(diff);
			} else {
				setVelocityX(diff / STOP_MODIFIER);
			}

		} else {
			float x = getVelocityX() - ACCELERATION;
			if (x < -maxSpeed)
				x = -maxSpeed;
			setVelocityX(x);
		}
	}

	public void moveRight() {
		if (!isAlive() || isStunned())
			return;
		if (getVelocityX() < 0) {
			float diff = getVelocityX() + ACCELERATION * STOP_MODIFIER;
			if (diff < 0) {
				setVelocityX(diff);
			} else {
				setVelocityX(diff / STOP_MODIFIER);
			}

		} else {
			float x = getVelocityX() + ACCELERATION;
			if (x > maxSpeed)
				x = maxSpeed;
			setVelocityX(x);
		}
	}

}
