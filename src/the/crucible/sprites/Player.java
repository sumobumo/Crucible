package the.crucible.sprites;

import com.brackeen.javagamebook.graphics.Animation;

public class Player extends Creature {

	 public Player(Animation left, Animation right,
		        Animation deadLeft, Animation deadRight)
		    {
		        super(left, right, deadLeft, deadRight);
		    }
	 
	 public void setY(float y) {
		 super.setY(y);
	 }
	
	
	 public void wakeUp() {
		 // do nothing
	 }
	 
	  public float getMaxSpeed() {
		  return 0.5f;
	  }
	    
}
