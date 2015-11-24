import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Ant agent that interacts with an ant colony simulation evironment.
 * @author David Holland
 */
public class Ant
{
	
	Set<Cell> foodFound = new HashSet<Cell>(); // Used for find all option
	
	public static double dropoffRate = .9; // Define dropoffRate. Can be changed later in advanced control panel
	public static double bestCellNext = 0.5; // find best cell ???
	public static boolean allFoodRequired = false; // Define whether ants have to collect all food. Can be changed over the the comboboxes
	
	private int x;
	/*		^
	 * Coordinates
	 * 		v	*/
	private int y;
	
	private boolean returnToNest; // Is the ant returning to the nest?
	
	Cell[][] world; // The outer world (Grid of Ants.java)
	
	double maxPheromone = 100.0; //The maxiumum pheromone level, which the ant can drop/recieve???
	
	int steps = 0; // How many steps???
	
	private Ants ants; // Calls the methods of the Ants class
	
	
	double maxFoodSoFar;
	double maxNestSoFar;
	List<Cell> maxFoodCells;
	List<Cell> allNeighborCells;
	double totalNeighborPheromones;
	Map<Cell, Double> maxFoodSoFarMap;
	
	int prevx;
	int prevy;

	/**
	 * When creating a new ant the starting coordinates (nest location) and other values will be set
	 * @param startCell The nest location (<Cell>)
	 * @param world The grid has to be given to this method
	 * @param ants Giving ants for method calling
	 */
	public Ant(Cell startCell, Cell[][] world, Ants ants)
	{
		this.x = startCell.c;
		this.y = startCell.r;
		this.world = world;
		this.ants = ants;
	}
	
	/**
	 * Let an ant die
	 */
	public void die()
	{
		returnToNest = false;
		steps = 0;
		foodFound.clear();
		Set<Cell> nests = ants.getNests();
		if (!nests.isEmpty())
		{
			int nestIndex = (int) (nests.size() * Math.random());
			Cell nest = (Cell) nests.toArray()[nestIndex];
			x = nest.c;
			y = nest.r;
			prevx = x;
			prevy = y;
		}
	}
	
	/**
	 * Make a step
	 */
	
	public boolean isBackwards(Cell planned)
	{
		return (planned.c == prevx && planned.r == prevy && steps != 0);
	}
	
	public void loopR (int c, int r)
	{
		//don't count yourself
		if (c == 0 && r == 0)
		{
			return;
		}
		else if (y + r < 0 || y + r >= world[0].length) // World border
		{
			return;
		}
		
		if (!world[x + c][y + r].isBlocked()/* && !isBackwards(world[x + c][y + r])*/) // When no obstacle (impossible move)
		{
			
			allNeighborCells.add(world[x + c][y + r]); // Add it to the theoretical possible cells
			
			if (maxFoodSoFar == 0) // When no food
			{
				maxFoodCells.add(world[x + c][y + r]); // Add it to the list of possible food cells
			}
			
			for (Cell food : foodFound) // Iterate through the list of food-cells ; save value in 'food'
			{
				
				if (!maxFoodSoFarMap.containsKey(food) // Execute only, if one of the nearby cells is not a food-cell 
						|| world[x + c][y + r] // or the nearby cells pheromone level greater than the pheromone level of the nearby food cell is
								.getFoodPheromoneLevel(food) > maxFoodSoFarMap
								.get(food))
				{
					maxFoodSoFarMap.put(food, world[x + c][y + r]
							.getFoodPheromoneLevel(food));
				}
				
			}
			
			if (world[x][y].isGoal()) // Wenn
			{
				maxFoodSoFarMap.put(world[x][y],
						Cell.maxFoodPheromoneLevel);
			}
			
			for (Cell food : foodFound)
			{
				world[x][y]
						.setFoodPheromone(food,
								maxFoodSoFarMap.get(food)
										* Ant.dropoffRate);
			}
			
			if (world[x + c][y + r].getNestPheromoneLevel() > maxNestSoFar)
			{
				maxNestSoFar = world[x + c][y + r]
						.getNestPheromoneLevel();
			}
			
			if (ants.getFood().isEmpty())
			{
				totalNeighborPheromones += 1;
			}
			else
			{
				for (Cell food : ants.getFood())
				{
					
					if (foodFound.contains(food))
					{
						continue;
					}
					totalNeighborPheromones += world[x + c][y + r]
							.getFoodPheromoneLevel(food);
					
					if (world[x + c][y + r]
							.getFoodPheromoneLevel(food) > maxFoodSoFar)
					{
						maxFoodSoFar = world[x + c][y + r]
								.getFoodPheromoneLevel(food);
						maxFoodCells.clear();
						maxFoodCells.add(world[x + c][y + r]);
					}
					else if (world[x + c][y + r]
							.getFoodPheromoneLevel(food) == maxFoodSoFar)
					{
						maxFoodCells.add(world[x + c][y + r]);
					}
				}
			}
		}
	}
	
	public void loopC (int c, int r)
	{
		if (x + c < 0 || x + c >= world.length) //
		{
			return;
		}
		
		loopR(c,r);
	}
	
	public void step()
	{
		

				double chanceToTakeBest = Math.random();
		steps++; // A step is going to be made so increase the steps
		
		foodFound.retainAll(ants.getFood()); // Lookup if the ant has found all food cells. Only needed, when 'all' is marked 
		
		if (!returnToNest) //If looking for food 
		{
			if (world[x][y].isGoal())
			{
				foodFound.add(world[x][y]);
				if (Ant.allFoodRequired)
				{
					if (foodFound.size() >= ants.getFood().size())
					{
						steps = 0;
						returnToNest = true;
						return;
					}
				}
				else
				{
					steps = 0;
					returnToNest = true;
					return;
				}
			}
			else if (world[x][y].hasNest())
			{
				if (steps > 1)
				{
					die();
					return;
				}
			}
			
			maxFoodSoFar = 0;
			maxNestSoFar = 0;
			maxFoodCells = new ArrayList<Cell>();
			allNeighborCells = new ArrayList<Cell>();
			totalNeighborPheromones = 0;
			maxFoodSoFarMap = new HashMap<Cell, Double>();
			
			int c = -1;
			int r = 0;
			loopC(c,r);
			
			c = 0;
			r = -1;
			loopC(c,r);
			
			c = 0;
			r = 0;
			loopC(c,r);
			
			c = 0;
			r = 1;
			loopC(c,r);
			
			c = 1;
			r = 0;
			loopC(c,r);
			
			prevx = x;
			prevy = y;
			
			if (world[x][y].hasNest())
			{
				maxNestSoFar = Cell.maxNestPheromoneLevel;
			}
			
			world[x][y].setNestPheromone(maxNestSoFar * Ant.dropoffRate);
			
			if (Ant.bestCellNext > chanceToTakeBest)
			{
				if (!maxFoodCells.isEmpty())
				{
					int cellIndex = (int) (maxFoodCells.size() * Math.random());
					Cell bestCellSoFar = maxFoodCells.get(cellIndex);
					
					x = bestCellSoFar.c;
					y = bestCellSoFar.r;
				}
			}
			else
			{ //give cells chance based on pheremone
				double pheremonesSoFar = 0;
				double goalPheromoneLevel = totalNeighborPheromones
						* Math.random();
				
				for (Cell neighbor : allNeighborCells)
				{
					if (ants.getFood().isEmpty())
					{
						pheremonesSoFar += 1;
						if (pheremonesSoFar > goalPheromoneLevel)
						{
							
							x = neighbor.c;
							y = neighbor.r;
							break;
						}
					}
					else
					{
						
						for (Cell food : ants.getFood())
						{
							if (foodFound.contains(food))
							{
								continue;
							}
							pheremonesSoFar += neighbor
									.getFoodPheromoneLevel(food);
							if (pheremonesSoFar > goalPheromoneLevel)
							{
								
								x = neighbor.c;
								y = neighbor.r;
								return;
							}
						}
					}
				}
			}
		}	
		else // If ant is returning to nest, then ...
		{
			if (world[x][y].hasNest()) // When ant reaches a field with a nest on it, destroy the ant. CAN BE CHANGED WHEN WANTING TO ALTERNATE LIVING TIME
			{
				die();
			}
			else // When on way home
			{
				double maxNestSoFar = 0;
				Map<Cell, Double> maxFoodSoFarMap = new HashMap<Cell, Double>();
				List<Cell> maxNestCells = new ArrayList<Cell>();
				List<Cell> allNeighborCells = new ArrayList<Cell>();
				double totalNeighborPheromones = 0;
				
				/*int c;
				int r;
				
				c = 0;
				r = -1;
				checkC(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);
				
				c = -1;
				r = 0;
				checkC(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);
				
				c = 0;
				r = 0;
				checkC(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);
				
				c = 1;
				r = 0;
				checkC(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);
				
				c = 0;
				r = 1;
				checkC(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);*/
				for (int c = -1; c <= 1; c++) // 'c' stands for column ; look one field left, on the field and one field right
				{
					
					if (x + c < 0 || x + c >= world.length) // Border reached
					{																										// these two for loops look up the region around the ant
						continue;
					}
					
					for (int r = -1; r <= 1; r++) // 'r' stands for row ; look one field down, on the field and one field up
					{
						if (c == 0 && r == 0) // Border reached
						{
							continue;
						}
						else if (y + r < 0 || y + r >= world[0].length) // Border reached
						{
							continue;
						}
						
						if (!world[x + c][y + r].isBlocked())
						{
							
							allNeighborCells.add(world[x + c][y + r]);
							totalNeighborPheromones += world[x + c][y + r].nestPheromoneLevel;
							
							if (world[x + c][y + r].getNestPheromoneLevel() > maxNestSoFar)
							{
								maxNestSoFar = world[x + c][y + r]
										.getNestPheromoneLevel();
								
								maxNestCells.clear();
								maxNestCells.add(world[x + c][y + r]);
							}
							else if (world[x + c][y + r]
									.getNestPheromoneLevel() == maxNestSoFar)
							{
								maxNestCells.add(world[x + c][y + r]);
							}
							
							for (Cell food : foodFound)
							{
								
								if (!maxFoodSoFarMap.containsKey(food)
										|| world[x + c][y + r]
												.getFoodPheromoneLevel(food) > maxFoodSoFarMap
												.get(food))
								{
									maxFoodSoFarMap.put(food, world[x + c][y
											+ r].getFoodPheromoneLevel(food));
								}
							}
						}
					}
				}
				
				if (world[x][y].isGoal())
				{
					maxFoodSoFarMap
							.put(world[x][y], Cell.maxFoodPheromoneLevel);
				}
				
				for (Cell food : foodFound)
				{
					world[x][y].setFoodPheromone(food,
							maxFoodSoFarMap.get(food) * Ant.dropoffRate);
				}
				
				if (Ant.bestCellNext > chanceToTakeBest)
				{
					if (!maxNestCells.isEmpty())
					{
						int cellIndex = (int) (maxNestCells.size() * Math
								.random());
						Cell bestNestCellSoFar = maxNestCells.get(cellIndex);
						
						x = bestNestCellSoFar.c;
						y = bestNestCellSoFar.r;
					}
				}
				else
				{ //give cells chance based on pheremone
					double pheremonesSoFar = 0;
					double goalPheromoneLevel = totalNeighborPheromones
							* Math.random();
					for (Cell neighbor : allNeighborCells)
					{
						pheremonesSoFar += neighbor.getNestPheromoneLevel();
						if (pheremonesSoFar > goalPheromoneLevel)
						{
							x = neighbor.c;
							y = neighbor.r;
							break;
						}
					}
				}
			}
		}		
	}
	
	public void checkC(double maxNestSoFar,	Map<Cell, Double> maxFoodSoFarMap, List<Cell> maxNestCells, List<Cell> allNeighborCells, double totalNeighborPheromones, int c, int r)
	{
		if (x + c < 0 || x + c >= world.length) // Border reached
		{																										// these two for loops look up the region around the ant
			return;
		}
		
		checkR(maxNestSoFar, maxFoodSoFarMap, maxNestCells, allNeighborCells, totalNeighborPheromones, c, r);
	}
	
	public void checkR(double maxNestSoFar,	Map<Cell, Double> maxFoodSoFarMap, List<Cell> maxNestCells, List<Cell> allNeighborCells, double totalNeighborPheromones, int c, int r)
	{
		if (c == 0 && r == 0) // Border reached
		{
			return;
		}
		else if (y + r < 0 || y + r >= world[0].length) // Border reached
		{
			return;
		}
		
		if (!world[x + c][y + r].isBlocked())
		{
			
			allNeighborCells.add(world[x + c][y + r]);
			totalNeighborPheromones += world[x + c][y + r].nestPheromoneLevel;
			
			if (world[x + c][y + r].getNestPheromoneLevel() > maxNestSoFar)
			{
				maxNestSoFar = world[x + c][y + r]
						.getNestPheromoneLevel();
				
				maxNestCells.clear();
				maxNestCells.add(world[x + c][y + r]);
			}
			else if (world[x + c][y + r]
					.getNestPheromoneLevel() == maxNestSoFar)
			{
				maxNestCells.add(world[x + c][y + r]);
			}
			
			for (Cell food : foodFound)
			{
				
				if (!maxFoodSoFarMap.containsKey(food)
						|| world[x + c][y + r]
								.getFoodPheromoneLevel(food) > maxFoodSoFarMap
								.get(food))
				{
					maxFoodSoFarMap.put(food, world[x + c][y
							+ r].getFoodPheromoneLevel(food));
				}
			}
		}
	}
	
	public int getCol()
	{
		return x;
	}
	
	public int getRow()
	{
		return y;
	}
	
	public boolean isReturningHome()
	{
		return returnToNest;
	}
}