package main;
/**
 * GamePanel extends Jpanel. Contains the main game loop.
 */

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import animation.SpriteListener;
import sound.MidiPlayer;
import sound.specific.JadeSoundManager10512Hz;
import sound.specific.JadeSoundManager22050Hz;
import tile.GameTile;
import tile.TileMap;
import objects.base.Creature;
import objects.creatures.Coin;
import objects.Jade.Jade;

public class GamePanel extends JPanel implements Runnable {

	private int panelWidth;
	private int panelHeight;
	private Graphics dbg;
	private Image dbImage = null;

	private boolean running = false; 
	private boolean gameOver = false;
	
	private Thread animator;
	private int period = 20; 
	
	private Jade Jade;
	private TileMap map;
	private TileMap backgroundMap;
	private TileMap foregroundMap;
	private GameRenderer renderer;
	private GameLoader manager;
	
	
	private MidiPlayer player;
	private JadeSoundManager22050Hz SM_22050_Hz;
	private JadeSoundManager10512Hz SM_10512_Hz;
	
	public GamePanel(int w, int h) {
		
		this.panelWidth = w;
		this.panelHeight = h;
		
		SM_22050_Hz = new JadeSoundManager22050Hz(new AudioFormat(22050, 8, 1, true, true));
		SM_10512_Hz = new JadeSoundManager10512Hz(new AudioFormat(10512, 8, 1, true, true));
 		Jade = new Jade(SM_22050_Hz);
		
		try {
			manager = new GameLoader();
			renderer = new GameRenderer();
			renderer.setBackground(ImageIO.read(new File("backgrounds/background2.png")));
			renderer.setForeground(ImageIO.read(new File("backgrounds/trees.png")));
			map = manager.loadMap("maps/map2.txt", SM_22050_Hz); // use the ResourceManager to load the game map
//			backgroundMap = manager.loadOtherMaps("backgroundMap.txt");
//			foregroundMap = manager.loadOtherMaps("foregroundMap.txt");
			map.setPlayer(Jade); // set the games main player to Jade
		} catch (IOException e){
			System.out.println("Invalid Map.");
		}
		
		player = new MidiPlayer();
		Sequence sequence;
		sequence = player.getSequence("sounds/smwovr2.mid");
        player.play(sequence, true);
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.addKeyListener(new SpriteListener(Jade));
		this.setFocusable(true); 
	}
	
	/**
	 * Automatically called as GamePanel is being added to its enclosing GUI component,
	 * and so is a good place to initiate the animation thread.
	 */
	public void addNotify() {
		super.addNotify(); // creates the peer
		startGame(); // start the thread
	}
	
	/**
	 * Start the game thread.
	 */
	private void startGame() {
		if(animator == null || !running) {
			animator = new Thread(this, "The Animator V 3.0");
			animator.start();
		}
	}
	
	/**
	 * Stop the game.
	 */
	public void stopGame() { running = false; }
	
	/**
	 * Defines a single game loop.
	 */
	public void gameAction() {
		gameUpdate(); // Update game state.
		gameRender(); // Draw to the double buffer.
		paintScreen(); // Draw double buffer to screen.
	}
	
	/**
	 * The main game loop - repeatedly update, repaint, sleep.
	 */
	public void run() {
		
		running = true;
		while(running) {
			
			try {
				gameAction();
				Thread.sleep(period);
			} catch(InterruptedException ex){}
		}
		System.exit(0); // so enclosing JFrame/JApplet exits
	}
	
	/**
	 * Update the state of all game objects. In the future this game logic
	 * should probably be abstracted out of this class.
	 */
	private void gameUpdate() {
		
		if (!gameOver) {
			// Update all relevant Creatures.
			for(int i = 0; i < map.relevantCreatures().size(); i++) {
				Creature c = map.relevantCreatures().get(i);
				if(!(c instanceof Coin)) {
					c.updateCreature(map, period);
					Jade.playerCollision(map, c);
					
				} else {
					c.updateCreature(map, period);
					Jade.playerCollision(map, c);
				}
			}
			
			// Debugging information:
			//System.out.println("relevant creatures size: " + map.relevantCreatures().size());
			//System.out.println("creatures size: " + map.creatures().size());
			//System.out.println(map.platforms().size());
			
			for(GameTile tile : map.animatedTiles()) {
	            tile.collidingCreatures().clear();  // clear the colliding sprites on the tile
	            tile.update(20);
			}
        
			// Add creatures that need to be created. They are added here to avoid concurrent modifcation errors.
            for(Creature c : map.creaturesToAdd()) {
            	map.creatures().add(c);
            }
            
            map.creaturesToAdd().clear(); // This line MUST be called BEFORE Jade.update(). Why?
            							  // If it is called after, all the creatures that are created
            							  // as a result of Jade colliding are not added next update because
            							  // they are cleared immediately afterwards.

			Jade.update(map, period);
			Coin.turn.update(period);
			map.relevantCreatures().clear();
			map.platforms().clear();
		}
	}
	
	/**
	 * Draws the game image to the buffer.
	 */
	private void gameRender() {
		if(dbImage == null) {
			dbImage = createImage(this.panelWidth, this.panelHeight);
			return;
		}
	    dbg = dbImage.getGraphics();    
		renderer.draw((Graphics2D) dbg, map, backgroundMap, foregroundMap, panelWidth, panelHeight);
	}
	
	/**
	 * Draws the game image to the screen by drawing the buffer.
	 */
	private void paintScreen() {	
		Graphics g;
		try {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))  {
				g.drawImage(dbImage, 0, 0, null);
				g.dispose();
			} 
		} catch (Exception e) { System.out.println("Graphics context error: " + e); }
	}
	
}