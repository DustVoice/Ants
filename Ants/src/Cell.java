import java.util.HashMap;
import java.util.Map;

/**
 * The basic cell class
 * 
 * @author David Holland
 */

public class Cell
{
	
	public static double maxFoodPheromoneLevel = 100.0; // Maximum pheromone level
	public static double maxNestPheromoneLevel = 100.0; // Maximum nest pheromone level
	public static double evaporationRate = .25; // The pheromone level reduction per step (.9 = reduce the pheromone with 10% per step)
	private boolean hasObstacle; // Does the cell contain an Obstacle
	private boolean hasNest; // Does the cell contain a Nest
	public Map<Cell, Double> foodPheromoneLevelMap = new HashMap<Cell, Double>(); // HashMap which represents a global mapping of all pheromone levels
	public double nestPheromoneLevel = 1.0;
	private boolean isGoal = false; // Is this cell the goal of a movement
	
	int c;
	int r;
	
	/**
	 * Create a new Cell Object with the following coordinates
	 * @param c
	 * @param r
	 */
	public Cell(int c, int r)
	{
		this.c = c; // Set the local variable c (stands for column) to the paramter c of the function
		this.r = r; // Set the local variable r (stands for row) to the paramter r of the function
	}
	
	/**
	 * Set the boolean isGoal
	 * @param isGoal boolean parameter, which will be parsed to the public boolean isGoal
	 */
	public void setIsGoal(boolean isGoal)
	{
		this.isGoal = isGoal; // Set isGoal to the parameter of the function
	}
	
	/**
	 * Method for a single step
	 */
	public void step()
	{
		// Following is meant to be for food cells
		for (Cell food : foodPheromoneLevelMap.keySet()) // Iterate through all the keys in the pheromone map and set 'food' to the current Key<Cell> 
		{
			double foodPheromoneLevel = foodPheromoneLevelMap.get(food); // Get the actual pheromone level of the current cell
			
			foodPheromoneLevel *= Cell.evaporationRate; // Reduce the cells pheromone level by the evaporation rate
			
			if (foodPheromoneLevel < 1) // Prevent the pheromone level getting under 1
			{
				foodPheromoneLevel = 1;
			}
			
			if (foodPheromoneLevel > Cell.maxFoodPheromoneLevel) // Prevent the pheromone level getting over max
			{
				foodPheromoneLevel = Cell.maxFoodPheromoneLevel;
			}
			
			foodPheromoneLevelMap.put(food, foodPheromoneLevel); // Sync the new pheromone values with the map
		}
		
		nestPheromoneLevel *= Cell.evaporationRate; // Reduce the nests pheromone level by the evaporation rate
		
		if (nestPheromoneLevel < 1) // Prevent the pheromone level getting under 1
		{
			nestPheromoneLevel = 1;
		}
		
		if (nestPheromoneLevel > Cell.maxNestPheromoneLevel) // Prevent the pheromone level getting over max
		{
			nestPheromoneLevel = Cell.maxNestPheromoneLevel;
		}
	}
	
	/**
	 * Method for a single step
	 */
	public void step(int ph)
	{
		// Following is meant to be for food cells
		for (Cell food : foodPheromoneLevelMap.keySet()) // Iterate through all the keys in the pheromone map and set 'food' to the current Key<Cell> 
		{
			double foodPheromoneLevel = foodPheromoneLevelMap.get(food); // Get the actual pheromone level of the current cell
			
			foodPheromoneLevel *= ph; // Reduce the cells pheromone level by the evaporation rate
			
			if (foodPheromoneLevel < 1) // Prevent the pheromone level getting under 1
			{
				foodPheromoneLevel = 1;
			}
			
			if (foodPheromoneLevel > Cell.maxFoodPheromoneLevel) // Prevent the pheromone level getting over max
			{
				foodPheromoneLevel = Cell.maxFoodPheromoneLevel;
			}
			
			foodPheromoneLevelMap.put(food, foodPheromoneLevel); // Sync the new pheromone values with the map
		}
		
		nestPheromoneLevel *= ph; // Reduce the nests pheromone level by the evaporation rate
		
		if (nestPheromoneLevel < 1) // Prevent the pheromone level getting under 1
		{
			nestPheromoneLevel = 1;
		}
		
		if (nestPheromoneLevel > Cell.maxNestPheromoneLevel) // Prevent the pheromone level getting over max
		{
			nestPheromoneLevel = Cell.maxNestPheromoneLevel;
		}
	}
	
	/**
	 * Method thought to be triggered at food arrive event.
	 * Parse the given Cell and the pheromone value to the pheromone map
	 * @param food The food cell
	 * @param pheromone The wanted pheromone level
	 */
	public void setFoodPheromone(Cell food, double pheromone)
	{
		if (pheromone > Cell.maxFoodPheromoneLevel) // If the given parameter 'pheromone' is greater than the maximum allowed pheromone level, ...
		{
			pheromone = Cell.maxFoodPheromoneLevel; // ... set the parameter 'pheromone' to the maximum pheromone level
		}
		foodPheromoneLevelMap.put(food, pheromone); // Add the target cell and the parameter 'pheromone' into the pheromone hash map
	}
	
	/**
	 * Set the nest pheromonelevel
	 * @param pheromone The wanted nest pheromone level
	 */
	public void setNestPheromone(double pheromone)
	{
		nestPheromoneLevel = pheromone; // Set the nest pheromone level to the value of 'pheromone'
		if (nestPheromoneLevel > Cell.maxNestPheromoneLevel) // If the nest pheromone level is over the allowed value, ...
		{
			nestPheromoneLevel = Cell.maxNestPheromoneLevel; // ... set it to the max level
		}
	}
	
	/**
	 * Returns the pheromone level of a given cell
	 * @param food The target cell
	 * @return Returns the pheromone level of the target cell, if not present in the map, return 1;
	 */
	public double getFoodPheromoneLevel(Cell food)
	{
		if (!foodPheromoneLevelMap.containsKey(food)) // Prevent from returning NULL witch catching the fact that the pheromone map does not contain an entrz for the target cell
		{
			return 1; // When no entry given return 1
		}
		return foodPheromoneLevelMap.get(food); // Return the assigned pheromone level of the target cell out of the map
	}
	
	/**
	 * Returns the nest pheromone level
	 */
	public double getNestPheromoneLevel()
	{
		return nestPheromoneLevel;
	}
	
	/**
	 * @return Returns, if a cell is blocked/contains an obstacle
	 */
	public boolean isBlocked()
	{
		return hasObstacle;
	}
	
	/**
	 * @return Returns, if the cell is the goal of a movement
	 */
	public boolean isGoal()
	{
		return isGoal;
	}
	
	/**
	 * Set wether the cell has an obstacle or not
	 * @param hasObstacle true: Cell will contain an obstacle; false: Cell will contain no obstacle
	 */
	public void setIsObstacle(boolean hasObstacle)
	{
		this.hasObstacle = hasObstacle; // Set the variable 'hasObstacle' to the given parameter
	}
	
	/**
	 * @return Returns wether the cell contains a nest or not
	 */
	public boolean hasNest()
	{
		return hasNest;
	}
	
	/**
	 * Set wether the cell contains a nest or not
	 * @param hasNest true: Cell will contain a nest; false: Cell will contain no nest
	 */
	public void setHasNest(boolean hasNest)
	{
		this.hasNest = hasNest; // Set the variable 'hasNest' to the given parameter
	}
}