
public interface Board {
	//put common methods and methods you need for running Move generator
	
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int RED_DISK = 1;
	public static final int YEL_DISK = -1;
	public static final int BUTTON_WIDTH = 200;  //in pixels
	public static final int BUTTON_HEIGHT = 24;
	
	//put common methods and methods you need for running Move generator
	public int[][] getMatrix();
	//anything else that is common...
	
}
