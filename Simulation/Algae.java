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
public class Algae extends Plant
{
    // Characteristics shared by all foxes (class variables).
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a fox can live.
    private static final int MAX_AGE = 10;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The fox's age.
    private int age;


    /**
     * Create a white shark. A white shark can be created as a new born (age zero)
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the white shark will have random age and hunger level.
     * @param location The location within the field.
     */
    public Algae(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
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
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                reproduce(nextFieldState);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placePlant(this, nextLocation);
            }

        }
    }



    @Override
    public String toString() {
        return "White shark{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
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
     * Look for rabbits and deers adjacent to the current location.
     * Only the first live rabbit or deer is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    
    public void reproduce(Field nextFieldState) {
        
            int births = breed();
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(this.getLocation());
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Algae algae = new Algae(false, loc);
                nextFieldState.placePlant(algae, loc);
            
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
