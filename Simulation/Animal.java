import java.util.Random;
import java.util.List;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */





//IF You see this is works!!!
// :)




public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    // The animal's gender.
    protected boolean isMale;
    // A Random for the animal's gender to be randomised.
    private Random random = new Random();

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        this.isMale = random.nextBoolean();
    }
    
    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }
    
    public boolean getIsMale() {
        return isMale;
    }
    
    protected boolean canBreedWith(Animal mate) {
        return mate != null && this.getClass().equals(mate.getClass()) && this.getIsMale() != mate.getIsMale();
    }
    
    public Animal findBreedingPartner(Field field) {
        List<Location> adjacentFields = field.getAdjacentLocations(getLocation());
        for (Location loc : adjacentFields) {
            Animal animal = field.getAnimalAt(loc);
            if (animal != null && canBreedWith(animal) && animal.isAlive()) {
                return animal; // Return first valid breeding partner found
            }
        }
        return null; // No valid partner found
    }
}
