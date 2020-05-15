
public class ComputerMoveGenerator {
	
	private Board myGame;
	private int myDiskColor;
	
	
	
	public ComputerMoveGenerator(Board whichBoard) {
		myGame = whichBoard;
		
		if(myDiskColor == Board.RED_DISK) {
			System.out.println("Computer for RED here!!!");
		} else  {
			System.out.println("Computer for YELLOW here!!!");
		}
		
	}
	
	public void setColor(int whichColor) {
		myDiskColor = whichColor;
	}


}
