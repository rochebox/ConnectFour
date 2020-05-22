import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class ComputerMoveGenerator  {

	
	private Board myGame;
	private int myDiskColor;
	private int myPlayer;   //either PLAYER_1 or PLAYER_2
	
	private Timer t;
	
	
	//constructor....using a polymorphic board abstract class
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
	
	public void setMyPlayer(int p) {
		myPlayer = p;
		
		if(myPlayer == Board.PLAYER_1) {
			System.out.println("Computer for PLAYER 1 ready!!!");
		} else  {
			System.out.println("Computer for PLAYER 2 ready!!!");
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
	
	public int[] getNextMove() {
		int[] theNextMove;
		
		//Come up with other moves... but start rnd
		
		int curMoveNumber = myGame.getGameMoveCount();
		
		if(curMoveNumber == 0) {
			theNextMove = makeFirstMove();
		} else {
		
		
			theNextMove = makeRandomMove();
		}
		
		System.out.println("CMG getNextMove " );
		System.out.println("nextMoveRow is... " + theNextMove[Board.A_ROW] );
		System.out.println("nextMoveCol is... " + theNextMove[Board.A_COL] );
		
		
		
		return theNextMove;
	}
	
	//THis would move to the middle
	public int[] makeFirstMove() {
		int middleCol = Board.COLS/2;
		int firstRow = canPlaceDisk(middleCol);
		
		int[] firstMoveCoords = new int[2];
		firstMoveCoords[0] = firstRow;
		firstMoveCoords[1] = middleCol;
		return firstMoveCoords;
		
	}
	
	
	//Create a method to make a random move
	public int[]makeRandomMove() {
		
		boolean colAndRowOK = false;
		int newCol = -1;
		int newRow = -1;
		do {
			newCol = (int)(Math.random() * Board.COLS);
			newRow = canPlaceDisk(newCol);
			if(newRow > -1) {
				colAndRowOK = true;
			}
		} while(!colAndRowOK);
		System.out.println("In make random Move " );
		System.out.println("newRow is... " + newRow );
		System.out.println("newCol is... " + newCol );
		//load the new row and col values
		int[] move = {newRow, newCol} ; //holds the row and col of the new move
		return move;
		
	}
	
	
	//For Homework March 5 -- Modified for CMG on May 19
	private int canPlaceDisk(int whichCol) {
		
		int whatRow = -1;
		//boolean dropping = true;
			
		int curPlace = 0;
		while(curPlace < Board.ROWS && myGame.getMatrix()[curPlace][whichCol] == 0) {
			curPlace += 1;
		}
		whatRow = curPlace - 1;
			
		System.out.println("In CMG canPlaceDisk() whichCol is " + whichCol +
				" and the rowPlace is = " + whatRow);
			
		return whatRow;
	}
	
	
	//ADD DELAY!!!
	public void sleepForAMove() {
        
        //Thread currThread = Thread.currentThread(); 
        //currThread.sleep(PenteGameBoard.SLEEP_TIME);
        
        try
        {
             
             Thread currThread = Thread.currentThread(); 
             currThread.sleep(1500); // sleep for 1500 milliseconds
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
       
    }



}
