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
	private static final int ROLL_TIME = 1000;
	private static final int INVULN_UPPER_THRESHOLD = 800;
	private static final int INVULN_LOWER_THRESHOLD = 300;
	private static final float ROLL_SPEED = .6f;
	private static final float ROLL_SLOW = .003f;
	private static final float SLOW = .002f;
	private static final float TIME_MODIFIER = .3f;
	private static final float ACCELERATION = SLOW+.001f;
	private static final float STOP_MODIFIER = 1.5f;

	private Animation left;
	private Animation right;
	private Animation deadLeft;
	private Animation deadRight;
	private int health;
	private long stateTime;
	private int stun;
	private int roll;
	private int beginRoll=0;

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
		roll = 0;
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
	
	public boolean isInvulnerable(){
		return (roll > INVULN_LOWER_THRESHOLD && roll < INVULN_UPPER_THRESHOLD);
	}
	
	public boolean isRolling(){
		return (roll > 0);
	}
	
	public void beginRoll(int direction){
		if (!isRolling()){
			capSpeed(.1f);
			roll = ROLL_TIME;
			beginRoll=direction;
		}
	}
	
	private void capSpeed(float speed) {
		if (dx>speed){
			dx=speed;
		}
		else if (dx<-speed){
			dx=-speed;
		}
	}

	private void roll(int direction){
		
		setVelocityX(direction*ROLL_SPEED);
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
	 * Updates the animation for this creature.
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
		// slow character
		slow(elapsedTime);
		// count down stun and invuln
		if (stun>0){
			stun-=elapsedTime;
		}
		if (roll>0){
			if (roll>INVULN_UPPER_THRESHOLD){
				//TODO: animate pre-roll
			}
			else if (roll>INVULN_LOWER_THRESHOLD){
				//TODO: animate roll
				if (beginRoll!=0){
					roll(beginRoll);
					beginRoll=0;
				}
			}
			else{
				//TODO: animate post-roll
			}
			
			roll-=elapsedTime;
		}
	}

	public void slow(long elapsedTime) {
		float slow;
		if (isRolling()){
			slow = ROLL_SLOW;
		}
		else{
			slow = SLOW;
		}
		if (getVelocityX() > 0) {
			float x = getVelocityX() - slow*elapsedTime*TIME_MODIFIER;
			if (x < 0)
				x = 0;
			setVelocityX(x);
		}
		if (getVelocityX() < 0) {
			float x = getVelocityX() + slow*elapsedTime*TIME_MODIFIER;
			if (x > 0)
				x = 0;
			setVelocityX(x);
		}
	}
	
	public void moveLeft() {
		if (!isAlive() || isStunned() || isRolling())
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
		if (!isAlive() || isStunned() || isRolling())
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
