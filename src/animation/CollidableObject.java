package animation;

import sound.specific.JadeSoundManager22050Hz;



public class CollidableObject extends Sprite {
	
	protected JadeSoundManager22050Hz soundManager;
	private boolean isCollidable;
	private boolean isOnScreen;
	
	public CollidableObject(int pixelX, int pixelY, JadeSoundManager22050Hz soundManager) {
		super(pixelX, pixelY);
		this.isCollidable = true;
		setIsOnScreen(false);
		this.soundManager = soundManager;
	}
	
	public CollidableObject(int pixelX, int pixelY) {
		this(pixelX, pixelY, null);
	}
	
	public boolean isCollidable() {
		return isCollidable;
	}
	
	public void setIsCollidable(boolean isCollidable) {
		this.isCollidable = isCollidable;
	}
	
	public boolean isOnScreen() {
		return isOnScreen;
	}
	
	public void setIsOnScreen(boolean isOnScreen) {
		this.isOnScreen = isOnScreen;
	}
}
