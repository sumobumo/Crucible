package the.crucible.main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.input.GameAction;
import com.brackeen.javagamebook.input.InputManager;
import com.brackeen.javagamebook.sound.MidiPlayer;
import com.brackeen.javagamebook.sound.SoundManager;
import com.brackeen.javagamebook.test.GameCore;



import the.crucible.main.TileMap;
import the.crucible.main.TileMapRenderer;
import the.crucible.main.ResourceManager;
import the.crucible.sprites.Creature;
import the.crucible.sprites.Player;


public class GameManager extends GameCore{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
    private InputManager inputManager;
    private TileMapRenderer renderer;
    
    //game actions
	private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction attack;
	
	
    //for pausing game
   	private GameAction pause;
   	
   	
	public void init() {
        super.init();
        System.out.println("Magic");
        
        // set up input manager
        initInput();
        
        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());
        
     // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background/bg1.png"));
        
        // load first map
        map = resourceManager.loadNextMap();
        
        // load sounds
        //soundManager = new SoundManager(PLAYBACK_FORMAT);
        
        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("res/sounds/music.midi");
        midiPlayer.play(sequence, true);
        
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
        attack = new GameAction("attack");
        
        pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);
        
        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(attack, KeyEvent.VK_F);
        inputManager.mapToKey(pause, KeyEvent.VK_ESCAPE);
    }
	
	
	private void checkInput(long elapsedTime) {
		
		
	}
	
	
	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		 renderer.draw(g, map,
		            screen.getWidth(), screen.getHeight());
		 
	}
	
	/**
	    Gets the current map.
	*/
	public TileMap getMap() {
	    return map;
	}
	
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
	    return (s1x < s2x + s2.getWidth() &&
	        s2x < s1x + s1.getWidth() &&
	        s1y < s2y + s2.getHeight() &&
	        s2y < s1y + s1.getHeight());
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
	    if (player.getState() == Creature.STATE_DEAD) {
	        map = resourceManager.reloadMap();
	        return;
	    }
	
	    // get keyboard/mouse input
	    checkInput(elapsedTime);
	
//	    if(!paused){
	    	// update player
	        updateCreature(player, elapsedTime);
	        player.update(elapsedTime);
	
	        // update other sprites
	        Iterator i = map.getSprites();
	        while (i.hasNext()) {
	            Sprite sprite = (Sprite)i.next();
	            if (sprite instanceof Creature) {
	                Creature creature = (Creature)sprite;
	                if (creature.getState() == Creature.STATE_DEAD) {
	                    i.remove();
	                }
	                else {
	                    updateCreature(creature, elapsedTime);
	                }
	            }
	            // normal update
	            sprite.update(elapsedTime);
	        }
//	    }
    
	}
	
	 /**
    Updates the creature, applying gravity for creatures that
    aren't flying, and checks collisions.
	*/
	private void updateCreature(Creature creature,
	    long elapsedTime)
	{
	
	    // apply gravity
	    if (!creature.isFlying()) {
	        creature.setVelocityY(creature.getVelocityY() +
	            GRAVITY * elapsedTime);
	    }
	
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
	
	    // check for player collision with other sprites
	    Sprite collisionSprite = getSpriteCollision(player);
	    if (collisionSprite instanceof Creature) {
	        Creature badguy = (Creature)collisionSprite;
	        if (canKill) {
	            // kill the badguy and make player bounce
	            //soundManager.play(deadSound);
	            badguy.setState(Creature.STATE_DYING);
	            player.setY(badguy.getY() - player.getHeight());
	            //player.jump(true);
	        }
	        else {
	            // player dies!
	            player.setState(Creature.STATE_DYING);
	        }
	    }
	}

	
	
	
}




