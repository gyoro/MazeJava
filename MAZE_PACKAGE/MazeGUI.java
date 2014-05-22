//Name	Matthew Dayley
//Date	4/24/2012
//Assignment 13
//IDE	Eclipse

package MAZE_PACKAGE;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class MazeGUI extends JFrame
{
	private Container pane;

	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private static final int STARTING_W = 15;
	private static final int STARTING_H = 10;
	
	private Maze maze;
	private JLabel widthL,heightL;
	private JTextField widthTF,heightTF;
	private JButton mazeB,restartB;
	private JPanel controlPanel;
	private GUIHandler guiHandler;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu trailMenu,colorMenu;
	
	// NOTE:  color_strings[] and colors[] need to have a one to one ratio between them
	//		(they need to be the same size and have ordering such that colors[i] corresponds to color_strings[i])
	private String[] color_strings = {"Red","Orange","Blue","Yellow","Green","Blue","Purple","Pink","Fancy Blue"};
	
	private Color[] colors = {Color.red, Color.orange, Color.blue,Color.yellow,Color.green,
								Color.blue,Color.magenta,Color.pink,new Color(34,153,255)};
	
	private JMenuItem[] colorMenuItems = new JMenuItem[color_strings.length];
	
	private JMenuItem noTrail,dottedTrail,lineTrail;


	// CONSTRUCTOR BEING A WORKHORSE LIKE A BOSS
	// It puts everything together
	public MazeGUI()
	{
		setTitle("Random Maze Generator!!!");
		pane = getContentPane();
		pane.setBackground(new Color(220,220,220));
		pane.setLayout(new BorderLayout(10,10));
		
		maze = new Maze(STARTING_W,STARTING_H);
		
		widthL = new JLabel("Width: ");
		heightL = new JLabel("Height: ");
		mazeB = new JButton("Generate new maze");
		restartB = new JButton("Return to beginning");
		guiHandler = new GUIHandler();
		mazeB.addActionListener(guiHandler);
		restartB.addActionListener(guiHandler);
		
		widthTF = new JTextField(3);
		heightTF = new JTextField(3);
		widthTF.addActionListener(guiHandler);
		heightTF.addActionListener(guiHandler);
		widthTF.setText(String.valueOf(STARTING_W));
		heightTF.setText(String.valueOf(STARTING_H));
		
		// For quicker user input
		widthTF.addFocusListener( new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						widthTF.selectAll();              
					}
				});
			}
		});
		heightTF.addFocusListener( new FocusAdapter() {
			public void focusGained(FocusEvent evt) {
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						heightTF.selectAll();              
					}
				});
			}
		});
		
		controlPanel = new JPanel();
		controlPanel.setBackground(new Color(220,220,220));
		controlPanel.add(widthL);
		controlPanel.add(widthTF);
		controlPanel.add(heightL);
		controlPanel.add(heightTF);
		controlPanel.add(mazeB);
		controlPanel.add(restartB);
		
		colorMenu = new JMenu("Character Color");
		trailMenu = new JMenu("Character Trail");
		menuBar.add(colorMenu);
		menuBar.add(trailMenu);

		// Color menu things
		for(int i = 0 ; i < colorMenuItems.length ; ++i)
		{
			colorMenuItems[i] = new JMenuItem(color_strings[i]);
			colorMenu.add(colorMenuItems[i]);
			colorMenuItems[i].addActionListener(guiHandler);
		}
		
		// Trail menu things
		noTrail = new JMenuItem("No Trail");
		dottedTrail = new JMenuItem("Bread Crumb");
		lineTrail = new JMenuItem("Line");
		noTrail.addActionListener(guiHandler);
		dottedTrail.addActionListener(guiHandler);
		lineTrail.addActionListener(guiHandler);
		trailMenu.add(noTrail);
		trailMenu.add(dottedTrail);
		trailMenu.add(lineTrail);
		
		// Finish adding this stuff in
		setJMenuBar(menuBar);
		pane.add(maze,BorderLayout.CENTER);
		pane.add(controlPanel,BorderLayout.SOUTH);
		
		pack();

		setSize(WIDTH,HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}


	// Handles events
	private class GUIHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Make a new maze if button is clicked or enter is pressed in the text-boxes
			if(e.getSource() == mazeB || e.getSource() == heightTF || e.getSource() == widthTF)	
			{
				int width,height;

				//Don't continue if there is no input
				if(widthTF.getText().length() == 0 || heightTF.getText().length() == 0)
					return;

				// Get user input
				width = Integer.parseInt(widthTF.getText());
				height = Integer.parseInt(heightTF.getText());

				pane.remove(maze);
				// To preserve player's previous settings, I gotta grab what they were using
				Color colorTmp = maze.getPlayerColor();
				int trailTmp = maze.getPlayerTrails();
				maze = new Maze(width,height);
				maze.setPlayerColor(colorTmp);
				maze.setPlayerTrails(trailTmp);
				widthTF.setText(String.valueOf(maze.getW()));	//Update gui to hold current maze width
				heightTF.setText(String.valueOf(maze.getH()));	//Update gui to hold current maze height
				maze.repaint();
				pane.add(maze,BorderLayout.CENTER);
				pane.validate();
				pane.repaint();
			}
			
			// Start current maze from beginning
			if(e.getSource() == restartB)	
				maze.restart();
			
			// For color menu-bar 
			// REMEMBER, colors[] should be same size as color_strings[]
			for(int i = 0 ; i < color_strings.length ; ++i)
			{
				if(e.getActionCommand() == color_strings[i])
				{
					maze.setPlayerColor(colors[i]);
					maze.repaint();
				}
			}
			
			// For trail selection
			if(e.getSource() == noTrail)
			{
				maze.setPlayerTrails(0);
				maze.repaint();
			}
			if(e.getSource() == dottedTrail)
			{
				maze.setPlayerTrails(1);
				maze.repaint();
			}
			if(e.getSource() == lineTrail)
			{
				maze.setPlayerTrails(2);
				maze.repaint();
			}
		}
	}


	// MAIN!!
	public static void main(String[] args)
	{
		MazeGUI guiObj = new MazeGUI();
	}
}
