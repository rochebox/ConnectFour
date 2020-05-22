
public interface Board {
	//put common methods and methods you need for running Move generator
	
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int RED_DISK = 1;
	public static final int YEL_DISK = -1;
	public static final int BUTTON_WIDTH = 200;  //in pixels
	public static final int BUTTON_HEIGHT = 24;
	public static final int PLAYER_1 = 1;
	public static final int PLAYER_2 = 2;
	public static final int HUMAN = -1;
	public static final int A_ROW = 0;
	public static final int A_COL = 1;
	public static final int NO = -1;
	
	//put common methods and methods you need for running Move generator
	public abstract int[][] getMatrix();
	
	//anything else that is common...
	public abstract int getGameMoveCount();
}
