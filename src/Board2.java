

	import java.awt.Color;
	import java.awt.Graphics;
	import java.awt.Image;
	import java.awt.Point;
	import java.awt.event.KeyEvent;
	import java.awt.event.MouseEvent;
	import java.awt.event.MouseListener;
	import java.awt.event.MouseMotionListener;
	import java.io.File;
	import java.util.ArrayList;

	import javax.imageio.ImageIO;
	import javax.swing.ImageIcon;
	import javax.swing.JOptionPane;
	import javax.swing.JPanel;

	public class Board2 extends JPanel implements MouseListener, MouseMotionListener {
		
		private static final long serialVersionUID = 1L;  // you need this when a class is using java.io
		public static final int ROWS = 6;
		public static final int COLS = 7;
		public static final int RED_DISK = 1;
		public static final int YEL_DISK = -1;

		private int pWidth, pHeight;
		
		private int boardW;
		private int boardH;
		private int boardStartX, boardStartY;
		private int colWidth;
		
		private int currentTurn;
		
		private int arrowX, arrowY;
		private int arrowH, arrowW;
		
		private Color bColor = Color.CYAN;
		private double sideM = 0.05;
		private double topM = 0.15;
		private double arrowM = 0.05;
		
		int matrix[][] = new int[ROWS][COLS];
		private Image blueBoard;
		private Image arrow;
		private Image redDisk;
		private Image yellowDisk;
		
		
		private final ImageIcon redIcon = new ImageIcon("redDisk.png");
		private final ImageIcon yelIcon = new ImageIcon("yellowDisk.png");
		
		private ArrayList<Point> winMoveList;

		
		public Board2 (int w, int h) {
			pWidth = w;
			pHeight = h;
			this.setSize(pWidth, pHeight);
			this.setBackground(bColor);
			
			try {
				//blueBoard = ImageIO.read(new File("connect4Board.png"));
				//I'm using connect4Board2.png -- you have to switch this back
				blueBoard = ImageIO.read(new File("connect4Board2.png"));
				arrow = ImageIO.read(new File("arrow.png"));
				redDisk = ImageIO.read(new File("redDisk.png"));
				yellowDisk = ImageIO.read(new File("yellowDisk.png"));
				System.out.println("Files are open");
			} catch (Exception e) {
				System.out.println("problem opening files...");
			}
			
			arrowY = (int)(pHeight * arrowM);
			arrowX = (int)(pWidth * sideM);
			arrowW = 70; //arbitrary
			arrowH = 70; //arbitrary
			
			boardW = pWidth - (int)(pWidth * 2 * sideM);
			boardH = pHeight - (int)(pHeight * 2 * topM);
			boardStartX = (int)(pWidth * sideM);
			boardStartY = (int)(pHeight * topM);
			colWidth = (int)(boardW/COLS);
			
			currentTurn = RED_DISK;
			
			this.addMouseListener(this); 
			this.setFocusable(true); 
			this.addMouseMotionListener(this);
		}
		
		
		public void paintComponent(Graphics g) {
			
			g.setColor(bColor);
			g.fillRect(0,0,pWidth,pHeight);
			g.setColor(Color.BLACK);
		
			g.drawRect((int)(pWidth * sideM),
						(int)(pHeight* topM), 
						(int)(pWidth * (1-sideM*2)),
						(int)(pHeight * (1-topM*2)));
			
			/* this was moved to bottom of method
			g.drawImage(blueBoard,
					(int)(pWidth * sideM),
					(int)(pHeight* topM), 
					(int)(pWidth * (1-sideM*2)),
					(int)(pHeight * (1-topM*2)), this);
			*/
			
			g.drawImage(arrow,
					arrowX - (arrowW/2),
					arrowY,
					arrowW, arrowH, this);   // we have a variable for arrow w/h should use it.
			
			/* will's spacers
			int redXSpacer = 1;
			int redSizeSpacer = 3;
			int yellowXSpacer = 1;
			int yellowSizeSpacer = 3;
			*/
			
			
			//Mr. Roche's spacer info
			//int redLocSpacer = 1;
			int redXSpacer = 2;
			int redSizeSpacer = 9;
			
			int yellowXSpacer = 13;
			int yellowSizeSpacer = 6;
		
			for(int row = 0; row < ROWS; row++) {
				for(int col = 0; col < COLS; col++) {
					if(matrix[row][col] == 1) {
						g.drawImage(redDisk, 
								boardStartX + (colWidth * col) + redXSpacer - redSizeSpacer,
								boardStartY + (colWidth * row) + (int)(colWidth * 0.1 * row) + (int)(colWidth * 0.02 * (ROWS-1-row)) - redSizeSpacer,
								colWidth + (redSizeSpacer * 2),
								colWidth + (redSizeSpacer * 2),
								this);
					} else if (matrix[row][col] == -1) {
						g.drawImage(yellowDisk, 
								boardStartX + (colWidth * col) + yellowXSpacer - yellowSizeSpacer,
								boardStartY + (colWidth * row) + (int)(colWidth * 0.11 * row) + (int)(colWidth * 0.045 * (ROWS-1-row)) - yellowSizeSpacer,
								(int)(colWidth * 0.78) + (yellowSizeSpacer * 2),
								(int)(colWidth * 0.78) + (yellowSizeSpacer * 2),
								this);
					} else {
						//Draw purple
					}
				}

			}
			
			/* draw the board after to hide things...
			 * 
			 */
			g.drawImage(blueBoard,
					(int)(pWidth * sideM),
					(int)(pHeight* topM), 
					(int)(pWidth * (1-sideM*2)),
					(int)(pHeight * (1-topM*2)), this);
			
			
			
		}
		public boolean winCheck() {
				boolean win = false;
				for (int i = 0; i < ROWS; i++)
				{
					for (int j = 0; j < COLS; j++)
					{
						//Check Across
						if (j <= COLS - 4)
						{
							System.out.println("Check Accross");
							if (Math.abs(matrix[i][j] + matrix[i][j+1] + matrix[i][j+2] + matrix [i][j+3]) == 4)
							{
								win = true;
							}
						}
						//Check Down
						if (i <= ROWS - 4)
						{
							System.out.println("Check Down");
							if (Math.abs(matrix[i][j] + matrix[i+1][j] + matrix[i+2][j] + matrix [i+3][j]) == 4)
							{
								win = true;
							}
						}
						//Check Diagonally Right
						if (j <= COLS - 4 && i <= ROWS - 4)
						{
							System.out.println("Check Diag Down");
							if (Math.abs(matrix[i][j] + matrix[i+1][j+1] + matrix[i+2][j+2] + matrix [i+3][j+3]) == 4)
							{
								win = true;
							}
						}
						//Check Diagonally Left
						System.out.println();
						System.out.println("Before last test: i   is " + i  + " Rows - 4 is " + (ROWS-4) + " i <= ROWS - 4: " + (i <= ROWS - 4));
						System.out.println("Before last test: j   is " + j  + " Cols - 4 is " + (COLS-4) + " j <= COLS - 4: " + (j <= COLS - 4));
						//System.out.println("Before last test: i   is " + i + "| j is " + j);
						System.out.println("Before last test: i-1 is " + (i-1) + " " + (i - 1 >=0));
						System.out.println("Before last test: i-2 is " + (i-2) + " " + (i - 2 >=0));
						System.out.println("Before last test: i-3 is " + (i-3) + " " + (i - 3 >=0));
						

						
						
						if ( j <= COLS - 4 && /* i <= ROWS - 4 && */ i - 1 >=0 && i - 2 >=0 && i - 3 >= 0) 
						{
					
							if (Math.abs(matrix[i][j] + matrix[i-1][j+1] + matrix[i-2][j+2] + matrix [i-3][j+3]) == 4)
							{
								win = true;
							}
						}
					}
				}
				return win;
		}
		
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("You imPRESS me");
					
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("You released me!");
					
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
			System.out.println("Mouse entered");
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("You exited me");
					
		}

		
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
					
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			arrowX = e.getX();
			
			//this checks left edge...
			if(arrowX < boardStartX + (arrowW/2 /* arrowM/2 */  )) {   //I think this should be arrowW
				arrowX = (int)(boardStartX + (arrowW/2));	
			}
						
			//this checks the right edge 
			if(arrowX > boardStartX +boardW -(arrowW/2)) {
				arrowX = boardStartX + boardW - (arrowW/2);
			}
			repaint();
			//System.out.println("Mouse moved x is " + arrowX);
		}
				
				
				
		private void changeTurn() {
			currentTurn *= -1;
		}
		
		/* this was not complete....
		private int getClickedColumn(int xLocation) {
			
				int whichCol = -1;
					
				return whichCol;
		} */
		//For homework March 5
		private int getClickedColumn(int xLocation) {
			
			int whichCol = -1;
			
			int colNum = 0;
			int colStart = 0, colEnd = 0;
			int circleMargin = 10;
			while(whichCol < 0 && colNum < 7) {
				colStart = (boardStartX + (colWidth * colNum)) + circleMargin;
				colEnd = (boardStartX + (colWidth * (colNum+1) - circleMargin));
					
				if(colStart < xLocation && xLocation < colEnd) {
					whichCol = colNum;
				}			
				colNum++;		
			}
				
			System.out.println("End of findColNum and theCol is " + whichCol);
			return whichCol;
		}
		
		
		
		/* this isn't complete....
		private int placeDisk(int whichCol) {
			
			return 0;	
		} */
		//For Homework March 5
		private int placeDisk(int whichCol) {
			
			int whatRow = -1;
			//boolean dropping = true;
			
			int curPlace = 0;
			while(curPlace < ROWS && matrix[curPlace][whichCol] == 0) {
					curPlace += 1;
			}
			whatRow = curPlace - 1;
			
			System.out.println("In letDiskFall rowPlace = " + whatRow);
			
			if(whatRow >= 0) {
				matrix[whatRow][whichCol] = currentTurn;
				markMatrix(whatRow, whichCol);  //this is a helper method to put a 1 or -1 in the matrix
			}
			return whatRow;
		}
		
		private void markMatrix(int newRow, int newCol) {
			
			if (matrix[newRow][newCol] == 0) {
				matrix[newRow][newCol] = currentTurn;
						
			} else {
				System.out.println("ERROR --- Problem with matrix[" + 
						newRow + "][" + newCol + "]");
			}
			
			System.out.println("At the end of Mark Matrix the matrix is");
			for(int r = 0; r < ROWS; r++) {
				for(int c = 0; c < COLS; c++) {
					System.out.print("" + matrix[r][c]  + "\t");
				}
				System.out.println();
			}
			
		}
		
		
					
		@Override
		public void mouseClicked(MouseEvent e) {
			
			//System.out.println("You clicked me");

			boolean win = false;
			if(!win) {   //if there is no "win" yet....
				
				//1. find out which column the click is in ....error will be -1
				int whichCol = getClickedColumn(e.getX());
				
				//2.A if its not an error
				if(whichCol >= 0) {
					
					//3. find out what row the disk will be place error again will be -1
					int whichRow = placeDisk(whichCol);
					
					
					System.out.println("Which row is ..  " + whichRow);
					if(whichRow < 0) {
						if(currentTurn == RED_DISK) {
							JOptionPane.showMessageDialog(this, "Column is full", 
								"Problem", JOptionPane.INFORMATION_MESSAGE, redIcon);
						} else {
							JOptionPane.showMessageDialog(this, "Column is full", 
								"Problem", JOptionPane.INFORMATION_MESSAGE, yelIcon);
						}
					} else {

						//4. Here stone placement was successful--so go on to next turn
						//  check here to see if you win
						//whichRow and whichCol   

						win = winCheck();
						//HERE *****
						System.out.println("In mouseClicked win is " + win);
						if(win == false) {
								changeTurn();
						} else {
							//stop the game...

						}
					}
										
				} else {
					//2.B if there was click and you can't place a stone
					if(currentTurn == RED_DISK) {
						JOptionPane.showMessageDialog(this, "You can't place a disk here", 
									"Problem", JOptionPane.INFORMATION_MESSAGE, redIcon);
					} else {
						JOptionPane.showMessageDialog(this, "You can't place a disk here", 
									"Problem", JOptionPane.INFORMATION_MESSAGE, yelIcon);
					}
				}

				repaint();	

			}

		}
					
	}
		
