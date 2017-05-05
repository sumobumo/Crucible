package sprites;

import java.lang.reflect.Constructor;

import graphics.*;

/**
 * A Creature is a Sprite that is affected by gravity and can die. It has four
 * Animations: moving left, moving right, dying on the left, and dying on the
 * right.
 */
public abstract class Creature extends Sprite {

	/**
	 * Amount of time to go from STATE_DYING to STATE_DEAD.
	 */
	private static final int DIE_TIME = 1000;

	public static final int STATE_NORMAL = 1;
	public static final int STATE_DYING = 0;
	public static final int STATE_DEAD = -1;

	private Animation left;
	private Animation right;
	private Animation deadLeft;
	private Animation deadRight;
	private int health;
	private long stateTime;
	private int stun;

	// private int health;
	// private int attackDamage;
	// private boolean state; //walk or attack

	/**
	 * Creates a new Creature with the specified Animations.
	 */
	public Creature(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(right);
		this.left = left;
		this.right = right;
		this.deadLeft = deadLeft;
		this.deadRight = deadRight;
		health = STATE_NORMAL;
		stun = 0;
	}

	public Object clone() {
		// use reflection to create the correct subclass
		Constructor constructor = getClass().getConstructors()[0];
		try {
			return constructor.newInstance(new Object[] { (Animation) left.clone(), (Animation) right.clone(),
					(Animation) deadLeft.clone(), (Animation) deadRight.clone() });
		} catch (Exception ex) {
			// should never happen
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the maximum speed of this Creature.
	 */
	public float getMaxSpeed() {
		return 0;
	}

	/**
	 * Wakes up the creature when the Creature first appears on screen.
	 * Normally, the creature starts moving left.
	 */
	public void wakeUp() {
		if (getHealth() >= STATE_NORMAL && getVelocityX() == 0) {
			setVelocityX(-getMaxSpeed());
		}
	}

	/**
	 * Gets the state of this Creature. The state is either STATE_NORMAL,
	 * STATE_DYING, or STATE_DEAD.
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Sets the state of this Creature to STATE_NORMAL, STATE_DYING, or
	 * STATE_DEAD.
	 */
	public void setHealth(int health) {
		if (this.health != health) {
			this.health = health;
			stateTime = 0;
			if (health == STATE_DYING) {
				setVelocityX(0);
				setVelocityY(0);
			}
		}
	}

	/**
	 * Checks if this creature is alive.
	 */
	public boolean isAlive() {
		return (health >= STATE_NORMAL);
	}

	public boolean isStunned() {
		return (stun > 0);
	}

	/**
	 * Checks if this creature is flying.
	 */
	public boolean isFlying() {
		return false;
	}

	/**
	 * Called before update() if the creature collided with a tile horizontally.
	 */
	public void collideHorizontal() {
		setVelocityX(-getVelocityX());
	}

	/**
	 * Called before update() if the creature collided with a tile vertically.
	 */
	public void collideVertical() {
		setVelocityY(0);
	}

	/**
	 * Updates the animaton for this creature.
	 */
	public void update(long elapsedTime) {
		// select the correct Animation
		Animation newAnim = anim;
		if (getVelocityX() < 0) {
			newAnim = left;
		} else if (getVelocityX() > 0) {
			newAnim = right;
		}
		if (health == STATE_DYING && newAnim == left) {
			newAnim = deadLeft;
		} else if (health == STATE_DYING && newAnim == right) {
			newAnim = deadRight;
		}

		// update the Animation
		if (anim != newAnim) {
			anim = newAnim;
			anim.start();
		} else {
			anim.update(elapsedTime);
		}

		// update to "dead" state
		stateTime += elapsedTime;
		if (health == STATE_DYING && stateTime >= DIE_TIME) {
			setHealth(STATE_DEAD);
		}
		slow();
	}

	public void slow() {
		return;

	}

}
