package objects.creatures;


import java.awt.image.BufferedImage;

import animation.Animation;
import tile.TileMap;
import objects.base.Creature;
import util.ImageManipulator;



public class Coin extends Creature {
	
	private static BufferedImage[] c = { ImageManipulator.loadImage("items/Coin_5.png"), ImageManipulator.loadImage("items/Coin_6.png"),
		ImageManipulator.loadImage("items/Coin_7.png"), ImageManipulator.loadImage("items/Coin_8.png") };
	public static Animation turn = new Animation(150).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
	// Alternate Animation;
    //private static BufferedImage[] c = { loadImage("items/Coin_1.png"), loadImage("items/Coin_2.png"),
	//	loadImage("items/Coin_3.png"), loadImage("items/Coin_4.png") };
	
	//private Animation turn;
	private Animation shoot;
	
	public Coin(int pixelX, int pixelY) {
		
		super(pixelX, pixelY);
		setIsItem(true);
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}
		
		//turn = new Animation(1000).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
		shoot = new DeadAfterAnimation().setDAL(120).addFrame(c[0]).addFrame(c[1]).addFrame(c[2]).addFrame(c[3]);
		setAnimation(turn);
	}
	
	public void updateCreature(TileMap map, int time) {
		if(currentAnimation() == shoot) {
			super.update(time);
			y = y + dy * time;
			if(dy < 0) {
				dy = dy + .018f;
			} 
		}
	}
	
	public void shoot() {
		setIsCollidable(false);
		setAnimation(shoot);
		dy = -.3f;
	}

}
