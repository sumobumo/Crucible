package sprites;

import graphics.Animation;

/**
 * The Player.
 */
public class Player extends Creature {

	
	

	private boolean onGround;

	public Player(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
	}

	public void collideHorizontal() {
		setVelocityX(0);
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

}
