package the.crucible.sprites;

import com.brackeen.javagamebook.graphics.Animation;

public class Enemy2 extends Creature{

	public Enemy2(Animation left, Animation right, Animation deadLeft, Animation deadRight) {
		super(left, right, deadLeft, deadRight);
		// TODO Auto-generated constructor stub
	}
	
	 public float getMaxSpeed() {
	        return 0.05f;
	    }
}
