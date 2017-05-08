package sprites;

import graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class GreenKnight extends Creature {

    public GreenKnight(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }
    
    public int[] attackColors(){
		return null;//TODO: grey
	}

}

