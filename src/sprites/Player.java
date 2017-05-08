package sprites;

import graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    //private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    
    static Animation rollLeft;
    static Animation rollRight;
    static Animation walkLeft;
    static Animation walkRight;

    public Player(Animation left, Animation right, Animation deadLeft, Animation deadRight, Animation attackLeft, Animation attackRight)
    {
        super(left, right, deadRight, deadRight, attackLeft, attackRight);
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

    
    public void setWalkLeft(Animation walkLeft){
    	this.walkLeft = walkLeft;
    }
    
    public static Animation getWalkLeft(){
    	return walkLeft;
    }
    
    public void setWalkRight(Animation walkRight){
    	this.walkRight = walkRight;
    }
    
    public static Animation getWalkRight(){
    	return walkRight;
    }
    
    public void setRollLeft(Animation rollLeft){
    	this.rollLeft = rollLeft;
    }
    
    public static Animation getRollLeft(){
    	return rollLeft;
    }

    public void setRollRight(Animation rollRight){
    	this.rollRight = rollRight;
    }
    public static Animation getRollRight(){
    	return rollRight;
    }


	public void attack(int i) {
		// TODO Auto-generated method stub
		
	}

    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
//    public void jump(boolean forceJump) {
//        if (onGround || forceJump) {
//            onGround = false;
//            setVelocityY(JUMP_SPEED);
//        }
//    }
//
//
//    public float getMaxSpeed() {
//        return 0.3f;
//    }

}
