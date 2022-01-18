/*
 * Names: Milena Mijuskovic, Selena Zhang
 * Teacher: Mr. Benum
 * Course: ICS3UE-01
 * Due Date: January 20th, 2020 
 * Program Summary: This is the final culminating project. It consists of a game, Space Invaders, where the user presses keys to shoot
 * bullets in order to eliminate the enemies, and displays the high scores at the end.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class SpaceInvaders2
{
	static int colCounter = 0;
	static int rowCounter = 0;
	static int time = 0;
	static int currScore;
	static int shootY = -1, shootX = -1; //these keep track of the location of the bullet
	static int enemyX[] = new int [12], enemyY[] = new int [12]; // these keep track of the location of the enemies 
	static int score = 0;
	
	static boolean hide = false;
	static boolean showRules = false;
	static boolean startGame = false;
	static boolean enemyHit[] = new boolean[12];
	static boolean endGame = false;
	
	static String playerInfo = "";
	static String currPlayerName = null;
	
	static Board field;
	
	public static void main(String[] args) throws InterruptedException 
	{
		field = new Board(15,10);
		field.f.setVisible(false); //when the game first starts is it not visible because the welcome screen is shown
		
		initializeWelcomeScreen(); 
		
		while(!startGame) //stops the code to wait for user to press button, exits loop when button is pressed
		{
			System.out.print(""); //it won't work unless this is here
		}

		if(startGame && !endGame) //Starts the game by showing the board
		{
			field.f.setVisible(true); //sets the board frame to visible
			initializeCannons();
			while(!endGame) //loops while game is running
			{
				gameOver(); //checks if the game is over
				try {
					shoot(); //main game method
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.print(""); //it won't work unless this is here
			}
		}
		//when game is over
		field.f.setVisible(false);
		highScore();
		initializeEndScreen();
	}
	/**
	 * This method calculates the time as seconds and displays the time at the bottom of the game screen
	 * Pre: time < 0
	 * Post: The score and time is displayed
	 */
	public static void getTime() 
	{
		String stringTime;
		if(time%1000 == 0)
		{
			stringTime = Integer.toString(time/1000); 
			
			field.displayMessage("Time: " + stringTime + " seconds"); //displays time
		}
		currScore = time/1000;
	}
	
	/**
	 * This method checks if the bottom rows are cleared
	 * Pre: N/A
	 * Post: totalCol is increased if conditions are met
	 */
	public static void checkRows()
	{
		boolean rowGone1 = false, rowGone2 = false;
		if(enemyHit[11] && enemyHit[10] && enemyHit[9] && enemyHit[0] && !rowGone2)
		{
			totalCol = 13;
			rowGone2 = true;
		}
		if(enemyHit[7] && enemyHit[6] && enemyHit[5] && enemyHit[8] && !rowGone1 && rowGone2)
		{
			totalCol = 14;
			rowGone1 = true;
		}
	}
	
	//These variables correspond with the following methods, so they are not at the top
	static int y = 0, x = 0; //for drawing and removing aliens
	static int yCount = 0, xCount = 0;
	static int rows = 0, col = 0;
	
	static int maxLength = 7;
	static int enemyCounter = 0; 
	static int moveDown = 1;
	static int totalAliensHit = 0;
	static int totalCol = 12;
	
	/**
	 * This method draws the enemies in 4x3 blocks
	 * Pre: enemyVisible[i] is false for it to draw
	 * Post: If enemyVisible[i] is false, then an enemy is drawn
	 */
	public static void drawEnemies()
	{
		final int LENGTH = 4;
		final int WIDTH = 3; 
		final int TOTAL_ALIENS = 12;
		
		aliensHit();
		checkRows();
		
		//starts
		if(col < totalCol)
		{
			for(int i = 0; i<WIDTH; i++)
			{
				for(int j = 0; j < LENGTH; j++) //draws them row by row
				{
					enemyCounter++;
					if(enemyCounter == TOTAL_ALIENS) //if all the aliens are drawn it resets
						enemyCounter = 0;
					
					if(!enemyHit[enemyCounter]) //draws enemies only if they're not hit
					{
						field.putPeg("red", y, x);
						enemyX[enemyCounter] = y; //tracks x and y coordinates of the enemies
						enemyY[enemyCounter] = x;
					}
					x++;
				}
				x = xCount;
				y++;
			}
			y = yCount;
		}
	}
	/**
	 * This method works with drawEnemies() to make the aliens appear like they are moving across the screen
	 * Pre: N/A
	 * Post: All enemies, regardless of it being invisible, is removed
	 */
	public static void removeEnemies()
	{
		final int LENGTH = 4;
		final int WIDTH = 3; 
		final int TIMES_ACROSS = 4;
		
		checkRows();
		
		//removes 
		if(col < totalCol)
		{
			for(int i = 0; i<WIDTH; i++)
			{
				for(int j = 0; j < LENGTH; j++)
				{
					field.removePeg(y, x);
					initializeCannons(); //in case of accidental removal of cannons
					x++;
				}
				x = xCount;
				y++;
			}
			x++;
			y = yCount;
			xCount++;
			rows++;
			
			if(rows==maxLength) //if it reaches the end of a row it resets
			{
				rows=0;
				x = 0;
				if(moveDown%TIMES_ACROSS==0)
				{
					yCount++;
					col++;
				}
				y = yCount;
				xCount = 0;
			}
			moveDown++;
		}
	}
	/**
	 * This method checks whether or not to end the game
	 * Pre: totalAliensHit is equal to or greater than 0
	 * Post: The game is ended if conditions are met
	 */
	public static void gameOver()
	{
		final int TOTAL_ALIENS = 12; 
		
		if(totalAliensHit == TOTAL_ALIENS || (col == totalCol)) //if all aliens are hit or the aliens reach the bottom of the screen
		{
			endGame = true;
			startGame = false;
		}
	}
	/**
	 * This method initializes the 4 cannons (white pegs) at the bottom of the field
	 * Pre: N/A
	 * Post: 4 cannons are drawn
	 */
	public static void initializeCannons()
	{
		final int COL_START = 3, COL_END = 7;
		final int LENGTH = 14;
		for(int i = COL_START; i<COL_END ; i++) //makes 4 cannons positioned in the middle
			field.putPeg("white", LENGTH, i);
	}
	/**
	 * This method checks if the bullet peg is on the same location as an enemy peg
	 * Pre: A bullet is on the field
	 * Post: If the locations are the same, the enemy that was hit will be true.
	 */
	public static void aliensHit()
	{
		final int TOTAL_COL = 12;
		final int BOTTOM_LEFT_CORNER = 14;
		for(int k = 0; k < TOTAL_COL; k++)
		{
			if(shootX == enemyX[k]-1 && shootY == enemyY[k] && enemyHit[k] == false) //if the coordinates of an alien are the same as the bullets and the alien hasn't been hit yet
			{
				enemyHit[k] = true;				
				shootX = BOTTOM_LEFT_CORNER;
				shootY = 0;
				totalAliensHit++;
				System.out.println("Alien that was hit: " + k);
			}
		}
	}
	/**
	 * This is the main game method that is called. 
	 * Pre: N/A
	 * Post: The game runs
	 * @throws InterruptedException
	 */
	public static void shoot() throws InterruptedException
	{
		int col = 0;
		final int row = 14;
		
		//checks if the keys are pressed
		if(field.key1)
			col = 3;
		else if(field.key2)
			col = 4;
		else if(field.key3)
			col = 5;
		else if(field.key4)
			col = 6;
		if(field.key1 || field.key2 || field.key3 || field.key4) //checks if a cannon is shot
		{
			for(int i = 3; i<7; i++)
			{
				if(col == i) //finds which cannon was pressed
				{
					shootX = row-1;
					shootY = col;
					for(int j = 0; j<14; j++) //starts moving bullet upwards (length of screen is 15)
					{
						gameOver();
						drawEnemies();
						field.putPeg("yellow", shootX, shootY); //bullet is drawn
						
						if(shootX == 0 || shootX == 14) //if the bullet reaches the top of the screen (hit nothing) or if the bullet is at an x of 14 (which we purposely placed there when the bullet does hit an alien) then breaks out 
						{
							field.removePeg(shootX, shootY);
							break;
						}
						
						Thread.sleep(200); //pause in drawing and removing the bullet and aliens so it looks like they are moving
						removeEnemies();
						
						time += 200;
						getTime();
						field.removePeg(shootX, shootY);
							
						field.key1 = false; //all keys set back to false after shooting
						field.key2 = false;
						field.key3 = false;
						field.key4 = false;
						
						shootX--; //bullet x decreases so it moves upwards
					}
				}
			}	
		}
		else //if a bullet isn't shot, the same thing happens but a bullet isn't drawn
		{
			gameOver();
			drawEnemies();
			Thread.sleep(200);
			removeEnemies();
			time += 200;
			getTime();
		}
	}
	/**
	 * This method initializes the welcome screen
	 * Sources: This window was built using WindowBuilder
	 * Pre: N/A
	 * Post: The welcome window is visible
	 * @throws InterruptedException
	 */
	
	public static void initializeWelcomeScreen() throws InterruptedException 
	{
		final String NEXT = "N E X T"; // the text that is on the button
		
		//images
//		try{
//			BufferedImage welcomeStory = ImageIO.read(getClass().getResourceAsStream("SpaceInvadersWelcomeStory_FINAL.png"));
//		} catch(IOException e){
//			
//		}
		JFrame frame = new JFrame("Space Invaders - Milena Mijuskovic and Selena Zhang"); //makes the frame
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setBackground(Color.BLACK);
		frame.setResizable(false);
		frame.setSize(670,860);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton nextButton = new JButton(NEXT);
		nextButton.setFont(new Font("Courier New", Font.BOLD, 44));
		nextButton.setBounds(10, 680, 644, 59);
		frame.getContentPane().add(nextButton);
		
		JTextField nameField = new JTextField(); //takes the username
		nameField.setBounds(130, 631, 405, 38);
		frame.getContentPane().add(nameField);
		nameField.setColumns(10);
		nameField.setVisible(false); //You don't see the text field on the first screen
		
		JLabel storyLabel = new JLabel(); //label for the 1st screen
		storyLabel.setIcon(new ImageIcon("SpaceInvadersWelcomeStory_FINAL.png"));
		storyLabel.setBounds(0, 0, 654, 620);
		frame.getContentPane().add(storyLabel);
		
		JLabel rulesLabel = new JLabel(); //label for the 2nd screen
		rulesLabel.setIcon(new ImageIcon("SpaceInvadersWelcomeRules.png"));
		rulesLabel.setBounds(0, 0, 654, 620);
		frame.getContentPane().add(rulesLabel);
		rulesLabel.setVisible(false);
		
		frame.setVisible(true);
		
		//for making button work
		showRules(nextButton, nameField, rulesLabel, storyLabel); //changes the screen
		getUsername(nextButton, nameField, frame); 

	}
	/**
	 * This method initializes the end screen
	 * Sources: This window was built using WindowBuilder
	 * Pre: The game is over
	 * Post: The end screen with high scores is visible
	 */
	private static void initializeEndScreen() 
	{
		final int TOTAL_ALIENS_HIT = 12;
		boolean lost = false;
		
		JFrame frame = new JFrame("Space Invaders - Milena Mijuskovic and Selena Zhang");
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().setForeground(Color.WHITE);
		frame.setSize(670, 860);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel youWon = new JLabel();
		youWon.setIcon(new ImageIcon("YOU WON.png"));
		youWon.setBounds(0, -75, 812, 489);
		frame.getContentPane().add(youWon);
		
		JLabel youLost = new JLabel();
		youLost.setIcon(new ImageIcon("YOU LOST.png"));
		youLost.setBounds(0, -75, 812, 489);
		frame.getContentPane().add(youLost);
		
		JTextPane highScoreLabel = new JTextPane();
		highScoreLabel.setForeground(Color.WHITE);
		highScoreLabel.setFont(new Font("OCR A Extended", Font.PLAIN, 30));
		highScoreLabel.setBackground(new Color(0, 0, 0));
		highScoreLabel.setEditable(false);
		highScoreLabel.setBounds(110, 350, 486, 279);
		frame.getContentPane().add(highScoreLabel);
		
		if(totalAliensHit == TOTAL_ALIENS_HIT) //if the game is won
		{
			youWon.setVisible(true);
			youLost.setVisible(false);
		}
		else //if game is lost
		{
			youWon.setVisible(false);
			youLost.setVisible(true);
			lost = true;
		}
		if(!lost)
		{
			highScoreLabel.setText(playerInfo + "\n\n" + currPlayerName.toUpperCase() + "'S SCORE: " + currScore);
		}
		else
		{
			currScore += 1000;
			highScoreLabel.setText(playerInfo + "\n" + "You lost. No score for you.");
		}
		
		frame.setVisible(true);
	}
	/**
	 * This method gets and stores the player's username
	 * Pre: The user has entered a username
	 * Post: The username is stored
	 * @param nextButton
	 * @param nameField
	 * @param frame
	 * @throws InterruptedException
	 */
	public static void getUsername(JButton nextButton, JTextField nameField, JFrame frame) throws InterruptedException
	{
		nextButton.addActionListener(new ActionListener() //if the user presses "Start"
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				currPlayerName = nameField.getText();
				if(!currPlayerName.equals("")) //makes sure there is a name before the game starts
				{
					startGame = true;
					if(showRules)
					{
						startGame = true;
						frame.setVisible(false); //sets the welcome screen to false so the game starts
					}
				}
			}
		});
	}
	/**
	 * This method reads and writes from a file and sorts the scores
	 * Source: ICS3U Moodle and examples in the ICS3UE Desktop resources
	 * Pre: The game has ended
	 * Post: The top 5 scores are stored in a file
	 */
	public static void highScore()
	{
		final int TOTAL_ALIENS = 12;
		File highScoresFile = new File("highScores.txt");
		PrintWriter output = null; //write to a file
		Scanner input = null; //read to a file
		String username[] = new String [5];
		int score[] = new int[5];
		
		String sortName;
		int sortScore;
		 
		//creates file
        if (!highScoresFile.exists()) 
        {
       	 try {
           	 highScoresFile.createNewFile();
           	 System.out.println("New file created.");
            } catch (IOException e) {
                System.out.println("File could not be created.");
                System.err.println("IOException: " + e.getMessage()); 
            }
        }
        
        //creates reader
        try{
       	 input = new Scanner(highScoresFile);
        }
        catch (FileNotFoundException e){
        }
       
       
        //reads from file
        for(int i = 0; i<=4; i++)
        {
       	 username[i] = input.nextLine();
       	 score[i] = input.nextInt();
       	 input.nextLine();
        }
        
        if(totalAliensHit != TOTAL_ALIENS) //if the game is lost
        	currScore +=1000; //adds a high number to their score so it won't get sorted
        
        //sorting
        if(currScore < score[4]) //replaces the 5th player's info with the current player's info if current player score is lower than 5th person's
        {
        	score[4] = currScore;
        	username[4] = currPlayerName;
        }
        for (int i = 0; i < 5; i++) 
        {
            for (int j = i + 1; j < 5; j++) 
            {
                if (score[i]>score[j])
                {
                    sortScore = score[i];
                    score[i] = score[j];
                    score[j] = sortScore;
                    
                    sortName = username[i];
                    username[i] = username[j];
                    username[j] = sortName;
                }
            }
        }
        
        //created writer
        try {
			output = new PrintWriter(highScoresFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        //writes to file
       for(int i = 0; i<5; i++)
       {
	       	output.println(username[i]);
	       	output.println(score[i]);
	       	playerInfo += i+1 + ". " + username[i] + "\t\t\t\t" + score[i] + "\n";
	       		       
       }
        output.close();
	}
	/**
	 * This method is used in the welcome window to change the screen to display the rules
	 * Pre: The "next" button is pressed
	 * Post: The rules are displayed
	 * @param nextButton
	 * @param nameField
	 * @param rulesLabel
	 * @param storyLabel
	 * @throws InterruptedException
	 */
	public static void showRules(JButton nextButton, JTextField nameField, JLabel rulesLabel, JLabel storyLabel) throws InterruptedException
	{
		nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				final String START = "S T A R T";
				showRules = true;
				if(showRules)
				{
					nameField.setVisible(true);
					rulesLabel.setVisible(true);
					storyLabel.setVisible(false);
					nextButton.setText(START);
				}
			}
		});
	}
}