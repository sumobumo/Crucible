package sprites;

import java.lang.reflect.Constructor;

import graphics.*;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class BackgroundSprites extends Sprite {

    public BackgroundSprites(Animation anim) {
        super(anim);
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


//    /**
//        A Star PowerUp. Gives the player points.
//    */
//    public static class Wall extends BackgroundSprites {
//        public Wall(Animation anim) {
//            super(anim);
//        }
//    }
//
//
//    /**
//        A Music PowerUp. Changes the game music.
//    */
//    public static class Roof extends BackgroundSprites {
//        public Roof(Animation anim) {
//            super(anim);
//        }
//    }


    /**
        A Goal PowerUp. Advances to the next map.
    */
    public static class Goal extends BackgroundSprites {
        public Goal(Animation anim) {
            super(anim);
        }
    }

}
