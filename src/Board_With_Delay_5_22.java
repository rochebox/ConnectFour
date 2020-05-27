
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
import javax.swing.Timer;

public class Board_With_Delay_5_22 extends JPanel implements MouseListener, MouseMotionListener, ActionListener, Board {

	//plays well with an 800x950px Board --- will have to be adjusted if size is changed 
	private static final long serialVersionUID = 1L;  // you need this when a class is using java.io
	
	private int pWidth, pHeight;
	
	private int boardW;
	private int boardH;
	private int boardStartX, boardStartY;
	private int colWidth;
	
	private int currentTurn;
	
	private int arrowX, arrowY;
	private int arrowH, arrowW;
	
	//private Color bColor = Color.CYAN;
	private Color bColor = new Color(255, 199, 199);
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
	private final ImageIcon boardIcon = new ImageIcon("connect4Board2.png");
	
	//create small image icons  to make the game better
	private final ImageIcon smallIconRed = new ImageIcon(
			redIcon.getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)); 
	
	private final ImageIcon smallIconYel = new ImageIcon(
			yelIcon.getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)); 
	
	private final ImageIcon smallBoardIcon = new ImageIcon(
			boardIcon.getImage().getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH)); 

	
	private Font font = new Font("Arial", Font.PLAIN, 56);
	private Font buttonFont = new Font("Arial", Font.PLAIN, 18);
	//this turns on anti-aliasing...
	private FontRenderContext frc = new FontRenderContext(null, true, true);
	
	private ArrayList<Point> winMoveList;
	
	
	//Information about the game players
	
	private String player1Name = "no_one_yet";
	private String player2Name = "no_one_yet";    
	
	private int player1Color = 0;
	private int player2Color = 0;
	private String player1ColorName = "";
	private String player2ColorName = "";
	private boolean drawPlayerNames = false;
	private String whoMovesFirst = "";
	   
	private boolean startGame = false;
	 //private boolean startGame = true;
	private boolean winGameOver = false;
	 
	
	 //This is for ending the game and for computer move generator
	   
	private int moveCount = 0;
	private ComputerMoveGenerator p1CMG = null, p2CMG = null;
	   
	//New Button Stuff
	private JButton startRestartB = new JButton("Start/Restart");
	private JButton newGameB = new JButton("New Game");
	
	//New Timer Stuff
	private Timer doMoveTimer;
	private Timer nextMoveTimer;
	private boolean lockedToDrawNextComputerMove = false;
	private int lockedFindNextComputerMove = NO;
	private int[] moveToDo = null;
	

	//Constructor
	public Board_With_Delay_5_22 (int w, int h) {
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
		
		//This sets up mouse motion and listening...
		this.addMouseListener(this);
		this.setFocusable(true);
		this.addMouseMotionListener(this);
		
		doMoveTimer = new Timer(2000, this);
		nextMoveTimer = new Timer(2500, this);
		
		doMoveTimer.setActionCommand("DO_MOVE_TIMER");
		nextMoveTimer.setActionCommand("NEXT_MOVE_TIMER");
		
		doMoveTimer.restart();
		nextMoveTimer.restart();
	
	}

	//Paint component ----Needs spacer adjustment
	//this overrides the same method in JPanel paintComponent
	public void paintComponent(Graphics g) {
		//background
		g.setColor(bColor);
		g.fillRect(0,0, pWidth, pHeight);
			
		int redXSpacer = (int)(colWidth * 0.02);
		int redSizeSpacer = (int)(colWidth * 0.12);
		
		int yellowXSpacer = (int)(colWidth * 0.13);
		int yellowSizeSpacer = (int)(colWidth * 0.09);
		
		for(int row = 0; row < ROWS; row++ ) {
			for(int col = 0; col < COLS; col++ ) {
				if(matrix[row][col] == 1) {
					//pseudo code..
					g.drawImage(redDisk,
							boardStartX + (colWidth * col) + redXSpacer - redSizeSpacer ,
							boardStartY + (colWidth * row) + (int)(colWidth * 0.075 * row) + (int)(colWidth * 0.009 * (ROWS-1-row)) - redSizeSpacer,
							colWidth + (redSizeSpacer * 2),
							colWidth + (redSizeSpacer * 2),
							this);
							
				} else if(matrix[row][col] == -1) {
					g.drawImage(yellowDisk,
							boardStartX + (colWidth * col) + yellowXSpacer - yellowSizeSpacer,
							boardStartY + (colWidth * row) + (int)(colWidth * 0.09 * row) + (int)(colWidth * 0.045 * (ROWS-1-row)) - yellowSizeSpacer,
							(int)(colWidth * 0.78) + (yellowSizeSpacer * 2),
							(int)(colWidth * 0.78) + (yellowSizeSpacer * 2),
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
			90, 90, this); /* w and h */
	
		//First Step -- Put Circles around the winning moves...
		if(winGameOver) {
			//System.out.println("There is a win.... we are drawing...");
			Graphics2D g2D = (Graphics2D) g;
			g2D.setColor(Color.BLACK);
			
			for(Point p : winMoveList) {
				g2D.setStroke(new BasicStroke(14));
				g2D.drawOval(
					boardStartX + (colWidth * p.x) + (int)(colWidth * 0.01),
					boardStartY + (int)(p.y * (colWidth * 0.05)) + (int)(colWidth * p.y) + (int)(colWidth * 0.075),
					colWidth - (int)(colWidth * 0.004),
					colWidth - (int)(colWidth * 0.001)
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
	    		 g2D.setColor(Color.MAGENTA);
	    		 g2D.fillOval(startX + r.width + 17, startY-22, 34, 34);
	    	 }
	       
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
	            r.width +1, r.height + 1,
	            BufferedImage.TYPE_INT_ARGB);
	       
	       // g2D.setColor(Color.blue);
	        int stringCenterX = (pWidth - r.width)/2;
	        layout.draw(g2D, stringCenterX, -r.y + 5);  
	}
	
	
	public boolean checkWin_(int row, int col) {
		
		System.out.println("In checkWin() at top.....");
		System.out.println("row is ..... " + row);
		System.out.println("col is ..... " + col);
		
		//check horizontal
		boolean win =  canDoAllSidesWinCheck_(row, col,  1,  0); //1 = horizontal "on"
		System.out.println("After Horiz winCheck_ win is ... " + win);
		if(!win) {
			//if no win, check Vertical
			win = canDoAllSidesWinCheck_(row, col, 0, 1);        // 1 = vertical "on"
			System.out.println("After Vert. winCheck_ win is ... " + win);
		}
		if(!win) {
			//if no win, check Diagonal Down
			win = canDoAllSidesWinCheck_(row, col, 1, 1);        //1 1 (both h. and v. "on")
			System.out.println("After Diag1 winCheck_ win is ... " + win);
		}
		if(!win){
			//if no win, check Diagonal Up
			win = canDoAllSidesWinCheck_(row, col, 1, -1);       //1 -1 (both h. and v. "on" but v. goes other way)
			System.out.println("After Diag2 winCheck_ win is ... " + win);
		}
		
		
		return win;
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
					//System.out.println("I'm in the bottom check");
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
					//System.out.println("I'm in the top check");
					if(matrix[row + (pos * vert)][col + (pos * horiz)]== currentTurn) {
						side2Count++;
						winMoveList.add(new Point(col + (pos * horiz), row + (pos * vert) ));  //add to end
					} else {
						goingSide2Direction = false;
					}
				}
				
				if(side1Count + side2Count >= 3) {
					win = true;
					int count = 0;
					while(winMoveList.size() > 4) {
						if(count % 2 == 0) {
							winMoveList.remove(winMoveList.size()-1); //remove back
						} else {
							winMoveList.remove(0);// remove from
						}
					}
					break;
				}
			}
			
			System.out.println("After check [hor=" + horiz + ", vert=" + vert 
					+ "] side2Count= " + side2Count + " | side1count= "
						+ side1Count);
			
			
			return win;
		}
		
	
	
	
	//This is triggered when the "Start" button is pushed --- it starts game...
	private void doStartReset() {
		
		player1Name = (String) JOptionPane.showInputDialog(this, 
				"Name of Player 1 (Type \"c\" or \"computer\" for a computer player", 
				"Connect 4",JOptionPane.INFORMATION_MESSAGE,this.smallBoardIcon,null,"");
		
		//exit
		if(player1Name == null) {
			JOptionPane.showMessageDialog(this, "Sorry you don't want to play today", 
					"Connect 4", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
			
		} else {  //here there is a player, it might be a computer
			if(player1Name.toLowerCase().equals("c") || player1Name.toLowerCase().equals("computer")) {
				player1Name = "COMPUTER_PLAYER";
				// make a computer move generator too-- buy calling the constructor for CMG
				p1CMG = new ComputerMoveGenerator(this);
				p1CMG.setMyPlayer(PLAYER_1);
			
				
			}
			
			//get the color
			boolean quitGame = false;
			do {
				String colorNameP1 =(String) JOptionPane.showInputDialog(this, 
						"What color disk for Player 1, Red or Yellow (input \"r\" or \"y\" )",
						"Connect 4",JOptionPane.INFORMATION_MESSAGE,this.smallBoardIcon,null,"");
						
				// exit
				if(colorNameP1 == null) {
					JOptionPane.showMessageDialog(this, "Sorry you don't want to play today",
							"Connect 4", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
					quitGame = true;
				} 
					
				if(!quitGame) {  //if you are here you have a color picked for Player 1
					
					if(colorNameP1.toLowerCase().equals("red") || colorNameP1.toLowerCase().equals("r")) {
						player1Color = RED_DISK;
						player1ColorName = "RED";
						
						if(player1Name.equals("COMPUTER_PLAYER")) {
							p1CMG.setMyColor(player1Color);  //sets color for CMG to RED
						}
					} else if (colorNameP1.toLowerCase().equals("yellow") || colorNameP1.toLowerCase().equals("y")) {
						player1Color = YEL_DISK;
						player1ColorName = "YELLOW";
						
						if(player1Name.equals("COMPUTER_PLAYER")) {
							p1CMG.setMyColor(player1Color);  //sets color for CMG to YELLOW
						}
						
					} else {
						JOptionPane.showMessageDialog(this, 
								"Sorry your have to input Red or Yellow (input \"r\" or \"y\" )",
								"Connect 4", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
					}
				}
			} while(!quitGame && player1Color != RED_DISK && player1Color != YEL_DISK);
				
			//keep going for player 2 if you didn't quit
			
			if(!quitGame) {
				player2Name = (String) JOptionPane.showInputDialog(this, 
						"Name of Player 2 (Type \"c\" or \"computer\" for a computer player",
						"Connect 4",JOptionPane.INFORMATION_MESSAGE,this.smallBoardIcon,null,"");
				
				//EXIT
				if(player2Name == null) {
					JOptionPane.showMessageDialog(this, "Sorry you don't want to play today",
							"Connect 4", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
				} else {
					//Again, to the check for the computer
					if(player2Name.toLowerCase().equals("c") || player2Name.toLowerCase().equals("computer")) {
						player2Name = "COMPUTER_PLAYER";
						// make a computer move generator too-- buy calling the constructor for CMG
						p2CMG = new ComputerMoveGenerator(this);
						p2CMG.setMyPlayer(PLAYER_2);	
					}
				
					//get the color for player 2
					String colorMsg = "";
					if(player1Color == RED_DISK) {
						colorMsg = "Since Player 1 (" + player1Name + ") is Red, \nPlayer 2 (" + player2Name + ") will be Yellow?";
						player2Color = YEL_DISK;
						player2ColorName = "YELLOW";
						
						if(player2Name.equals("COMPUTER_PLAYER")) {
							p2CMG.setMyColor(player2Color);
						}
					} else {
						colorMsg = "Since Player 1 (" + player1Name + ") is Yellow, \nPlayer 2 (" + player2Name + ") will be Red?";
						player2Color = RED_DISK;
						player2ColorName = "RED";
						
						if(player2Name.equals("COMPUTER_PLAYER")) {
							p2CMG.setMyColor(player2Color);
						}
					}
					
					colorMsg += ".....OK? \n(No will Quit Game)";
					
					int reply = JOptionPane.showConfirmDialog(this, colorMsg, "Confirm, please!", JOptionPane.YES_NO_OPTION, 
							JOptionPane.YES_NO_CANCEL_OPTION, this.smallBoardIcon
							);
					
					
					if (reply == JOptionPane.NO_OPTION) {
						JOptionPane.showMessageDialog(this, "Sorry you don't want to play today",
								"Connect 4", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
					} else {
					   		    
					    //find out who should go first:
					    
					    String[] options = new String[2];
					    options[0] = new String("Player 2 (" + player2Name +")?");
					    options[1] = new String("Player 1 (" + player1Name +")?");
					   
					    int whoIsFirst = JOptionPane.showOptionDialog(
					    		this,
					    		"Who should go first!",
					    		"Pick Starting Player",
					    		0,JOptionPane.INFORMATION_MESSAGE,this.smallBoardIcon,options,null);
					    System.out.println("whoIsFirst is " + whoIsFirst);
					    
					    
					    if(whoIsFirst == 0) {  //player 2
					    	whoMovesFirst = "P2";
					    	//this will clear the board and turn on game
					    	resetMatrix();
					    	resetGameVariables();
					    	//set the current turn
					    	currentTurn = player2Color;
					    	repaint();
					    	
					    } else {				//player 1
					    	whoMovesFirst = "P1";
					    	//this will clear the board
					    	resetMatrix();
					    	resetGameVariables();
					    	//set the current turn
					    	currentTurn = player1Color;   // *** might be redundant now...
					    	repaint();
					    }
					    	
					    	// allow the computer to make a move if needed
					    	
						//FIGURE OUT WHETHER ITS A COMPUTER MOVE TURN OR NOT....
					    int computerOrHumanMove = seeIfComputerMove();
//					    System.out.println("At end of doStartReset computerOrHumanMove is " + 
//					    		computerOrHumanMove);
						if(computerOrHumanMove != HUMAN) {
							lockedFindNextComputerMove = computerOrHumanMove;
							System.out.println("Hello in doStartReset() We Are Locked for a Next Computer Move");
							System.out.println("LockedForNextComputerMove is " +  lockedFindNextComputerMove);
							System.out.println("NOW WAITING TO MAKE THE NEXT MOVE ......... ");
							System.out.println();
							//do computer move ....for the computer player
							//doComputerMove(computerOrHumanMove);  // Player1 or Player2
						}      				    
					}			
				}		
			}	
		}
	}
	
	public void setTheNextComputerMove(int whichPlayer) {
		//private boolean lockedForComputerMove = false;
		//private int lockedForNextComputerMove = NO;
		
		int[] theNextMove;  //variable to hold the row and col of next move
		
		if(whichPlayer == PLAYER_1) {
			System.out.println("Player 1's move is now being made by COMPUTER.....!!!!");
			//DO A MOVE!!!!
			theNextMove = p1CMG.getNextMove();
		} else {
			System.out.println("Player 2's move is now being made by COMPUTER.....!!!!");
			//DO A MOVE!!!!
			theNextMove = p2CMG.getNextMove();
		}
		
		
		//try to place the disk from the computer move.....
		System.out.println("In setTheNextComputerMove().....Just Before Disk Placement");
		System.out.println("Row for move is: " + theNextMove[Board.A_ROW]);
		System.out.println("Col for move is: " + theNextMove[Board.A_COL]);
		
		moveToDo = new int[2];
		moveToDo[0] = theNextMove[Board.A_ROW];
		moveToDo[1] = theNextMove[Board.A_COL];
		System.out.println("From setTheNextComputerMove() we have new move at: ");
		System.out.println("NextMove [" + moveToDo[0] + "][" + moveToDo[1] + "]");
		System.out.println();
		
		//set up for drawing it...
		lockedToDrawNextComputerMove = true;  
		lockedFindNextComputerMove = NO;
	}
	
	
	public void drawTheNextComputerMove() {
		
		//private boolean lockedForComputerMove = false;
		//private int lockedForNextComputerMove = NO;
		
//		int[] theNextMove;  //variable to hold the row and col of next move
//		
//		if(whichPlayer == PLAYER_1) {
//			System.out.println("Player 1's move is now being made by COMPUTER.....!!!!");
//			//DO A MOVE!!!!
//			theNextMove = p1CMG.getNextMove();
//		} else {
//			System.out.println("Player 2's move is now being made by COMPUTER.....!!!!");
//			//DO A MOVE!!!!
//			theNextMove = p2CMG.getNextMove();
//		}
		System.out.println("In DrawTheNextComputerMove()...at top");
		
		if(lockedToDrawNextComputerMove) {
			//try to place the disk from the computer move.....
			System.out.println("In doComputerMove().....Just Before Disk Placement");
			System.out.println("Row for move is: " + moveToDo[Board.A_ROW]);
			System.out.println("Col for move is: " + moveToDo[Board.A_COL]);
			
			int whichRow = placeDisk(moveToDo[Board.A_COL]);
			System.out.println("Back from placeDisk() and whichRow is " + whichRow);
			
			if(whichRow != moveToDo[Board.A_ROW]) {
				if(currentTurn == RED_DISK) {
					System.out.println("ERROR with CMG for Red Player...." );
				} else {
					System.out.println("ERROR with CMG for Yellow Player...." );
				}
			} else {
				//Here stone placement was successful--so go on to next turn
				//  check here to see if you win  
				repaint();
				moveCount++;   //count moves on HUMAN-Player Side
				winGameOver = checkWin_(moveToDo[Board.A_ROW], moveToDo[Board.A_COL]);
				System.out.println("In drawTheNextComputerMove() back from checkWin_");
				System.out.println("winGameOver is " + winGameOver);
				if(winGameOver == false) {
					
					if(moveCount >= Board.ROWS * Board.COLS) {
						endOnTieGame();
						
					} else {	
						changeTurn();
						
						
						// This is for the possibility of a second computer move....
						int computerOrHumanMove = seeIfComputerMove();
						if(computerOrHumanMove != HUMAN) {
							//do computer move ....but for what player..
							//System.out.println("In doComputerMove() bottom --we are making second computer move");
							repaint();
							//this.paintImmediately(0, 0, boardW, boardH);
							//sleepForAMove();
							//doComputerMove(computerOrHumanMove);
							lockedFindNextComputerMove = computerOrHumanMove;
						} 
					}
				} else {			
					Point[] winPoints = new Point[winMoveList.size()]; 
					winPoints = winMoveList.toArray(winPoints); 
			  
					System.out.print("The final winning points " );
			        for (Point x : winPoints) System.out.print(x + " "); 
			        System.out.println();	
				}
				lockedToDrawNextComputerMove = false;
			}	
		}
	}
	
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("You imPRESS me");
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("You released me!");
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Mouse entered");	
	}

	
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("You exited me");
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
		
		//System.out.println("End of findColNum and theCol is " + whichCol);
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
		
		//System.out.println("In letDiskFall rowPlace = " + whatRow);
		
		if(whatRow >= 0) {
			//matrix[whatRow][whichCol] = currentTurn;
			markMatrix(whatRow, whichCol);  //this is a helper method to put a 1 or -1 in the matrix
		}
		return whatRow;
	}
	
	
	

	private void markMatrix(int newRow, int newCol) {
		System.out.println("In Board markMatrix...");
		System.out.println("newRow is......... " +  newRow);
		System.out.println("newCol is......... " +  newCol);
		System.out.println("matrix[r][c] is .. " +  matrix[newRow][newCol]);
		
		if (matrix[newRow][newCol] == 0) {
			matrix[newRow][newCol] = currentTurn;
		} else {
			System.out.println("ERROR --- Problem with matrix[" +
					newRow + "][" + newCol + "]");
		}
		
		System.out.println("At the end of Mark Matrix the matrix is");
		for(int r = 0; r < ROWS; r++) {
			for(int c = 0; c < COLS; c++) {
				String s = Integer.toString(matrix[r][c]);
				if(s.length() == 2) {
					System.out.print("" + matrix[r][c]  + "\t");
				} else {
					System.out.print(" " + matrix[r][c]  + "\t");
				}
			}
			System.out.println();
		}
	}
	
	//To figure out if currentTurn is a computer move
	public int seeIfComputerMove() {
		//CODES for Computer Moves
		// -1 equals a HUMAN MOVE...
		//  1 equals PLAYER 1 COMPUTER MOVE 
		//  2 equals PLayer 2
		// 3 possible outcomes are turned back!!!
//		System.out.println("In seeIfComputerMove()....");
//		System.out.println("currentTurn is ...." + currentTurn);
//		System.out.println("player1Color is ..." + player1Color);
//		System.out.println("player1Name is ...." + player1Name);
//		System.out.println("player2Color is ..." + player2Color);
//		System.out.println("player2Name is ...." + player2Name);
		
		
		
		int isCTurn = HUMAN;
		if(this.currentTurn == RED_DISK) {
			//here is RED
			if(player1Color == RED_DISK && player1Name.equals("COMPUTER_PLAYER")) {
				isCTurn = PLAYER_1;
			}
			
			if(player2Color == RED_DISK && player2Name.equals("COMPUTER_PLAYER")) {
				isCTurn = PLAYER_2;
			}
			
		} else {
			//here is YELLOW
			if(player1Color == YEL_DISK && player1Name.equals("COMPUTER_PLAYER")) {
				isCTurn = PLAYER_1;
			}
			
			if(player2Color == YEL_DISK && player2Name.equals("COMPUTER_PLAYER")) {
				isCTurn = PLAYER_2;
			}
		}
		
//		System.out.println("At End seeIfComputerMove()....");
//		System.out.println("isCTurn is ........" + isCTurn);
		
		return isCTurn;
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		// MOUSE CLICKED PLAYS THE GAME
		
		if(startGame) {
				if(!winGameOver) {
					//Check to see if you run out of moves.
					if(moveCount >= Board.ROWS * Board.COLS) {
						endOnTieGame();
						
					} else {
						//if you are a computer mover ....
						// we do somethiing
							
						//FIGURE OUT WHETHER ITS A COMPUTER MOVE TURN OR NOT....
						int computerOrHumanMove = seeIfComputerMove();
						if(computerOrHumanMove != HUMAN) {
							//do computer move ....but for what player..
							//doComputerMove(computerOrHumanMove);
							System.out.println("Probably an error since computer move happened on a click");
						}  else {
						    //HUMANS!!!!!!
							//otherwise....You are a HUMAN
							int whichCol = getClickedColumn(e.getX());
							
							 if(whichCol >= 0) {
								int whichRow = placeDisk(whichCol);
								
								if(whichRow < 0) {
									if(currentTurn == RED_DISK) {
										JOptionPane.showMessageDialog(this, "Column is full", 
												"Problem", JOptionPane.INFORMATION_MESSAGE, smallIconRed);
									} else {
										JOptionPane.showMessageDialog(this, "Column is full", 
												"Problem", JOptionPane.INFORMATION_MESSAGE, smallIconYel);
									}
								} else {
									//Here stone placement was successful--so go on to next turn
									//  check here to see if you win
									//whichRow and whichCol   
									moveCount++;   //count moves on HUMAN-Player Side
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
											"Problem", JOptionPane.INFORMATION_MESSAGE, this.smallIconRed);
								} else {
									JOptionPane.showMessageDialog(this, "You can't place a disk here", 
											"Problem", JOptionPane.INFORMATION_MESSAGE, this.smallIconYel);
								}
							}
						}
						
						
						//Down here, humans have done a move...
						repaint();	
						//this.paintImmediately(0, 0, this.boardW, this.boardH);
						//after the paint you have to figure if its computer move or not...
						
						if(moveCount >= Board.ROWS * Board.COLS) {
							endOnTieGame();
							
						} else {
							computerOrHumanMove = seeIfComputerMove();
							if(computerOrHumanMove != HUMAN) {
								//do computer move ....but for what player..
								//this.paintImmediately(0, 0, this.boardW, this.boardH);
								//sleepForAMove();
								//doComputerMove(computerOrHumanMove);
								//private boolean lockedForComputerMove = false;
								//private int lockedForNextComputerMove = NO;
								lockedFindNextComputerMove = computerOrHumanMove;
								
							}
						}
					} 
					
				}
		} else {
			JOptionPane.showMessageDialog(this, "Click the \"Start/Reset\" button to begin a game.", 
					"Problem", JOptionPane.INFORMATION_MESSAGE, this.smallBoardIcon);
		}
	}
	
	private void endOnTieGame() {
		JOptionPane.showMessageDialog(this, "I'm Sorry...It's a Tie");
		startGame=false;
		
	}
	
	
	private void resetMatrix() {
		for(int row = 0; row < ROWS; row++) {
			for(int col = 0; col < COLS; col++) {
				matrix[row][col] = 0;
			}
		}
	}
	
	private void resetGameVariables() {
		currentTurn = player1Color;
		startGame = true;
		drawPlayerNames = true;
		winGameOver = false;
		moveCount = 0;
		repaint();
	}

	private void doRestart() {
	
		resetMatrix();
		resetGameVariables();
		
		int computerOrHumanMove = seeIfComputerMove();
		if(computerOrHumanMove != HUMAN) {
			//do computer move ....but for what player..
			//this.paintImmediately(0, 0, this.boardW, this.boardH);
			//sleepForAMove();
			//doComputerMove(computerOrHumanMove);
			lockedFindNextComputerMove = computerOrHumanMove;
		} 
		
		
	}
	
	public void makeTheComputerMove() {
		
		//This is triggered to do a move with a wait using repaint()
		if(lockedToDrawNextComputerMove) {
			int whichRow = placeDisk(moveToDo[Board.A_COL]);
			System.out.println("In MakeTheComputerMove, Back from placeDisk() and whichRow is " + whichRow);
		
			if(whichRow != moveToDo[Board.A_ROW]) {
				if(currentTurn == RED_DISK) {
					System.out.println("ERROR with CMG for Red Player....in makeTheComputerMove" );
				} else {
					System.out.println("ERROR with CMG for Yellow Player...." );
				}
			} else {
				//Here stone placement was successful--so go on to next turn
				//  check here to see if you win  
				repaint();
				moveCount++;   //count moves on HUMAN-Player Side
				winGameOver = checkWin_(moveToDo[Board.A_ROW], moveToDo[Board.A_COL]);
				System.out.println("In doComputerMove() back from checkWin_");
				System.out.println("winGameOver is " + winGameOver);
				if(winGameOver == false) {
					
					if(moveCount >= Board.ROWS * Board.COLS) {
						endOnTieGame();
						
					} else {
						changeTurn();
						lockedToDrawNextComputerMove=false;
						moveToDo = null;
						
						// This is for the possibility of a second computer move....
						int computerOrHumanMove = seeIfComputerMove();
						if(computerOrHumanMove != HUMAN) {
							//do computer move ....but for what player..
							//System.out.println("In makeTheComputerMove() bottom --we are making second computer move");
							//this.paintImmediately(0, 0, boardW, boardH);
							repaint();
							//doComputerMove(computerOrHumanMove);
							this.lockedFindNextComputerMove = computerOrHumanMove;
							
						} 
					}
				} else {			
					Point[] winPoints = new Point[winMoveList.size()]; 
					winPoints = winMoveList.toArray(winPoints); 
			  
					System.out.print("The final winning points " );
			        for (Point x : winPoints) System.out.print(x + " "); 
			        System.out.println();	
				}
				
			}
		}
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//This is for button processing -- the START BUTTON
		if("START_RESET".equals(e.getActionCommand())) {
			//System.out.println("The Start/Reset Button says, \"Hello\"");
			doStartReset();
		}
		
		//This is for the New Game Button
		if("NEW_GAME".equals(e.getActionCommand())) {
			//System.out.println("The New Game Button says, \"Hello\"");
			doRestart();
		}
		
		//doMoveTimer.setActionCommand("DO_MOVE_TIMER");
		//nextMoveTimer.setActionCommand("NEXT_MOVE_TIMER");
		
		if("DO_MOVE_TIMER".equals(e.getActionCommand())){
			System.out.println("Make the Move Now -- DO MOVE");
			System.out.println("LockedForComputerMove " +  lockedToDrawNextComputerMove);
			if(this.lockedToDrawNextComputerMove) {
				System.out.println("In ActionPerformed we should be drawing...");
				drawTheNextComputerMove();
			}
		}
		
		if("NEXT_MOVE_TIMER".equals(e.getActionCommand())){
			System.out.println("Set Up for Next Move-- NEXT MOVE");
			System.out.println("LockedForComputerMove " +  lockedToDrawNextComputerMove);
			if(this.lockedFindNextComputerMove > Board.NO) {
				setTheNextComputerMove(lockedFindNextComputerMove);
				lockedFindNextComputerMove = Board.NO;
				this.lockedToDrawNextComputerMove = true;
				
			}
			
		}
	}
	
	//Things that help computer move generation....
	//Allows a computer move generator to see the current game matrix
	
	@Override
	public int getGameMoveCount() {
		return moveCount;
	}

	@Override
	public int[][] getMatrix() {
		// TODO Auto-generated method stub
		return matrix;
	}	
	
}
