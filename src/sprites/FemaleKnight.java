package sprites;

import graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class FemaleKnight extends Creature {

    public FemaleKnight(Animation left, Animation right, Animation deadLeft, Animation deadRight, Animation attackLeft, Animation attackRight)
    {
    	super(left, right, deadRight, deadRight, attackLeft, attackRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
