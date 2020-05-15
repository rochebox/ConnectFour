
public class ComputerMoveGenerator {
	
	private Board myGame;
	private int myDiskColor;
	
	
	//constructor....
	public ComputerMoveGenerator(Board whichBoard) {
		myGame = whichBoard;
		
		printMyMatrix();
	}
	
	
	
	//method to get my color
	public void setMyColor(int whichColor) {
		myDiskColor = whichColor;
		
		System.out.println("Computer Move Generator On Line....");
		if(myDiskColor == Board.RED_DISK) {
			System.out.println("Computer for RED here!!!");
		} else  {
			System.out.println("Computer for YELLOW here!!!");
		}
	}
	
	
	public void printMyMatrix() {
		
		int matrix[][] = myGame.getMatrix();
		
		for (int r = 0; r < matrix.length; r++) {
			for (int c = 0; c < matrix[0].length; c++) {
				System.out.print(matrix[r][c] + "\t");
			}
			System.out.println();
		}
		
		
		
	}
	



}
