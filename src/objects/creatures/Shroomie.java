package objects.creatures;



import java.awt.image.BufferedImage;
import java.util.Random;

import animation.Animation;
import sound.specific.JadeSoundManager22050Hz;
import objects.base.Creature;
import util.ImageManipulator;





public class Shroomie extends Creature {
	
	private Animation waddle, dead, flip;
	
	public Shroomie(int x, int y, JadeSoundManager22050Hz soundManager) {
		
		super(x, y, soundManager);
		
		BufferedImage w1 = ImageManipulator.loadImage("baddies/Shroomie_Normal_1.png");
		BufferedImage w2 = ImageManipulator.loadImage("baddies/Shroomie_Normal_2.png");
		BufferedImage smashed = ImageManipulator.loadImage("baddies/Shroomie_Dead.png");
		BufferedImage flipped = ImageManipulator.loadImage("baddies/Shroomie_Flip.png");
		
		final class DeadAfterAnimation extends Animation {
			public void endOfAnimationAction() {
				kill();
			}
		}

		waddle = new Animation(150).addFrame(w1).addFrame(w2);
		dead = new DeadAfterAnimation().setDAL(100).addFrame(smashed).setDAL(20).addFrame(smashed);
		flip = new Animation().addFrame(flipped).addFrame(flipped);
		setAnimation(waddle);
	}
	
	public void wakeUp() {
		Random r = new Random();
		super.wakeUp();
		dx = (r.nextInt(3) == 0) ? -.03f : .03f;
	}
	
	public void jumpedOn() {
		setAnimation(dead);
		setIsCollidable(false);
		dx = 0;
	}
	
	public void flip() {
		setAnimation(flip);
		setIsFlipped(true);
		setIsCollidable(false);
		dy = -.2f;
		dx = 0;
	}
}
