package tilegame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.PixelGrabber;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import graphics.*;
import input.*;
import sound.*;
import sprites.*;
import test.GameCore;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

  

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction roll;
    private GameAction attack;

    //for pausing game
	private GameAction pause;
	private boolean paused;
	private JPanel pauseMenu;


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load first map
        map = resourceManager.loadNextMap();
        
        // load resources
        renderer = new TileMapRenderer();
        
//        renderer.setBackground(
//            resourceManager.loadImage("background/background1.png"),
//            resourceManager.loadImage("background/background_mid1.png"),
//            resourceManager.loadImage("background/background_front1.png"));
        Image[] background = map.getBackgrounds();
        renderer.setBackground(background[0], background[1], background[2]);
        		
 
        
        
        //Intro movie
        
        
        //instruction screen before game starts
        JFrame frame = screen.getFullScreenWindow();
        JOptionPane.showMessageDialog(frame, "The Crucuble \n Instructions: \n 1. Kill all the enemy to proceed to next level. \n 2. Use left and right arrow to move and space to attack.\n 3.Press Esc to pause. ");
        

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("res/sounds/clang10.wav");
        boopSound = soundManager.getSound("res/sounds/beep.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("res/sounds/music.midi");
        midiPlayer.play(sequence, true);
        //toggleDrumPlayback();
        
        
        pauseMenu = new JPanel();
        JButton resume = new JButton("resume");
        resume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//paused = !paused;
				pause.press();
				
			}
		});
        
     
        
        JButton exit = new JButton("exit");
        exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				stop();
				
			}
		});
        
        //create pause menu
        Border border= BorderFactory.createLineBorder(Color.black);
        
        pauseMenu.add(resume);
        pauseMenu.add(exit);
        
        pauseMenu.setBorder(border);
        pauseMenu.setVisible(false);
        pauseMenu.setSize(pauseMenu.getPreferredSize());
        
        //center pause menu
        //pauseMenu.setLocation((screen.getWidth() - pauseMenu.getWidth())/2, (screen.getHeight() - pauseMenu.getHeight())/2);
        
        screen.getFullScreenWindow().getLayeredPane().add(pauseMenu, JLayeredPane.MODAL_LAYER);
    }


    /**
        Closes any resources used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        roll = new GameAction("jump", GameAction.DETECT_INITAL_PRESS_ONLY);
        attack = new GameAction("attack", GameAction.DETECT_INITAL_PRESS_ONLY);

        pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);
        
        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(roll, KeyEvent.VK_D);
        inputManager.mapToKey(attack, KeyEvent.VK_F);
        inputManager.mapToKey(pause, KeyEvent.VK_ESCAPE);
    }


    private void checkInput(long elapsedTime) {

//        if (exit.isPressed()) {
//            stop();
//        }

    	if(pause.isPressed()){
    		paused = !paused;
    		//inputManager.resetAllGameActions();
    		pauseMenu.setVisible(paused);
            
    	}
    	
        Player player = (Player)map.getPlayer();
//        if (player.isAlive()) {
//            float velocityX = 0;
//            if (moveLeft.isPressed()) {
//                velocityX-=player.getMaxSpeed();
//            }
//            if (moveRight.isPressed()) {
//                velocityX+=player.getMaxSpeed();
//            }
//            if (roll.isPressed()) {
//                player.jump(false);
//            }
//            player.setVelocityX(velocityX);
//        }
    	if (moveLeft.isPressed() && !moveRight.isPressed()) {
			player.moveStart(-1);
		}
		if (moveRight.isPressed() && !moveLeft.isPressed()) {
			player.moveStart(1);
		}
		if(attack.isPressed()){
			if(moveRight.isPressed()){
				player.attack(1);
			}
			if(moveLeft.isPressed()){
				player.attack(-1);
			}
		}
		if (roll.isPressed()) {
			if(moveRight.isPressed()){
				player.beginRoll(1);
			}
			if(moveLeft.isPressed()){
				player.beginRoll(-1);
			}
		}

    }


    public void draw(Graphics2D g) {
        renderer.draw(g, map,
            screen.getWidth(), screen.getHeight());
        if(paused){
        	pauseMenu.paint(g);
        }
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
//    public void toggleDrumPlayback() {
//        Sequencer sequencer = midiPlayer.getSequencer();
//        if (sequencer != null) {
//            sequencer.setTrackMute(DRUM_TRACK,
//                !sequencer.getTrackMute(DRUM_TRACK));
//        }
//    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }

    
    /*
	 * pixelPerfectCollision(); first determines the area where the sprites
	 * collides AKA the collision-rectangle. It then grabs the pixels from both
	 * sprites which are inside the rectangle. It then checks every pixel from
	 * the arrays given by grabPixels();, and if 2 pixels at the same position
	 * are opaque, (alpha value over 0) it will return true. Otherwise it will
	 * return false.
	 */
	private boolean pixelPerfectCollision(Sprite s1, Sprite s2) {
		/*
		 * Get the X-values and Y-values for the two coordinates where the
		 * sprites collide
		 */
		int left1 = (int) s1.getX();
		int right1 = left1 + s1.getWidth();
		int top1 = (int) s1.getY();
		int bottom1 = top1 + s1.getHeight();

		int left2 = (int) s2.getX();
		int right2 = left2 + s2.getWidth();
		int top2 = (int) s2.getY();
		int bottom2 = top2 + s2.getHeight();

		int leastleft = (left1 > left2) ? left1 : left2;
		int leastright = (right1 < right2) ? right1 : right2;
		int leasttop = (top1 > top2) ? top1 : top2;
		int leastbottom = (bottom1 < bottom2) ? bottom1 : bottom2;

		// Determine the width and height of the collision rectangle
		int width = leastright - leastleft;
		int height = leastbottom - leasttop;

		// Create arrays to hold the pixels
		int[] pixels1 = new int[width * height];
		int[] pixels2 = new int[width * height];

		// Create the pixelgrabber and fill the arrays
		PixelGrabber pg1 = new PixelGrabber(s1.getImage(), leastleft - left1, leasttop - top1, width, height, pixels1, 0, width);
		PixelGrabber pg2 = new PixelGrabber(s2.getImage(), leastleft - left2, leasttop - top2, width, height, pixels2, 0, width);

		// Grab the pixels
		try {
			pg1.grabPixels();
			pg2.grabPixels();
		} catch (InterruptedException ex) {
			// Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null,
			// ex);
		}

		// Check if pixels at the same spot from both arrays are not
		// transparent.
		for (int i = 0; i < pixels1.length; i++) {
			int a = (pixels1[i] >>> 24) & 0xff;
			int a2 = (pixels2[i] >>> 24) & 0xff;

			/*
			 * Awesome, we found two pixels in the same spot that aren't
			 * completely transparent! Thus the sprites are colliding!
			 */
			if (a > 0 && a2 > 0)
				return true;

		}

		return false;
	}
	
	

    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
//        return (s1x < s2x + s2.getWidth() &&
//            s2x < s1x + s1.getWidth() &&
//            s1y < s2y + s2.getHeight() &&
//            s2y < s1y + s1.getHeight());
        
    	if (s1x < s2x + s2.getWidth() && s2x < s1x + s1.getWidth() && s1y < s2y + s2.getHeight()
				&& s2y < s1y + s1.getHeight()){
			return pixelPerfectCollision(s1,s2);
		}
		else{
			return false;
		}
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();


        // player is dead! start map over
        if (player.getHealth() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }
        

        // get keyboard/mouse input
        checkInput(elapsedTime);

        if(!paused){
        	// update player
            updateCreature(player, elapsedTime);
            player.update(elapsedTime);

            // update other sprites
            Iterator i = map.getSprites();
            while (i.hasNext()) {
                Sprite sprite = (Sprite)i.next();
                if (sprite instanceof Creature) {
                    Creature creature = (Creature)sprite;
                    if (creature.getHealth() == Creature.STATE_DEAD) {
                        i.remove();
                    }
                    else {
                        updateCreature(creature, elapsedTime);
                    }
                }
                // normal update
                sprite.update(elapsedTime);
            }
        }
        
    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        //if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        //}

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        //TODO: Attack and Invulnerability Frames
        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof BackgroundSprites) {
            acquirePowerUp((BackgroundSprites)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setHealth(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                //player.jump(true);
            }
            else if (!player.isInvulnerable()){
                // player dies!
                player.setHealth(Creature.STATE_DYING);
            }
        }
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(BackgroundSprites powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof BackgroundSprites.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
            Image[] background = map.getBackgrounds();
            renderer.setBackground(background[0], background[1], background[2]);
        }
    }

}
