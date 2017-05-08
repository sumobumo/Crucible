package sprites;

import graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class StaffKnight extends Creature {

    public StaffKnight(Animation left, Animation right, Animation deadLeft, Animation deadRight, Animation attackLeft, Animation attackRight)
    {
    	super(left, right, deadRight, deadRight, attackLeft, attackRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
