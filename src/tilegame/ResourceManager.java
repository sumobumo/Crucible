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
        	loadImage("characters/hero_Idle0.png"),
        	loadImage("characters/hero_Idle1.png"),
        	loadImage("characters/hero_Idle2.png"),
            loadImage("characters/hero_walk0.png"),
            loadImage("characters/hero_walk1.png"),
            loadImage("characters/hero_walk2.png"),
            loadImage("characters/hero_walk3.png"),
            loadImage("characters/hero_walk4.png"),
            loadImage("characters/hero_dodge0.png"),
            loadImage("characters/hero_dodge1.png"),
            loadImage("characters/hero_dodge2.png"),
            loadImage("characters/hero_dodge3.png"),
            loadImage("characters/hero_dodge4.png"),
            loadImage("characters/hero_dodge5.png"),
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
            loadImage("characters/char1_walk0.png"),
            loadImage("characters/char1_walk1.png"),
            loadImage("characters/char1_walk2.png"),
            loadImage("characters/char1_walk3.png"),
            loadImage("characters/char1_0.png"),
            loadImage("characters/char1_1.png"),
            loadImage("characters/char1_2.png"),
            loadImage("characters/char1_3.png"),
            loadImage("characters/char1_4.png"),
            loadImage("characters/char1_5.png"),
            loadImage("characters/char1_6.png"),
            loadImage("characters/char1_7.png"),
            loadImage("characters/char1_8.png"),
            loadImage("characters/char1_9.png"),
            loadImage("characters/char1_10.png"),
            loadImage("characters/char1_11.png"),
            loadImage("characters/char1_12.png"),
            loadImage("characters/char2_walk0.png"),
            loadImage("characters/char2_walk1.png"),
            loadImage("characters/char2_walk2.png"),
            loadImage("characters/char2_walk3.png"),
            loadImage("characters/char2_walk4.png"),
            loadImage("characters/char2_0.png"),
            loadImage("characters/char2_1.png"),
            loadImage("characters/char2_2.png"),
            loadImage("characters/char2_3.png"),
            loadImage("characters/char2_4.png"),
            loadImage("characters/char2_5.png"),
            loadImage("characters/char2_6.png"),
            loadImage("characters/char2_7.png"),
            loadImage("characters/char2_8.png"),
            loadImage("characters/char2_9.png"),
            loadImage("characters/char3_walk0.png"),
            loadImage("characters/char3_walk1.png"),
            loadImage("characters/char3_walk2.png"),
            loadImage("characters/char3_walk3.png"),
            loadImage("characters/char3_walk4.png"),
            loadImage("characters/char3_walk5.png"),
            loadImage("characters/char3_walk6.png"),
            loadImage("characters/char3_walk7.png"),
            loadImage("characters/char3_walk8.png"),
            loadImage("characters/char3_0.png"),
            loadImage("characters/char3_1.png"),
            loadImage("characters/char3_2.png"),
            loadImage("characters/char3_3.png"),
            loadImage("characters/char3_4.png"),
            loadImage("characters/char3_5.png"),
            loadImage("characters/char3_6.png"),
            loadImage("characters/char3_7.png"),
            loadImage("characters/char3_8.png"),
            loadImage("characters/char3_9.png"),
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
        Animation[] playerWalkAnim = new Animation[4];
        Animation[] playerIdleAnim = new Animation[4];
        Animation[] playerRollAnim = new Animation[4];
        Animation[] playerAttackAnim = new Animation[4];
        Animation[] greyKnightWalkAnim = new Animation[4];
        Animation[] greyKnightAttackAnim = new Animation[4];
        Animation[] greenKnightWalkAnim = new Animation[4];
        Animation[] greenKnightAttackAnim = new Animation[4];
        Animation[] staffKnightWalkAnim = new Animation[4];
        Animation[] staffKnightAttackAnim = new Animation[4];
        Animation[] femaleKnightWalkAnim = new Animation[4];
        Animation[] femaleKnightAttackAnim = new Animation[4];
        Animation[] bossKnightWalkAnim = new Animation[4];
        Animation[] bossKnightAttackAnim = new Animation[4];
        
        
        for (int i=0; i<4; i++) {
            
            playerIdleAnim[i] = createPlayerIdleAnim(images[i][0], images[i][1], images[i][2]);
            playerWalkAnim[i] = createPlayerWalkAnim(images[i][3], images[i][4], images[i][5], images[i][6], images[i][7]);
            playerRollAnim[i] =  createPlayerRollAnim(images[i][8], images[i][9], images[i][10], images[i][11], images[i][12], images[i][13]);
            playerAttackAnim[i] = createPlayerAttackAnim(images[i][14], images[i][15], images[i][16], images[i][17], images[i][18], images[i][19], images[i][20], images[i][21], images[i][22], images[i][23], images[i][24], images[i][25], images[i][26]);
            		
            staffKnightWalkAnim[i] = createStaffKnightWalkAnim(images[i][44], images[i][45], images[i][46], images[i][47], images[i][48]);
            staffKnightAttackAnim[i] = createStaffKnightAttackAnim(images[i][49], images[i][50], images[i][51], images[i][52], images[i][53], images[i][54], images[i][55], images[i][56], images[i][57], images[i][58]); 
            
            femaleKnightWalkAnim[i] = createFemaleKnightWalkAnim(images[i][27], images[i][28], images[i][29], images[i][30]);
            femaleKnightAttackAnim[i] = createFemaleKnightAttackAnim(images[i][31], images[i][32], images[i][33], images[i][34], images[i][35], images[i][36], images[i][37], images[i][38], images[i][39], images[i][40], images[i][41], images[i][42], images[i][43]);
            
            bossKnightWalkAnim[i] = createBossKnightWalkAnim(images[i][59], images[i][60], images[i][61], images[i][62], images[i][63], images[i][64], images[i][65], images[i][66], images[i][67]);
            bossKnightAttackAnim[i] = createBossKnightAttackAnim(images[i][68], images[i][69], images[i][70], images[i][71], images[i][72], images[i][73], images[i][74], images[i][75], images[i][76], images[i][77]);
                        
            greyKnightWalkAnim[i] = createGreyKnightWalkAnim(images[i][78], images[i][79], images[i][80]);
            greyKnightAttackAnim[i] = createGreyKnightAttackAnim(images[i][81], images[i][82], images[i][83], images[i][84], images[i][85]);
            
            greenKnightWalkAnim[i] = createGreenKnightWalkAnim(images[i][86], images[i][87], images[i][88]);
            greenKnightAttackAnim[i] = createGreenKnightAttackAnim(images[i][89], images[i][90]); 
        }

        // create creature sprites
        playerSprite = new Player(playerIdleAnim[0], playerIdleAnim[1], playerIdleAnim[2], playerIdleAnim[3], playerAttackAnim[0], playerAttackAnim[1]);
        staffKnightSprite = new StaffKnight(staffKnightWalkAnim[0], staffKnightWalkAnim[1], staffKnightWalkAnim[2], staffKnightWalkAnim[3], staffKnightAttackAnim[0], staffKnightAttackAnim[1]);
        femaleKnightSprite = new FemaleKnight(femaleKnightWalkAnim[0], femaleKnightWalkAnim[1], femaleKnightWalkAnim[2], femaleKnightWalkAnim[3], femaleKnightAttackAnim[0], femaleKnightAttackAnim[1]);
        bossKnightSprite = new Boss(bossKnightWalkAnim[0], bossKnightWalkAnim[1], bossKnightWalkAnim[2], bossKnightWalkAnim[3], bossKnightAttackAnim[0], bossKnightAttackAnim[1]);
        greyKnightSprite = new GreyKnight(greyKnightWalkAnim[0], greyKnightWalkAnim[1], greyKnightWalkAnim[2], greyKnightWalkAnim[3], greyKnightAttackAnim[0], greyKnightAttackAnim[1]);
        greenKnightSprite = new GreenKnight(greenKnightWalkAnim[0], greenKnightWalkAnim[1], greenKnightWalkAnim[2], greenKnightWalkAnim[3], greenKnightAttackAnim[0], greenKnightAttackAnim[1]);
        
        
    }


    private Animation createGreenKnightAttackAnim(Image image1, Image image2) {
		// TODO Auto-generated method stub
    	Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        return anim;
	}


	private Animation createGreenKnightWalkAnim(Image image1, Image image2, Image image3) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        return anim;
	}


	private Animation createGreyKnightAttackAnim(Image image1, Image image2, Image image3, Image image4, Image image5) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        return anim;
	}


	private Animation createGreyKnightWalkAnim(Image image1, Image image2, Image image3) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        return anim;
	}


	private Animation createBossKnightAttackAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6, Image image7, Image image8, Image image9, Image image10) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        anim.addFrame(image7, 100);
        anim.addFrame(image8, 100);
        anim.addFrame(image9, 100);
        anim.addFrame(image10, 100);
        return anim;
	}


	private Animation createBossKnightWalkAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6, Image image7, Image image8, Image image9) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        anim.addFrame(image7, 100);
        anim.addFrame(image8, 100);
        anim.addFrame(image9, 100);
        return anim;
	}


	private Animation createFemaleKnightAttackAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6, Image image7, Image image8, Image image9, Image image10, Image image11, Image image12,
			Image image13) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        anim.addFrame(image7, 100);
        anim.addFrame(image8, 100);
        anim.addFrame(image9, 100);
        anim.addFrame(image10, 100);
        anim.addFrame(image11, 100);
        anim.addFrame(image12, 100);
        anim.addFrame(image13, 100);
        return anim;
	}


	private Animation createFemaleKnightWalkAnim(Image image1, Image image2, Image image3, Image image4) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        return anim;
	}


	private Animation createStaffKnightAttackAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6, Image image7, Image image8, Image image9, Image image10) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        anim.addFrame(image7, 100);
        anim.addFrame(image8, 100);
        anim.addFrame(image9, 100);
        anim.addFrame(image10, 100);
        return anim;
	}


	private Animation createStaffKnightWalkAnim(Image image1, Image image2, Image image3, Image image4, Image image5) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        return anim;
	}


	private Animation createPlayerAttackAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6, Image image7, Image image8, Image image9, Image image10, Image image11, Image image12,
			Image image13) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        anim.addFrame(image7, 100);
        anim.addFrame(image8, 100);
        anim.addFrame(image9, 100);
        anim.addFrame(image10, 100);
        anim.addFrame(image11, 100);
        anim.addFrame(image12, 100);
        anim.addFrame(image13, 100);
        return anim;
	}


	private Animation createPlayerRollAnim(Image image1, Image image2, Image image3, Image image4, Image image5,
			Image image6) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 100);
        anim.addFrame(image2, 100);
        anim.addFrame(image3, 100);
        anim.addFrame(image4, 100);
        anim.addFrame(image5, 100);
        anim.addFrame(image6, 100);
        return anim;
	}
	
	private Animation createPlayerWalkAnim(Image player1,
	        Image player2, Image player3, Image player4, Image player5)
	    {
	        Animation anim = new Animation();
	        anim.addFrame(player1, 300);
	        anim.addFrame(player2, 300);
	        anim.addFrame(player3, 300);
	        anim.addFrame(player4, 300);
	        anim.addFrame(player5, 300);
	        return anim;
	    }


	private Animation createPlayerIdleAnim(Image image1, Image image2, Image image3) {
		// TODO Auto-generated method stub
		Animation anim = new Animation();
        anim.addFrame(image1, 400);
        anim.addFrame(image2, 400);
        anim.addFrame(image3, 400);
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
