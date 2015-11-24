import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/*import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
//import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;*/


/**
 * Class for displaying and updating an ant colony simulation.
 * Another class should be used to control the simulation
 * by calling the methods of this class.
 * 
 * @author David Holland
 */
@SuppressWarnings("serial")
public class Ants extends JPanel
{
	
	public static enum Pattern // Create an enumaration
	{
		Clear, Random, Filled;
	}
	
	public static enum Tile // Create an enumeration with the option of the cell containing these elements
	{
		OBSTACLE, NEST, GOAL, CLEAR;
	}
	
	private Tile tile = Tile.GOAL; // Set the type of Tile { (Obstacle/Blocked) ; (Nest/Source of Ants) ; (Goal/cell, which can be the goal of a movement) ; (Clear/a normal clear cell) } 
	
	int rows = 50; // Setting row count (can be changed)
	int columns = 50; // Setting column count (can be changed)
	public Cell[][] cellArray = new Cell[columns][rows]; // Initialize a two-dimensional Cell Array, which contains all the cells pf the game (Initialize with the row & col count) 
	
	private int maxAnts = 100; // Set the maximum amount of ants to be represented in the nest
	public List<Ant> ants = new ArrayList<Ant>(); // Create a List<?> with the the type of Ant (basic constructor of an Ant). This list contains all ants represented on the game field, except the nest
	
	private Set<Cell> nests = new HashSet<Cell>(); // Create a Set<?> of Cells, which represent the nests
	private Set<Cell> food = new HashSet<Cell>(); // Create a Set<?> of Cells, which represent the food points
	
	AdvancedControlPanel advancedControlPanel; // Create an advanced control panel at the bottom, which can be expanded by a checkbox
	
	final JInternalFrame aboutFrame = new JInternalFrame("About", false, true);
	final JInternalFrame messageFrame = new JInternalFrame("Getting Started", false, true);
	
	public int repaint = 10;
	
	
	/**
	 * Main initialization of the JPanel 
	 */
	public Ants()
	{
		
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		/*JLabel messageLabel1 = new JLabel(
				"Place some food and obstacles in the environment and press play,");
		JLabel messageLabel2 = new JLabel(
				"then experiment by changing the environment!");
		JLabel messageLabel3 = new JLabel(
				"For more info, see About from the Advanced panel.");
		
		messageFrame.add(messageLabel1, BorderLayout.NORTH);
		messageFrame.add(messageLabel2, BorderLayout.CENTER);
		messageFrame.add(messageLabel3, BorderLayout.SOUTH);
		
		add(messageFrame);
		
		messageFrame.pack();
		
		messageFrame.setLocation(300, 300);
		messageFrame.setVisible(true);
		JPanel aboutPanel = new JPanel();
		
		aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel(
				"Auto-adjust: Automatically adjust parameters over time."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel(
				"Deltas: How fast each parameter should adjust."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel
				.add(new JLabel(
						"Max Pheromone: The maximum pheromone allowed in the environment."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel
				.add(new JLabel("Evaporation: How fast pheromones dissipate."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel(
				"Dropoff: Pheromones get weaker further from nests or food."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel
				.add(new JLabel(
						"Trail Strength: How strictly ants follow strongest pheromones."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel(
				"Adapt Time: How fast all parameters should adjust."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel.add(new JLabel(
				"Food Needed: One: Ants must find one food before returning."));
		aboutPanel.add(Box.createVerticalStrut(10));
		aboutPanel
				.add(new JLabel(
						"Food Needed: All: Ants must find all food before returning. (TSP)"));
		aboutPanel.add(Box.createVerticalStrut(10));
		
		aboutFrame.setContentPane(aboutPanel);
		
		aboutFrame.pack();
		add(aboutFrame);
		aboutFrame.setLocation(300, 300);*/
		
		addMouseListener(new MouseAdapter() // Fired when clicking on Grid
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				
				//messageFrame.dispose();
				//aboutFrame.setVisible(false);
				
				int clickedCellColumn = (int) (((double) e.getX()) / getWidth() * columns); // Get the clicked Column
				int clickedCellRow = (int) (((double) e.getY()) / getHeight() * rows); // Get the clicked Row
				
				if (clickedCellColumn < 0 || clickedCellColumn >= columns
						|| clickedCellRow < 0 || clickedCellRow >= rows) // Invalid event fired
				{
					return;
				}
				//Disable features:
				cellArray[clickedCellColumn][clickedCellRow].setIsGoal(false); // goal ...
				food.remove(cellArray[clickedCellColumn][clickedCellRow]); // food ...
				cellArray[clickedCellColumn][clickedCellRow]
						.setIsObstacle(false); // obstacle ...
				cellArray[clickedCellColumn][clickedCellRow].setHasNest(false); // nest ...
				nests.remove(cellArray[clickedCellColumn][clickedCellRow]);
				
				if (e.getButton() == MouseEvent.BUTTON1) // If clicked with left mouse button:
				{
					
					if (Tile.OBSTACLE.equals(tile)) // If combobox is set to obstacle, ...
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setIsObstacle(true); // ... set Cell to obstacle
					}
					else if (Tile.GOAL.equals(tile)) // If combobox is set to goal, ...
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setIsGoal(true); // ... set Cell to goal
						food.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					else if (Tile.NEST.equals(tile)) // If combobox is set to nest, ...
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setHasNest(true); // ... set Cell to nest
						nests.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
				}
				
				advancedControlPanel.environmentChanged(); // invoke environmentChanged() method
				
				repaint(repaint); // invoke repaint()
			}
		});
		
		addMouseMotionListener(new MouseAdapter() // Handle dragging events
		{
			private int previousColumn = -1;
			private int previousRow = -1;
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				int clickedCellColumn = (int) (((double) e.getX()) / getWidth() * columns);
				int clickedCellRow = (int) (((double) e.getY()) / getHeight() * rows);
				
				if (clickedCellColumn < 0 || clickedCellColumn >= columns
						|| clickedCellRow < 0 || clickedCellRow >= rows)
				{
					return;
				}
				
				if (clickedCellColumn != previousColumn
						|| clickedCellRow != previousRow)
				{
					cellArray[clickedCellColumn][clickedCellRow]
							.setIsGoal(false);
					food.remove(cellArray[clickedCellColumn][clickedCellRow]);
					cellArray[clickedCellColumn][clickedCellRow]
							.setIsObstacle(false);
					cellArray[clickedCellColumn][clickedCellRow]
							.setHasNest(false);
					nests.remove(cellArray[clickedCellColumn][clickedCellRow]);
					
					if (Tile.OBSTACLE.equals(tile))
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setIsObstacle(true);
					}
					else if (Tile.GOAL.equals(tile))
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setIsGoal(true);
						food.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					else if (Tile.NEST.equals(tile))
					{
						cellArray[clickedCellColumn][clickedCellRow]
								.setHasNest(true);
						nests.add(cellArray[clickedCellColumn][clickedCellRow]);
					}
					
					advancedControlPanel.environmentChanged();
					
					repaint(repaint);
					previousColumn = clickedCellColumn;
					previousRow = clickedCellRow;
				}
			}
		});
		
		setBackground(Color.WHITE);
		
		killAllCells();
	}
	
	/**
	 * Change the grid size (JComboBox)
	 * @param columns To how many columns the grid is meant to be set
	 * @param rows To how many rows the grid is meant to be set
	 */
	public void setGridSize(int columns, int rows)
	{
		this.columns = columns;
		this.rows = rows;
		cellArray = new Cell[columns][rows];
		killAllCells();
		ants.clear();
		
		cellArray[columns / 2][rows / 2].setHasNest(true);
		nests.add(cellArray[columns / 2][rows / 2]);
		
		repaint(repaint);
	}
	
	/**
	 *  Reset all cells, but does not remove the ants NOTE: meant to be used with the Patterns JComboBox
	 */
	public void killAllCells()
	{
		nests.clear();
		food.clear();
		for (int column = 0; column < columns; column++)
		{
			for (int row = 0; row < rows; row++)
			{
				cellArray[column][row] = new Cell(column, row);
			}
		}
		if (advancedControlPanel != null)
		{
			advancedControlPanel.environmentChanged();
		}
		repaint(repaint);
	}
	
	/**
	 *  Meant to be used with the Patterns JComboBox
	 * @param newPattern The targed Pattern {Filled ; Random ; Clear (invoked at the beginning to proceed with the other options)}
	 */
	public void setPattern(Pattern newPattern) // Meant to be used with the Patterns JComboBox
	{
		
		killAllCells();
		
		if (newPattern.equals(Pattern.Filled)) // When 'Filled' selected
		{
			for (int column = 0; column < columns; column++)
			{
				for (int row = 0; row < rows; row++)
				{
					cellArray[column][row].setIsObstacle(true); // Fill everything witch obstacles
				}
			}
		}
		else if (newPattern.equals(Pattern.Random)) // When 'Random' was selected
		{
			for (int column = 0; column < columns; column++)
			{
				for (int row = 0; row < rows; row++)
				{
					if (Math.random() < .3)
					{
						cellArray[column][row].setIsObstacle(true); // Fill random cells with obstacles
					}
				}
			}
			
			cellArray[columns / 2][rows / 2].setIsObstacle(false);
			cellArray[columns / 2][rows / 2].setHasNest(true);
			nests.add(cellArray[columns / 2][rows / 2]);
		}
		
		repaint(repaint);
	}
	
	/**
	 *  The paint methode (also called with repaint())
	 */
	public void paintComponent(Graphics g) // The paint methode (also called with repaint())
	{
		
		super.paintComponent(g);
		
		g.setColor(Color.BLACK); // Paint witch black color
		
		double cellWidth = (double) getWidth() / columns; // Get the width of a single cell
		double cellHeight = (double) getHeight() / rows; // Get the height of a single cell
		
		if (columns <= 50 && rows <= 50)
		{
			
			for (int column = 0; column < columns; column++)
			{
				int cellX = (int) (cellWidth * column);
				g.drawLine(cellX, 0, cellX, getHeight());
			}
			
			for (int row = 0; row < rows; row++)
			{
				int cellY = (int) (cellHeight * row);
				g.drawLine(0, cellY, getWidth(), cellY);
			}
		}
		/*
		 * ^
		 * |
		 * 
		 * Draw the cells
		 * 
		 * |
		 * v
		 */
		for (int column = 0; column < columns; column++)
		{
			for (int row = 0; row < rows; row++)
			{
				
				int cellX = (int) (cellWidth * column);
				int cellY = (int) (cellHeight * row);
				
				int thisCellWidth = (int) (cellWidth * (column + 1) - cellX);
				int thisCellHeight = (int) (cellHeight * (row + 1) - cellY);
				
				if (cellArray[column][row].hasNest())
				{
					g.setColor(Color.ORANGE);
				}
				else if (cellArray[column][row].isGoal())
				{
					Random random = new Random(
							cellArray[column][row].hashCode());
					g.setColor(new Color(random.nextInt(256), random
							.nextInt(256), random.nextInt(256), 255));
				}
				else if (cellArray[column][row].isBlocked())
				{
					g.setColor(Color.GRAY);
				}
				else
				{
					
					double nestPheromone = Math.min(1,
							(cellArray[column][row].nestPheromoneLevel - 1)
									/ Cell.maxNestPheromoneLevel);
					double foodPheromone = 0;
					double maxFood = 0;
					Cell maxFoodCell = null;
					
					for (Cell food : getFood())
					{
						if (cellArray[column][row].getFoodPheromoneLevel(food) > maxFood)
						{
							maxFood = cellArray[column][row]
									.getFoodPheromoneLevel(food);
							maxFoodCell = food;
						}
						foodPheromone = Math.max(foodPheromone, Math.min(
								1,
								(cellArray[column][row]
										.getFoodPheromoneLevel(food) - 1)
										/ Cell.maxFoodPheromoneLevel));
					}
					
					if (nestPheromone > foodPheromone)
					{
						
						g.setColor(new Color(0, 255, 0,
								(int) (255 * nestPheromone)));
					}
					else if (maxFoodCell != null)
					{
						
						Random random = new Random(maxFoodCell.hashCode());
						g.setColor(new Color(random.nextInt(256), random
								.nextInt(256), random.nextInt(256),
								(int) (255 * foodPheromone)));
						
					}
					else
					{
						g.setColor(Color.white);
					}
				}
				g.fillRect(cellX + 1, cellY + 1,
						Math.max(thisCellWidth - 1, 1),
						Math.max(thisCellHeight - 1, 1));
			}
		}
		
		for (Ant ant : ants) // Draw the ants
		{
			int column = ant.getCol();
			int row = ant.getRow();
			int cellX = (int) (cellWidth * column);
			int cellY = (int) (cellHeight * row);
			int thisCellWidth = (int) (cellWidth * (column + 1) - cellX);
			int thisCellHeight = (int) (cellHeight * (row + 1) - cellY);
			
			if (ant.isReturningHome())
			{
				g.setColor(Color.BLUE);
			}
			else
			{
				g.setColor(Color.BLACK);
			}
			
			g.fillRect(cellX + 2, cellY + 2, thisCellWidth - 3,
					thisCellHeight - 3);
		}
	}
	
	/**
	 * @return Returns the Set<?> of Cells, which contains all food Cells
	 */
	public Set<Cell> getFood()
	{
		return food;
	}
	 /**
	  * @return Returns the Set<?> of Cells, which contains all nests
	  */
	public Set<Cell> getNests()
	{
		return nests;
	}
	
	/**
	 * Invokes a single step
	 * @see Cell
	 */
	public void step()
	{
		
		if (ants.size() < maxAnts)
		{
			if (!nests.isEmpty())
			{
				int nestIndex = (int) (nests.size() * Math.random());
				ants.add(new Ant((Cell) nests.toArray()[nestIndex], cellArray,
						this));
			}
		}
		else if (ants.size() > maxAnts) // When the ants presented at the field is higher than max, then ...
		{
			ants.remove(0); // Remove the first ant to be created
		}
		
		for (Ant ant : ants) // Also invoke for each ant, their individual step() method
		{
			ant.step(); 
		}
		
		for (int column = 0; column < columns; column++) // Furthermore invoke for all Cells their individual step() method
		{
			for (int row = 0; row < rows; row++)
			{
				cellArray[column][row].step();
			}
		}
		
		repaint(repaint); // Repaint the field
	}
	
	//public void showAboutFrame()
	//{
	//	messageFrame.dispose();
	//	aboutFrame.setVisible(true);
	//}
	
	/**
	 * Set tile add
	 * @param tile Tile type
	 */
	public void setTileToAdd(Tile tile)
	{
		this.tile = tile;
	}
	
	/**
	 * Change the maximum amount of ants
	 * @param maxAnts the maximum count of ants
	 */
	public void setMaxAnts(int maxAnts)
	{
		this.maxAnts = maxAnts;
		while (ants.size() > maxAnts)
		{
			ants.remove(0); // Remove the overflowing ants (first ants created are first ants to be removed
		}
	}
}