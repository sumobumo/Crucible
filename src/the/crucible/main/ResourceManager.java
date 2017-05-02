package the.crucible.main;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.Animation;
import com.brackeen.javagamebook.graphics.Sprite;


import the.crucible.main.TileMap;
import the.crucible.main.TileMapRenderer;
import the.crucible.sprites.Enemy1;
import the.crucible.sprites.Enemy2;
import the.crucible.sprites.Player;

public class ResourceManager {

	private ArrayList<Image> tiles;
	private int currentMap;
	private GraphicsConfiguration gc;
	
	// host sprites used for cloning
    private Sprite playerSprite;
    private Sprite enemy1Sprite;
    private Sprite enemy2Sprite;
    private Sprite goalSprite;
	 
	public ResourceManager(GraphicsConfiguration gc) {
		// TODO Auto-generated constructor stub
		 this.gc = gc;
		 loadTileImages();
		 loadCreatureSprites();
	}
	
	 /**
    	Gets an image from the res/ directory.
	  */
	public Image loadImage(String name) {
		String filename = "res/" + name;
        return new ImageIcon(filename).getImage();
	}


   public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }
    
    
	public TileMap loadNextMap() {
		TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
	}
	
	public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
	 private TileMap loadMap(String filename)
		        throws IOException
    {
        ArrayList<String> lines = new ArrayList<String>();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }

	 
	 private void addSprite(TileMap map,
		        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }
	 
	 
	// -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList<Image>();
        char ch = 'A';
        while (true) {
            String name = "floor.png";
        	//File file = new File("images/" + name);
            //only one kind of tile for floor. So putting it directly
        	File file = new File("res/background/"+name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("characters/hero_0.png"),
            loadImage("characters/hero_1.png"),
            loadImage("characters/hero_2.png"),
            loadImage("characters/hero_3.png"),
            loadImage("characters/hero_4.png"),
            loadImage("characters/hero_5.png"),
            loadImage("characters/hero_6.png"),
            loadImage("characters/hero_7.png"),
            loadImage("characters/hero_8.png"),
            loadImage("characters/hero_9.png"),
            loadImage("characters/hero_10.png"),
            loadImage("characters/hero_11.png"),
            loadImage("characters/hero_12.png"),
            loadImage("characters/enemy1_walk1.png"),
            loadImage("characters/enemy1_walk2.png"),
            loadImage("characters/enemy1_walk3.png"),
            loadImage("characters/enemy1_attack1.png"),
            loadImage("characters/enemy1_attack2.png"),
            loadImage("characters/enemy1_attack3.png"),
            loadImage("characters/enemy1_attack4.png"),
            loadImage("characters/enemy1_attack5.png"),
            loadImage("characters/enemy2_walk1.png"),
            loadImage("characters/enemy2_walk2.png"),
            loadImage("characters/enemy2_walk3.png"),
            loadImage("characters/enemy2_attack1.png"),
            loadImage("characters/enemy2_attack2.png")
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] enemy1Anim = new Animation[4];
        Animation[] enemy2Anim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2], images[i][3], images[i][4], images[i][5], images[i][6], images[i][7], images[i][8], images[i][9], images[i][10], images[i][11], images[i][12]);
            enemy1Anim[i] = createEnemy1Anim(
                images[i][13], images[i][14], images[i][15], images[i][16], images[i][17], images[i][18], images[i][19], images[i][20]);
            enemy2Anim[i] = createEnemy2Anim(
                images[i][21], images[i][22], images[i][23], images[i][24], images[i][25]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        enemy1Sprite = new Enemy1(enemy1Anim[0], enemy1Anim[1],
        		enemy1Anim[2], enemy1Anim[3]);
        enemy2Sprite = new Enemy2(enemy2Anim[0], enemy2Anim[1],
        		enemy2Anim[2], enemy2Anim[3]);
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4, Image player5, Image player6, Image player7, Image player8, Image player9, Image player10, Image player11, Image player12, Image player13)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 200);
        anim.addFrame(player2, 150);
        return anim;
    }

    
    // TODO  will need to fo this way. Implement later
    private Animation createAnim(ArrayList<Image> playerImgs, ArrayList<Integer> duration){
    	boolean frameduration_defined = true;
    	int default_duration = 100;
    	if(duration == null || duration.size()==0 || playerImgs.size()!=duration.size()){
    		frameduration_defined = false;
    	}
    	Animation anim = new Animation();
    	for(int i=0; i< playerImgs.size(); i++){
    		anim.addFrame(playerImgs.get(i), frameduration_defined ? duration.get(i) : default_duration);
    	}
    	return anim;
    }

    private Animation createEnemy1Anim(Image img1, Image img2,
        Image img3, Image img4, Image img5, Image img6, Image img7, Image img8)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createEnemy2Anim(Image img1, Image img2, Image img3, Image img4, Image img5) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }

	

}
