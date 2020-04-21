import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JPanel implements MouseListener, MouseMotionListener {
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int RED_DISK = 1;
	public static final int YEL_DISK = -1;
	
	private int pWidth, pHeight;
	
	//important to carry around
	private int boardW;
	private int boardH;
	private int boardStartX, boardStartY;
	private int colWidth;
	
	private int currentTurn;
		
	
	//private Color bColor = Color.CYAN;
	private Color bColor = new Color(255, 199, 199);
	private double sideM =  0.05;
	private double topM = 0.17;
	private double arrowM = 0.05;
	
	
	private int matrix[][] = new int[6][7];
	private Image blueBoard;
	private Image arrow;
	private Image redDisk;
	private Image yellowDisk;
	
	//coordinates for the Arrow Key
	private int arrowX, arrowY;
	private int arrowH, arrowW;
	
	//icon for message dialog
	private final ImageIcon redIcon = new ImageIcon("redDisk.png");
	private final ImageIcon yelIcon = new ImageIcon("yellowDisk.png");
	
	
	//THis is the constructor....
	public Board (int w, int h) {
		pWidth = w;
		pHeight = h;
		this.setSize(pWidth, pHeight);
		this.setBackground(bColor);
		
		try {
			blueBoard = ImageIO.read(new File("connect4Board2.png"));
			arrow = ImageIO.read(new File("arrow.png"));
			redDisk =  ImageIO.read(new File("redDisk.png"));
			yellowDisk = ImageIO.read(new File("yellowDisk.png"));
			
			System.out.println("files are open--happy playing");
		} catch (Exception e) {
			System.out.println("problem opening files...");
		}
		
		arrowY = (int)(pHeight * arrowM);
		arrowX = (int)(pWidth * sideM);
		arrowW = 90; //arbitrary
		arrowH = 90; //arbitrary
		
		//move BoardW H Up
		boardW = pWidth - (int)(pWidth * 2* sideM);
		boardH = pHeight - (int)(pHeight * 2* topM);
		boardStartX = (int)(pWidth * sideM);
		boardStartY = (int)(pHeight * (topM));
		colWidth = (int)(boardW/COLS);
		
		
		
		
		//set the first turn...
		currentTurn = RED_DISK;
		
		//enable mouseListening and clicking
		this.addMouseListener(this);
		this.setFocusable(true);
		this.addMouseMotionListener(this);
	}
	
	//override JPanel paintComponent
	public void paintComponent(Graphics g) {
		//background
		g.setColor(bColor);
		g.fillRect(0,0, pWidth, pHeight);
		
		
		//loop to draw the board state
		int redLocSpacer = 1;
		int redYSpacer = 7;
		int redSizeSpacer = 3;
		for(int row = 0; row < ROWS; row++ ) {
			for(int col = 0; col < COLS; col++ ) {
				if(matrix[row][col] == 1) {
					//pseudo code..
					g.drawImage(redDisk, 
								boardStartX + (colWidth * col) - redLocSpacer,
								boardStartY + (colWidth * row) + (redLocSpacer * row) + redYSpacer,
								colWidth + redSizeSpacer, 
								colWidth + redSizeSpacer, 
								this);
				} else if(matrix[row][col] == -1) {
					g.drawImage(yellowDisk, 
							boardStartX + (colWidth * col),
							boardStartY + (colWidth * row),
							colWidth, 
							colWidth, 
							this);
				}
			}
		}
		
		//draws board with rectangle
		g.setColor(Color.BLACK);
		
		//draws border rect.
		g.drawRect(
				boardStartX, 
				boardStartY, 
				boardW, 
				boardH );
		
		// This draws the blue board...
		g.drawImage(blueBoard, 
				boardStartX, 
				boardStartY, 
				boardW, 
				boardH, 
				this);
		
		// This draws the arrow
		g.drawImage(arrow, 
				arrowX - (arrowW/2),  
				arrowY,  
				90, 90, this);				/* w and h */
		
	}
	
	//THis is a method to change turns
	private void changeTurn() {
		currentTurn *= -1;
	}
	
	
	
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
	
	
	
	
	//For Homework March 5
	private int placeDisk(int whichCol) {
		
		int whatRow = -1;
		boolean dropping = true;
		
		int curPlace = 0;
		while(curPlace < ROWS && matrix[curPlace][whichCol] == 0) {
				curPlace += 1;
		}
		whatRow = curPlace - 1;
		
		System.out.println("In letDiskFall rowPlace = " + whatRow);
		
		if(whatRow >= 0) {
			matrix[whatRow][whichCol] = currentTurn;
			markMatrix(whatRow, whichCol);
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
		// TODO Auto-generated method stub
		
		System.out.println("You clicked me.");
		
		int whichCol = getClickedColumn(e.getX());
		
	
		
		 if(whichCol >= 0) {
		 
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
				//Here stone placement was successful--so go on to next turn
				
				changeTurn();
			}
		} else {
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

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		arrowX = e.getX();
		
		// lower bound
		if( arrowX < boardStartX + (arrowW/2)) {
			arrowX = boardStartX +(arrowW/2);
		}
		
		//upper bound
		if(arrowX > boardStartX + boardW - (arrowW/2)) {
			arrowX = boardStartX + boardW - (arrowW/2);
		}
		
		repaint();
		//System.out.println("mouse moved x is " + arrowX);
	}
	
	
	
	

}
