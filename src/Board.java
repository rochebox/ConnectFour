import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Board extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

	private static final long serialVersionUID = 1L;
	public static final int ROWS = 6;
	public static final int COLS = 7;
	public static final int RED_DISK = 1;
	public static final int YEL_DISK = -1;
	public static final int BUTTON_WIDTH = 200;  //in pixels
	public static final int BUTTON_HEIGHT = 24;

	
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
	
	
	private int matrix[][] = new int[ROWS][COLS];
	private Image blueBoard;
	private Image arrow;
	private Image redDisk;
	private Image yellowDisk;
	
	//coordinates for the Arrow Key
	private int arrowX, arrowY;
	private int arrowW;
	
	//icon for message dialog
	private final ImageIcon redIcon = new ImageIcon("redDisk.png");
	private final ImageIcon yelIcon = new ImageIcon("yellowDisk.png");
	
	//***************** FOR WIN CHECKING AND SHOWING
	//Start and End Point of the Winning Set
	private ArrayList<Point> winMoveList;
	private boolean winGameOver = false;  //we don't have a winner yet
	
	//for communicating win or loss
	private Font font = new Font("Arial", Font.PLAIN, 56);
	private Font buttonFont = new Font("Arial", Font.PLAIN, 18);
	
    private FontRenderContext frc = new FontRenderContext(null, true, true);
    //null means no affine Transformation going on ... (no scaling, rotating or reflections happening...
    //true #1 means yes use anti aliasing
    //true #2 means yes use fractions for measurements...
	// ********************
    
    
    //Part I Adding Variables Etc for Playing with Computer Mover
    private JButton startRestartB = new JButton("Start/Restart");
    private JButton newGameB = new JButton("New Game");
    
   
    
    
    //Information about the game players
    private String player1Name = "no_one_yet";
    private String player2Name = "no_one_yet";
    
    private int player1Color = 0;
    private int player2Color = 0;
    private String player1ColorName = "";
    private String player2ColorName = "";
    private boolean drawPlayerNames = false;
    
    // private boolean startGame = false;
    private boolean startGame = true;
    private String whoMovesFirst = "";
    
    //This is for ending the game and for computer move generator
    private int moveCount = 0;
    
    private ComputerMoveGenerator p1CMG, p2CMG;
	
	
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
		//arrowH = 90; arbitrary
		
		//move BoardW H Up
		boardW = pWidth - (int)(pWidth * 2 * sideM);
		boardH = pHeight - (int)(pHeight * 2 * topM);
		boardStartX = (int)(pWidth * sideM);
		boardStartY = (int)(pHeight * (topM));
		colWidth = (int)(boardW/COLS);
		
		//set the first turn...
		currentTurn = RED_DISK;
		
		//enable mouseListening and clicking
		this.addMouseListener(this);
		this.setFocusable(true);
		this.addMouseMotionListener(this);
		
		this.setLayout(null);  //turns off the auto centering turn off layout
			
		//Part II --this is for a new start button
		startRestartB.setActionCommand("START_RESET");
		startRestartB.setToolTipText("Start a Game or Restart");
		startRestartB.addActionListener(this);
		startRestartB.setFont(buttonFont);
		startRestartB.setOpaque(true);
		startRestartB.setBackground(Color.CYAN);
		startRestartB.setBounds(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		
		//Part II-B -- this is for the new game button
		newGameB.setActionCommand("NEW_GAME");
		newGameB.setToolTipText("Keep Players, but Start a New Game");
		newGameB.addActionListener(this);
		newGameB.setFont(buttonFont);
		newGameB.setOpaque(true);
		newGameB.setBackground(Color.CYAN);
		newGameB.setBounds(pWidth - BUTTON_WIDTH, 0, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		//add both buttons to the panel	
		this.add(startRestartB);
		this.add(newGameB);
			
	   
}
	
	//override JPanel paintComponent
	public void paintComponent(Graphics g) {
		//background
		g.setColor(bColor);
		g.fillRect(0,0, pWidth, pHeight);
		
		
		
		//Draw all disks --first
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
		

		
		// NEW PART *************
		//draw the player names if its time
		if(drawPlayerNames) {
			
			
			if(player1Color == RED_DISK) {
				drawPlayerText((Graphics2D) g , "P1", 45, RED_DISK);
			} else {
				drawPlayerText((Graphics2D) g , "P1", 45, YEL_DISK);
			}
			
			if(player2Color == RED_DISK) {
				drawPlayerText((Graphics2D) g , "P2", 45, RED_DISK);
			} else {
				drawPlayerText((Graphics2D) g , "P2", 45, YEL_DISK);
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
		
		//First Step -- Put Circles around the winning moves...
		if(winGameOver) {
			System.out.println("There is a win.... we are drawing...");
			Graphics2D g2D = (Graphics2D) g;
			g2D.setColor(Color.BLACK);
			for(Point p : winMoveList) {
				
				g2D.setStroke(new BasicStroke(10));
				g2D.drawOval( 
						boardStartX + (colWidth * p.x) + 7,
						boardStartY + (int)(p.y * 1.5) + (colWidth * p.y) + 7,
						colWidth - 14, 
						colWidth - 14
						);
							
			}
			//Second Step --Write "YOU WIN!!!"
			String winString = "";
			if(currentTurn == RED_DISK) {
				winString += "Red Wins!!!";
			} else {
				winString += "Yellow Wins!!!";
			}
			drawWinText(g2D, winString );
			//g2D.dispose();
		}
	}
	
	
	//This allows the player names to be drawn.....
	private void drawPlayerText(Graphics2D g2D, String player, int startY, int whichDisk) {
		
		g2D.setColor(Color.GRAY);
		
		//Make up the "P1 is"... or "P2 is"... text...
		String drawString = "";
		if(player.equals("P1")) {
        	drawString = "P1: " + this.player1Name;
        } else {
        	drawString = "P2: " + this.player2Name;
        }
		
		//Make an image of the text to find out the length of the words
		TextLayout layout = new TextLayout(drawString, this.buttonFont, frc);
		Rectangle r = layout.getPixelBounds(null, 0, 0);
        //System.out.println(r);
        BufferedImage bi = new BufferedImage(
            r.width + 1, r.height + 1,
            BufferedImage.TYPE_INT_ARGB);
        
        int startX = 0;
        //Get a place to draw the text either screen right or left
        if(player.equals("P1")) {
        	startX = 15;
        } else {
        	startX = this.pWidth - r.width - 20 - 34;
        }
        
        // Get the text to the screen.....
        layout.draw(g2D, startX, startY);
       
        //NOW HIGHLIGHT WHO's TURN IT IS.........
        if(currentTurn == whichDisk) {
	        if(currentTurn == whichDisk) {
	        	//g2D.setColor(Color.RED);
	        	g2D.setColor(Color.MAGENTA);
	        	g2D.fillOval(startX + r.width + 17, startY-22, 34, 34);
	        } else {
	        	//g2D.setColor(Color.GRAY);
	        	//g2D.fillOval(startX + r.width + 17, startY-22, 34, 34);
	        }
        
        //..AND DRAW THE COLORED DISK FOR EACH PLAYER COLOR
       
        	if(whichDisk == RED_DISK) {
        		g2D.drawImage(redDisk, startX + r.width + 20, startY-19, 30, 30, this);
        	} else {
        		g2D.drawImage(yellowDisk, startX + r.width + 22, startY-17, 24, 24, this);
        	}
		}
	}
	
	private void drawWinText(Graphics2D g2D, String s ) {
		
		// fonts and font rendering context are shown above
        TextLayout layout = new TextLayout(s, font, frc);
        Rectangle r = layout.getPixelBounds(null, 0, 0);
        //System.out.println(r);
        BufferedImage bi = new BufferedImage(
            r.width + 1, r.height + 1,
            BufferedImage.TYPE_INT_ARGB);
        
       // g2D.setColor(Color.blue);
        int stringCenterX = (pWidth - r.width)/2;
        layout.draw(g2D, stringCenterX, -r.y + 5);  
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
		//boolean dropping = true;
		
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
		// MOUSE CLICKED PLAYS THE GAME
		
		
		if(startGame) {
				if(!winGameOver) {
					//if you are a computer mover ....
					
					// we do somethiing
					
					//otherwise....
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
							//  check here to see if you win
							//whichRow and whichCol   
							winGameOver = checkWin_(whichRow, whichCol);
							
							if(winGameOver == false) {
								changeTurn();
							} else {
								//stop the game...
								//this.paintImmediately(0, 0, boardW, boardH);
								repaint();
								
								Point[] winPoints = new Point[winMoveList.size()]; 
								winPoints = winMoveList.toArray(winPoints); 
						  
								System.out.print("The final winning points " );
						        for (Point x : winPoints) System.out.print(x + " "); 
						        System.out.println();	
							}
							
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
		}
	}
	
	public boolean checkWin_(int row, int col) {
			
		//check horizontal
		boolean win =  canDoAllSidesWinCheck_(row, col,  1,  0); //1 = horizontal "on"
		
		if(!win) {
			//if no win, check Vertical
			win = canDoAllSidesWinCheck_(row, col, 0, 1);        // 1 = vertical "on"
		}
		if(!win) {
			//if no win, check Diagonal Down
			win = canDoAllSidesWinCheck_(row, col, 1, 1);        //1 1 (both h. and v. "on")
		}
		if(!win){
			//if no win, check Diagonal Up
			win = canDoAllSidesWinCheck_(row, col, 1, -1);       //1 -1 (both h. and v. "on" but v. goes other way)
		}
		
		
		return win;
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
	
	
	//Doing all of the checking in one routine...
	
	public boolean canDoAllSidesWinCheck_(int row, int col, int horiz, int vert) {
		//This is also trying to find the Start and End of the Win
		//Reset the arrayList to track winning moves
		winMoveList = new ArrayList<Point>();
		winMoveList.add(new Point(col, row));  //add your move to the list -- it could win
											   // col = x and row = y
		
		boolean win = false;
		//we are going to look right and left
		int side1Count = 0, side2Count = 0;
		boolean  goingSide1Direction = true, goingSide2Direction = true;
		
		
		for(int pos = 1; pos <=3; pos++) {	
				
			//look basically going Left in the side 1 direction
			if(goingSide1Direction && col - (pos * horiz) >= 0
					&&  row - (pos * vert) < ROWS && row - (pos * vert) >= 0) {
				System.out.println("I'm in the bottom check");
				if(matrix[row - (pos * vert)][col - (pos * horiz)]== currentTurn) {
					side1Count++;
					winMoveList.add(0, new Point(col - (pos * horiz), row - (pos * vert)));  //adds to the front
				} else {
					goingSide1Direction = false;
				}
			}
			
			//look basically right going in the side 2 direction
			if(goingSide2Direction && col + (pos * horiz) < COLS 
					&& row + (pos * vert) < ROWS && row + (pos * vert) >= 0) {
				System.out.println("I'm in the top check");
				if(matrix[row + (pos * vert)][col + (pos * horiz)]== currentTurn) {
					side2Count++;
					winMoveList.add(new Point(col + (pos * horiz), row + (pos * vert) ));  //add to end
				} else {
					goingSide2Direction = false;
				}
			}
			
			if(side1Count + side2Count >= 3) {
				win = true;
				break;
			}
		}
		
		System.out.println("After check [hor=" + horiz + ", vert=" + vert 
				+ "] side2Count= " + side2Count + " | side1count= "
					+ side1Count);
		
		
		return win;
	}
	
	

	public boolean checkHorizontalWin_(int row, int col) {
		//This is also trying to find the Start and End of the Win
		//Reset the arrayList to track winning moves
		winMoveList = new ArrayList();
		winMoveList.add(new Point(col, row));  //add your move to the list -- it could win
		
		boolean win = false;
		//we are going to look right and left
		int leftCount = 0, rightCount = 0;
		boolean goingRight = true, goingLeft = true;
		
		
		for(int pos = 1; pos <=3; pos++) {	
			//look right at the position
			if(goingRight && (col + pos) < COLS) {
				if(matrix[row][col + pos]== currentTurn) {
					rightCount++;
					winMoveList.add(new Point((col + pos), row));  //add to end
				} else {
					goingRight = false;
				}
			}
			
			//look left at the position
			if(goingLeft && (col - pos) >= 0) {
				if(matrix[row][col - pos]== currentTurn) {
					leftCount++;
					winMoveList.add(0, new Point((col - pos), row));  //adds to the front
				} else {
					goingLeft = false;
				}
			}
			
			if(rightCount + leftCount >= 3) {
				win = true;
				break;
			}
		}
		
		System.out.println("After R-L check rCount= " + rightCount + " | lcount= "
					+ leftCount);
		
		
		return win;
	}

	private void doStartReset() {
		
		
		player1Name = JOptionPane.showInputDialog(this, "Name of Player 1 (Type \"c\" or \"computer\" for a computer player");
		
		if(player1Name == null) {
			JOptionPane.showMessageDialog(this, "Sorry you don't want to play today");
		} else {
			if(player1Name.toLowerCase().equals("c") || player1Name.toLowerCase().equals("computer")) {
				player1Name = "COMPUTER_PLAYER";
				// make a computer move generator too
			}
			
			//get the color
			boolean quitGame = false;
			do {
				String colorNameP1 =JOptionPane.showInputDialog(this, "What color disk for Player 1, Red or Yellow (input \"r\" or \"y\" )");
					
				if(colorNameP1 == null) {
					JOptionPane.showMessageDialog(this, "Sorry you don't want to play today");
					quitGame = true;
				} 
					
				if(!quitGame) {
					
					if(colorNameP1.toLowerCase().equals("red") || colorNameP1.toLowerCase().equals("r")) {
						player1Color = RED_DISK;
						player1ColorName = "RED";
					} else if (colorNameP1.toLowerCase().equals("yellow") || colorNameP1.toLowerCase().equals("y")) {
						player1Color = YEL_DISK;
						player1ColorName = "YELLOW";
					} else {
						JOptionPane.showMessageDialog(this, "Sorry your have to input Red or Yellow (input \"r\" or \"y\" )");
					}
				}
			} while(!quitGame && player1Color != RED_DISK && player1Color != YEL_DISK);
				
			//keep going for player 2 if you didn't quit
			
			if(!quitGame) {
				player2Name = JOptionPane.showInputDialog(this, "Name of Player 2 (Type \"c\" or \"computer\" for a computer player");
				
				if(player2Name == null) {
					JOptionPane.showMessageDialog(this, "Sorry you don't want to play today");
				} else {
					
					//Again, to the check for the computer
					if(player2Name.toLowerCase().equals("c") || player2Name.toLowerCase().equals("computer")) {
						player2Name = "COMPUTER_PLAYER";
						// make a computer move generator too.  And we can have 2 move generator object
					}
					
					//get the color for player 2
					String colorMsg = "";
					if(player1Color == RED_DISK) {
						colorMsg = "Since Player 1 (" + player1Name + ") is Red, Player 2 (" + player2Name + ") will be Yellow?";
						player2Color = YEL_DISK;
						player2ColorName = "YELLOW";
					} else {
						colorMsg = "Since Player 1 (" + player1Name + ") is Yellow, Player 2 (" + player2Name + ") will be Red?";
						player2Color = RED_DISK;
						player2ColorName = "RED";
					}
					
					colorMsg += ".....OK? (No will Quit Game)";
					
					int reply = JOptionPane.showConfirmDialog(this, colorMsg, "Confirm, please!", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.NO_OPTION) {
						JOptionPane.showMessageDialog(this, "Sorry you don't want to play today");
					} else {
					   		    
					    //find out who should go first:
					    
					    String[] options = new String[2];
					    options[0] = new String("Player 2 (" + player2Name +")?");
					    options[1] = new String("Player 1 (" + player1Name +")?");
					   
					    int whoIsFirst = JOptionPane.showOptionDialog(
					    		this,
					    		"Who should go first!",
					    		"Pick Starting Player",
					    		0,JOptionPane.INFORMATION_MESSAGE,null,options,null);
					    System.out.println("whoIsFirst is " + whoIsFirst);
					    
					    
					    if(whoIsFirst == 0) {  //player 2
					    	whoMovesFirst = "P2";
					    	JOptionPane.showMessageDialog(this, 
					    			"Player 2 (" + player2Name + ")\n" +
					    			player2ColorName + "\n"+
					    			"You go FIRST!"
					    			);
					    	//turn on the game
					    	startGame = true;
					    	
					    	//set the current turn
					    	currentTurn = player2Color;
					    	drawPlayerNames = true;
					    	repaint();
					    	
					    } else {				//player 1
					    	whoMovesFirst = "P1";
					    	JOptionPane.showMessageDialog(this, 
					    			"Player 1 (" + player1Name + ")\n" +
							    	player1ColorName + "\n"+
							    	"You go FIRST!"
					    			);
					    	//turn on the game
					    	startGame = true;
					    	
					    	//set the current turn
					    	currentTurn = player1Color;
					    	drawPlayerNames = true;
					    	repaint();
					    }

					    				    
					}			
				}		
			}	
		}
		
		
		//For checking....
		System.out.println("Player 1 Name  is: " + player1Name);
		System.out.println("Player 1 Color is: " + player1Color);
		
		System.out.println("Player 2 Name  is: " + player2Name);
		System.out.println("Player 2 Color is: " + player2Color);
		
		
	}
	
	private void doRestart() {
		
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				matrix[row][col] = 0;
			}
		}
		
		currentTurn = player1Color;
		startGame = true;
		drawPlayerNames = true;
		winGameOver = false;
		moveCount = 0;
		repaint();
		
	}
	
	
	//Allows a computer move generator to see the current game matrix
	public int[][] getMatrix() {
		return matrix;	
	}


	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		//This is for button processing -- the START BUTTON
		if("START_RESET".equals(e.getActionCommand())) {
			System.out.println("The Start/Reset Button says, \"Hello\"");
			doStartReset();
			
		}
		//This is for the New Game Button
		if("NEW_GAME".equals(e.getActionCommand())) {
			System.out.println("The New Game Button says, \"Hello\"");
			doRestart();
		}
		
		
	}
	
	
	
	
	
/*	
	public boolean checkVerticalWin_(int row, int col) {
		
		//Reset the arrayList to track winning moves
		winMoveList = new ArrayList();
		winMoveList.add(new Point(col, row)); //add this move -- it could be a winner
		
		boolean win = false;
		//we are going to look right and left
		int upCount = 0, downCount = 0;
		boolean goingUp = true, goingDown = true;
		
		for(int pos = 1; pos <=3; pos++) {	
			//look down from the position
			if(goingDown && (row + pos) < ROWS) {
				if(matrix[row + pos][col]== currentTurn) {
					winMoveList.add(new Point(col, row + pos)); //add to back
					downCount++;
				} else {
					goingDown = false;
				}
			}
			
			//look left at the position
			if(goingUp && (row - pos) >= 0) {
				if(matrix[row - pos][col]== currentTurn) {
					winMoveList.add(0, new Point(col, row - pos)); //add to front
					upCount++;
				} else {
					goingUp = false;
				}
			}
			
			if(upCount + downCount >= 3) {
				win = true;
			}
		}
		System.out.println("After U-D check uCount= " + upCount + " | dcount= "
					+ downCount);
		
		
		return win;
	}
*/

}
