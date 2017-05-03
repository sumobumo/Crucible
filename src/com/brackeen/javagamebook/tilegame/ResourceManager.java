package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;


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
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    
    private Image wall;
    private Image roof;
    private Image litWindow;
    private Image unlitWindow;
    private Image door_bl;
    private Image door_br;
    private Image door_l;
    private Image door_r;
    private Image door_tl;
    private Image door_tr;

    

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

                                
                if (ch == 'X') {
                    newMap.setTile(x, y, tile);
                }

                // check if the char represents a sprite
                else if (ch == '*') {
                    //addSprite(newMap, wallSprite, x, y);
                	wall = loadImage("res/background/wall.png");
                	//newMap.setTile(x, y, wall);
                }
                else if (ch == '-') {
                    //addSprite(newMap, roofSprite, x, y);
                	roof = loadImage("res/background/roof.png");
                	//newMap.setTile(x, y, roof);
                }
                else if (ch == 'o') {
                    //addSprite(newMap, unlitWindowSprite, x, y);
                	unlitWindow = loadImage("res/background/window.png");
                	//newMap.setTile(x, y, unlitWindow);
                }
                else if (ch == 'O') {
                    //addSprite(newMap, litWindowSprite, x, y);
                	litWindow = loadImage("res/background/window_lit.png");
                	//newMap.setTile(x, y, litWindow);
                }
                else if (ch == 'A') {
                    //addSprite(newMap, door_blSprite, x, y);
                	door_bl = loadImage("res/background/door_bl.png");
                	//newMap.setTile(x, y, door_bl);
                }
                else if (ch == 'B') {
                    //addSprite(newMap, door_brSprite, x, y);
                	door_br = loadImage("res/background/door_br.png");
                	//newMap.setTile(x, y, door_br);
                } 
                else if (ch == 'C') {
                    //addSprite(newMap, door_lSprite, x, y);
                	door_l = loadImage("res/background/door_l.png");
                	//newMap.setTile(x, y, door_l);
                }
                else if (ch == 'D') {
                    //addSprite(newMap, door_rSprite, x, y);
                	door_r = loadImage("res/background/door_r.png");
                	//newMap.setTile(x, y, door_r);
                } 
                else if (ch == 'E') {
                    //addSprite(newMap, door_tlSprite, x, y);
                	door_tl = loadImage("res/background/door_tl.png");
                	//newMap.setTile(x, y, door_tl);
                }
                else if (ch == 'F') {
                    //addSprite(newMap, door_trSprite, x, y);
                	door_tr = loadImage("res/background/door_tr.png");
                	//newMap.setTile(x, y, door_tr);
                }
                else if (ch == '@') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
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
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
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


    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }

    

    //required for goal sprite only
    private void loadGameBackgroundSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("../images/heart1.png"), 150);
        anim.addFrame(loadImage("../images/heart2.png"), 150);
        anim.addFrame(loadImage("../images/heart3.png"), 150);
        anim.addFrame(loadImage("../images/heart2.png"), 150);
        goalSprite = new BackgroundSprites.Goal(anim);

//        // create "star" sprite
//        anim = new Animation();
//        anim.addFrame(loadImage("../images/star1.png"), 100);
//        anim.addFrame(loadImage("../images/star2.png"), 100);
//        anim.addFrame(loadImage("../images/star3.png"), 100);
//        anim.addFrame(loadImage("../images/star4.png"), 100);
//        coinSprite = new BackgroundSprites.Wall(anim);
//
//        // create "music" sprite
//        anim = new Animation();
//        anim.addFrame(loadImage("../images/music1.png"), 150);
//        anim.addFrame(loadImage("../images/music2.png"), 150);
//        anim.addFrame(loadImage("../images/music3.png"), 150);
//        anim.addFrame(loadImage("../images/music2.png"), 150);
//        musicSprite = new BackgroundSprites.Roof(anim);
    }

}
