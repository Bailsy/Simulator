import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * WhiteSharks age, move, eat rabbits or deers, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class WhiteShark extends Animal
{
    // Characteristics shared by all foxes (class variables).
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a fox can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Clownfish. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int CLOWNFISH_FOOD_VALUE = 4;
    // The food value of a single Rabbitfish.
    private static final int RABBITFISH_FOOD_VALUE = 10;
    // The food value of a single Parrotfish.
    private static final int PARROTFISH_FOOD_VALUE = 13;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating rabbits and deers.
    private int foodLevel;

    /**
     * Create a white shark. A white shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the white shark will have random age and hunger level.
     * @param location The location within the field.
     */
    public WhiteShark(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(PARROTFISH_FOOD_VALUE);
    }
    
    /**
     * This is what the white shark does most of the time: it hunts for
     * rabbits or deers. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }



    @Override
    public String toString() {
        return "White shark{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the white shark's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this white shark more hungry. This could result in the white shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits and deers adjacent to the current location.
     * Only the first live rabbit or deer is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof  Parrotfish) {
                if(animal.isAlive()) {
                    animal.setDead();
                    foodLevel = PARROTFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            else if(animal instanceof  Clownfish) {
                if(animal.isAlive()) {
                    animal.setDead();
                    foodLevel = CLOWNFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            else if(animal instanceof  Rabbitfish) {
                if(animal.isAlive()) {
                    animal.setDead();
                    foodLevel = RABBITFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this white shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        // List<Location> adjacentFields = nextFieldState.getAdjacentLocations(getLocation());
        // for(Location loc : adjacentFields) {
            // Animal animal = nextFieldState.getAnimalAt(loc);
            // if(animal instanceof  WhiteShark whiteShark) {
                // if(canBreedWith(whiteShark) && animal.isAlive()){
                    
                // }
            // }
        // }
        
        int births = breed();
        if(births > 0) {
            for(int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                WhiteShark young = new WhiteShark(false, loc);
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A white shark can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}

