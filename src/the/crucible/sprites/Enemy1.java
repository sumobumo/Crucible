package the.crucible.sprites;

import com.brackeen.javagamebook.graphics.Animation;

public class Enemy1 extends Creature{

	public Enemy1(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		// TODO Auto-generated constructor stub
	}

	public float getMaxSpeed() {
        return 0.2f;
    }
	
}
