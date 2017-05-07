package tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import graphics.*;
import sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private Image tile;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite portalSprite;
    
    private Sprite greenKnightSprite;
    private Sprite greyKnightSprite;
    private Sprite staffKnightSprite;
    private Sprite femaleKnightSprite;
    private Sprite bossKnightSprite;
    


    

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadGameBackgroundSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "res/" + name;
        //ClassLoader cl = this.getClass().getClassLoader();
        //return new ImageIcon(cl.getClass().getResource(filename)).getImage();
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getDeadImage(Image image, int x) {
    	
        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(x * 90), image.getWidth(null)/2, image.getHeight(null)/2);
        //transform.translate(
        //    image.getWidth(null) / 2,
        //    image.getHeight(null) / 2);

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
    	String background="background/";
        String background_mid="background/";
    	String background_front="background/";
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
            	//front background image
            	if(line.startsWith("+")){
            		background_front += line.substring(1);
            	}
            	//mid background image
            	else if(line.startsWith("-")){
            		background_mid += line.substring(1);
            	}
            	//back background image
            	else if(line.startsWith("/")){
            		background += line.substring(1);
            	}
            	else{
            		lines.add(line);
            		width = Math.max(width, line.length());
            	}
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                                
                if (ch == 'X') {
                    newMap.setTile(x, y, tile);
                }
                else if (ch == '@') {
                    addSprite(newMap, portalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, greenKnightSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, greyKnightSprite, x, y);
                }
                else if (ch == '3') {
                	addSprite(newMap, femaleKnightSprite, x, y);
                }
                else if (ch == '4') {
                	addSprite(newMap, staffKnightSprite, x, y);
                }
                else if (ch == '5') {
                	addSprite(newMap, bossKnightSprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);
        newMap.setBackgrounds(loadImage(background), loadImage(background_mid), loadImage(background_front));
        return newMap;
    }


    //required for goal sprite only
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
        
    	
        
        String name = "background/floor.png";
        File file = new File("res/" + name);
        tile = loadImage(name);
        
    
        
// 	TODO if we want to have new floor for different levels we should implement this as tile A,B,C
//        
//        char ch = 'A';
//        while (true) {
//            String name = "background/tile_" + ch + ".png";
//            File file = new File("res/" + name);
//            if (!file.exists()) {
//                break;
//            }
//            tiles.add(loadImage(name));
//            ch++;
//        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("characters/hero_0.png"),
            loadImage("characters/hero_1.png"),
            loadImage("characters/hero_3.png"),
            loadImage("characters/enemy1_walk1.png"),
            loadImage("characters/enemy1_walk2.png"),
            loadImage("characters/enemy1_walk3.png"),
            loadImage("characters/enemy2_attack1.png"),
            loadImage("characters/enemy2_attack2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getDeadImage(images[0][i], 1);
            // right-facing "dead" images
            images[3][i] = getDeadImage(images[1][i], -1);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] greyKnightAnim = new Animation[4];
        Animation[] greenKnightAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            greyKnightAnim[i] = createGreyKnightAnim(
                images[i][3], images[i][4], images[i][5]);
            greenKnightAnim[i] = createGreenKinghtAnim(
                images[i][6], images[i][7]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        greyKnightSprite = new GreyKnight(greyKnightAnim[0], greyKnightAnim[1],
            greyKnightAnim[2], greyKnightAnim[3]);
        greenKnightSprite = new GreenKnight(greenKnightAnim[0], greenKnightAnim[1],
            greenKnightAnim[2], greenKnightAnim[3]);
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3)
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


    private Animation createGreyKnightAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGreenKinghtAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }

    

    //required for goal sprite only
    private void loadGameBackgroundSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("background/portal1.png"), 150);
        anim.addFrame(loadImage("background/portal2.png"), 150);
        anim.addFrame(loadImage("background/portal1.png"), 150);
        anim.addFrame(loadImage("background/portal3.png"), 150);
        portalSprite = new BackgroundSprites.Goal(anim);
    }

}
