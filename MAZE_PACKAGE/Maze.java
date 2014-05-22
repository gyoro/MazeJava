// -Generates a random maze in a JPanel with a red dot representing a player
// -Player can move using arrow keys on the keyboard
// -Player may get stuck if JPanel loses focus
// -One constructor, takes two integers, width and height

package MAZE_PACKAGE;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;



public class Maze extends JPanel implements KeyListener
{
	//CONSTANTS
	public static final int MAX_WIDTH = 120;		// Measured in maze-cells
	public static final int MAX_HEIGHT = 80;
	private final int UP = 0;
	private final int RIGHT = 1;
	private final int DOWN = 2;
	private final int LEFT = 3;
	
	//PRIVATE MEMBERS
	private DisjointSet mazeset;
	private boolean[] topwalls,rightwalls;
	private int w,h;
	private int cell_scale = 20;	// Each cell takes up n pixels -- try to keep it as an even integer
	private int player_position = 0;	// Ranges from 0 to w*h-1
	private Color player_color = Color.red;
	private int player_trails = 0;	// Let 0 = none, 1 = dotted, and 2 = line

	// position history is kept in the form <key, value> such that the key is the player's previous position
	// and the value is the direction the player went
	private Hashtable<Integer,Integer> player_position_history = new Hashtable<Integer,Integer>();

	
	
	// Return a random int
	public static int randFunc(int low, int high)	//only works from 0 to the max int, no negatives.
	{
		return (int)( Math.random()*high*10 % (high-low +1) + low );
	}
	

	// The constructor
	public Maze(int width, int height)
	{	
		w = width;
		h = height;
		
		//Keep maze size reasonable (ignore large values and negative ones)
		//This also counts as validating user input
		if(w < 2)
			w = 2;
		if(w > MAX_WIDTH)
			w = MAX_WIDTH;
		if(h < 2)
			h = 2;
		if(h > MAX_HEIGHT)
			h = MAX_HEIGHT;
		
		// Cell scaling based on maze size
		if(w <= 10 && h <= 10)
			cell_scale = 40;
		if(w > 40 || h > 30)
			cell_scale = 16;
		if(w > 60 || h > 40)
			cell_scale = 12;
		if(w > 80 || h > 50)
			cell_scale = 8;
		
		// Set things up
		setBackground(new Color(190,190,190));
		setPreferredSize(new Dimension(w*cell_scale,h*cell_scale));
		addKeyListener(this);
			
		// Get ready for generation
		mazeset = new DisjointSet(w*h);
		topwalls = new boolean[w*h];
		rightwalls = new boolean[w*h];	
		for(int i = 0 ; i < (w*h) ; ++i)
		{
			topwalls[i] = true;
			rightwalls[i] = true;
		}
		
		// Generate it
		generateMaze();
	}
	
	
	private void generateMaze()
	{
		int cell, direction, neighbor;
		
		while( mazeset.isOneSet() == false)
		{
			cell = randFunc(0,w*h-1);
			direction = randFunc(0,3);  // 0 up, 1 right, 2 down, 3 left
			neighbor = findNeighbor(cell, direction);

			if(neighbor == -1)
				continue;		// Forget invalid neighbor, and generate a new one.
		
			// Break wall IF cell and neighbor aren't in the same set.
			if(mazeset.find(cell) != mazeset.find(neighbor))
			{
				breakWall(cell, direction, neighbor);
				mazeset.unionSets( mazeset.find(cell),mazeset.find(neighbor) );  // Union of sets, being sure to merge w/ roots
			}
		}

		
	}
	
	
	private int findNeighbor(int cell, int direction)  // Returns -1 if invalid neighbor
	{
		int tmp;
		switch(direction)
		{
			case UP:
				tmp = cell - w;
				if(tmp < 0)
					return -1;
				return tmp;
				// break; is omitted due to returns

			case RIGHT:
				tmp = cell + 1;
				if((tmp % w) == 0)
					return -1;
				return tmp;

			case DOWN:
				tmp = cell + w;
				if(tmp >= (w*h))
					return -1;
				return tmp;

			case LEFT:
				tmp = cell - 1;
				if( ((tmp+1) % w) == 0)
					return -1;
				return tmp;

			default: 
				System.out.println("If you are reading this message then randFunc() has gone horribly wrong.\n\n");
				return -1;
		}

	}


	private void breakWall(int cell, int direction, int neighbor)
	{
		switch(direction)
		{
			case UP:
				topwalls[cell] = false;
				break;

			case RIGHT:
				rightwalls[cell] = false;
				break;

			case DOWN:
				topwalls[neighbor] = false;
				break;

			case LEFT:
				rightwalls[neighbor] = false;
				break;
			default: 
				System.out.println("If you are reading this message then randFunc() has gone horribly wrong.\n\n");
		}
	}
	
	
	// for toString()
	private String top_char(boolean value)  // Converts bool for top walls into proper ascii chars
	{
		if(value == false)
			return " ";
		if(value == true)
			return "_";
		return "!";		//even though all control paths already return a value, eclipse forces me to add something here
	}


	// for toString()
	private String right_char(boolean value)  // Converts bool for right walls into proper ascii chars
	{
		if(value == false)
			return " ";
		if(value == true)
			return "|";
		return "!";		//even though all control paths already return a value, eclipse forces me to add something here
	}

	
	public String toString()
	{
		String outputStr = "";
		for(int rows = 0 ; rows < h ; rows++)
		{
			outputStr += "  ";				// Align top border to accommodate left border
			for(int j = 0 ; j < w ; j++)
			{
				outputStr += top_char(topwalls[(rows*w)+j]) + " ";  // Top walls  (also top border)
			}
			outputStr += "\n";

			outputStr += "| ";				// LEFT BORDER
			for(int j = 0 ; j < w ; j++)
			{
				outputStr += " " + right_char(rightwalls[(rows*w)+j]);  // Right walls (also right border)
			}
			outputStr += "\n";
		}
		outputStr += "  ";					// Align bottom border to accomodate left border
		for(int j = 0 ; j < w ; j++)
			outputStr += "_ ";				// BOTTOM BORDER
		outputStr += "\n";
		return outputStr;
	}

	
	// Draws the maze
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// For centering the maze
		Graphics2D g2d = (Graphics2D) g;
	    g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
	    g2d.translate(-w*cell_scale / 2, -h*cell_scale / 2);
		
		g.setColor(Color.black);
		
		// Draw border--(excluding top border and right border)
		g.drawLine(0,cell_scale,0,h*cell_scale);
		g.drawLine(0, h*cell_scale, w*cell_scale-cell_scale-1, h*cell_scale);
		
		int a,b;
		// Draw top walls
		for(int i = 0 ; i < w*h ; ++i)
		{
			if(topwalls[i])
			{
				a = (i%w)*cell_scale;
				b = (i/w)*cell_scale;
				g.drawLine(a+1,b,a+cell_scale,b);
			}
		}
		
		// Draw right walls
		for(int i = 0 ; i < w*h ; ++i)
		{
			if(rightwalls[i])
			{
				a = (i%w)*cell_scale;
				b = (i/w)*cell_scale;
				g.drawLine(a+cell_scale,b,a+cell_scale,b+cell_scale);	// Draws cell_scale+1 pixels per wall
			}
		}
		
		// Draw trails
		if(player_trails == 1)	// dotted
		{
			Enumeration<Integer> positions = player_position_history.keys();
			int c;
			g.setColor(player_color);
			while(positions.hasMoreElements())
			{
				c = positions.nextElement();
				a = (c%w)*cell_scale;
				b = (c/w)*cell_scale;
				// Location is of dots are approximate, not pixel perfect
				g.fillArc(a+(int)Math.round(cell_scale/2.7), b+(int)Math.round(cell_scale/2.7), cell_scale/3, cell_scale/3, 0, 360);
			}
			//fun statement to debug internal trail representation
			//	System.out.println(player_position_history);
		}
		
		if(player_trails == 2)	// line
		{
			Enumeration<Integer> positions = player_position_history.keys();
			int c;
			g.setColor(player_color);
			while(positions.hasMoreElements())
			{
				c = positions.nextElement();
				a = (c%w)*cell_scale;
				b = (c/w)*cell_scale;

				int direction = player_position_history.get(c);
				int mid_dist = cell_scale/2;
				switch(direction)		// IS IT OK FOR YOU TO HAVE A SWITCH STATEMENT INSIDE OF A LOOP???????
				{
					case UP:
						g.drawLine(a+mid_dist,b+mid_dist,a+mid_dist,b-mid_dist);
						break;
					case RIGHT:
						g.drawLine(a+mid_dist,b+mid_dist,a+mid_dist+cell_scale,b+mid_dist);
						break;
					case DOWN:
						g.drawLine(a+mid_dist,b+mid_dist,a+mid_dist,b+mid_dist+cell_scale);
						break;
					case LEFT:
						g.drawLine(a+mid_dist,b+mid_dist,a-mid_dist,b+mid_dist);
						break;
					default:
						System.out.println("Problem during line-trail drawing");
						System.exit(0);
				}
			}
		}
		
		// Draw player
		a = (player_position%w)*cell_scale;
		b = (player_position/w)*cell_scale;
		g.setColor(player_color);
		// Location of dot is approximate, not pixel perfect
		g.fillArc(a+(int)Math.round(cell_scale/4.0), b+(int)Math.round(cell_scale/4.0), cell_scale/2, cell_scale/2, 0, 360);
		g.setColor(Color.black);
		g.drawArc(a+(int)Math.round(cell_scale/4.0), b+(int)Math.round(cell_scale/4.0), cell_scale/2, cell_scale/2, 0, 360);
		
		requestFocus();	//for keyboard input to work
	}
	

	// Move player to start
	public void restart()
	{
		player_position = 0;
		player_position_history.clear();	// It doesn't mind being cleared when empty.  Nice.
		repaint();
	}
	
	
	// For arrow key movement of player
	// Note:  Player can only win with a downward move
	public void keyPressed(KeyEvent e) 
	{
	    int keyCode = e.getKeyCode();
	    
	    int prev_player_position;	// Used to keep track of player trails
	    
	    switch( keyCode ) 
	    { 
	        case KeyEvent.VK_UP:
	        	if(findNeighbor(player_position,UP) != -1 && !topwalls[player_position]) // if valid move
	        	{
	        		// Update player position
	        		prev_player_position = player_position;	// need to grab this first though
	        		player_position = findNeighbor(player_position,UP);
	        		
	        		// Keep track of path (regardless of whether or not it is used)
		    	    if(player_position_history.containsKey(player_position))
		    	    {
		    	    	player_position_history.remove(player_position);
		    	    }
		    	    else
		    	    	player_position_history.put(prev_player_position,UP);
		    	    
	        		repaint();
	        	}
	            break;
	            
	        case KeyEvent.VK_RIGHT :
	        	if(findNeighbor(player_position,RIGHT) != -1 && !rightwalls[player_position]) // if valid move
	        	{
	        		// Update player position
	        		prev_player_position = player_position;	// need to grab this first though
	        		player_position = findNeighbor(player_position,RIGHT);
	        		
	        		// Keep track of path (regardless of whether or not it is used)
		    	    if(player_position_history.containsKey(player_position))
		    	    {
		    	    	player_position_history.remove(player_position);
		    	    }
		    	    else
		    	    	player_position_history.put(prev_player_position,RIGHT);
		    	    
	        		repaint();
	        	}
	        	break;
	        	
	        case KeyEvent.VK_DOWN:
	        	// Has the player won?
	        	if(player_position == w*h-1)	
	        	{
	        		player_position = player_position + w;
	        		repaint();
	        		JOptionPane.showMessageDialog(null, "Congratulations!","You reached the end.",-1);
	        		restart();
	        		return;
	        	}
	        	// If player hasn't won, it's a regular move
	        	else if(findNeighbor(player_position,DOWN) != -1 && !topwalls[findNeighbor(player_position,DOWN)]) // if valid move
	        	{
	        		// Update player position
	        		prev_player_position = player_position;	// need to grab this first though
	        		player_position = findNeighbor(player_position,DOWN);
	        		
	        		// Keep track of path (regardless of whether or not it is used)
		    	    if(player_position_history.containsKey(player_position))
		    	    {
		    	    	player_position_history.remove(player_position);
		    	    }
		    	    else
		    	    	player_position_history.put(prev_player_position,DOWN);
		    	    
	        		repaint();
	        	}
	            break;
	            
	        case KeyEvent.VK_LEFT:
	        	if(findNeighbor(player_position,LEFT) != -1 && !rightwalls[findNeighbor(player_position,LEFT)])	// if valid move
	        	{
	        		// Update player position
	        		prev_player_position = player_position;	// need to grab this first though
	        		player_position = findNeighbor(player_position,LEFT);
	        		
	        		// Keep track of path (regardless of whether or not it is used)
		    	    if(player_position_history.containsKey(player_position))
		    	    {
		    	    	player_position_history.remove(player_position);
		    	    }
		    	    else
		    	    	player_position_history.put(prev_player_position,LEFT);
		    	    
	        		repaint();
	        	}
	            break;
	            
	     }
	} 
	public void keyReleased( KeyEvent e ) { }	// Required by KeyListener interface
	public void keyTyped( KeyEvent e ) { }		// Required by KeyListener interface

	
	//SETTERS AND GETTERS
	public int getW()
	{
		return w;
	}
	
	public int getH()
	{
		return h;
	}
	
	public Color getPlayerColor()
	{
		return player_color;
	}
	
	public int getPlayerTrails()
	{
		return player_trails;
	}
	
	public void setPlayerTrails(int x)
	{
		player_trails = x;
	}
	
	public void setPlayerColor(Color x)
	{
		player_color = x;
	}
	
	
	// For testing
	public static void main(String[] args)
	{
		Maze maze = new Maze(3,3);
		System.out.print(maze);
	}
}
