import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A model of a swordfish they can breed, eat preys to survive and if 
 * their maximun age is reached or they dont eat what they are required 
 * they will die. They will also likely die if the disease catches them, 
 * it can also be transmitted to their mate or baby.
 * 
 * @author Nicolás Alcalá Olea and Bailey Crossan
 */
public class Swordfish extends Animal
{
    // Characteristics shared by all swordfish (class variables).

    // The age at which a swordfish can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a swordfish can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a swordfish breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The likelihood of a swordfish catching the disease.
    private static final double INFECTION_PROBABILITY = 0.01;
    // The likelihood of a swordfish transmitting the disease.
    private static final double TRANSMISSION_PROBABILITY = 0.02;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single parrotfish. In effect, this is the
    // number of steps a swordfish can go before it has to eat again.
    private static final int PARROTFISH_FOOD_VALUE = 300;
    // The food value of a single clownfish.
    private static final int CLOWNFISH_FOOD_VALUE = 300;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The swordfish age.
    private int age;
    // The swordfish food level, which is increased by eating parrotfish and clownfish.
    private int foodLevel;

    /**
     * Create a swordfish. A swordfish can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the swordfish will have random age and hunger level.
     * @param location The location within the field.
     */
    public Swordfish(boolean randomAge, Location location)
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
     * Defines the actions performed by the swordfish during one simulation
     * step: it looks for its source of food and in the process, it might 
     * breed, die of hunger or die of old age.
     * 
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
            
            if(!infected && rand.nextDouble() <= INFECTION_PROBABILITY) {
                setInfected();
            }
            if(infected && rand.nextDouble() <= 0.05) {
                setDead();
            }
            
            if(! freeLocations.isEmpty()) {
                giveBirth(nextFieldState);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            
            double movingModifier = Simulator.weatherManager.getPredatorMovingModifier();
            if(rand.nextDouble() <= movingModifier){
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
    }

    @Override
    public String toString() 
    {
        return "Swordfish{" +
        "age=" + age +
        ", alive=" + isAlive() +
        ", location=" + getLocation() +
        ", foodLevel=" + foodLevel +
        '}';
    }

    /**
     * Increase the age. This could result in the swordfish death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this swordfish more hungry. This could result in the swordfish death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for preys adjacent to the current location.
     * Only the first prey is eaten.
     * 
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;

        double huntingModifier = Simulator.weatherManager.getPredatorHuntingModifier();
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Parrotfish && animal.isAlive()) {
                if(rand.nextDouble() <= huntingModifier) {
                    animal.setDead();
                    foodLevel = PARROTFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            else if(animal instanceof Clownfish && animal.isAlive()) {
                if(rand.nextDouble() <= huntingModifier) {
                    animal.setDead();
                    foodLevel = CLOWNFISH_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Give birth to a new swordfish that spawns if there are free locations
     * around their parent. When mating if one of the parents has the disease
     * there is a chance that it transmitts the disease to the other mate. And
     * if both have the disease, their baby will also have the disease.
     * 
     * @param nextFieldState Where the new swordfish is going to be added.
     */
    public void giveBirth(Field nextFieldState) 
    {
        Animal mate = findBreedingMate(nextFieldState);
        if (mate != null) {
            if (this.isInfected() && !mate.isInfected()) {
                if (rand.nextDouble() <= TRANSMISSION_PROBABILITY) {
                    mate.setInfected();
                }
            } else if (!this.isInfected() && mate.isInfected()) {
                if (rand.nextDouble() <= TRANSMISSION_PROBABILITY) {
                    this.setInfected();
                }
            }
            int births = breed();
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(this.getLocation());
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Swordfish young = new Swordfish(false, loc);
                double INHERIT_PROBABILITY = 0.01;
                if(mate.isInfected() || this.isInfected()) {
                    if (rand.nextDouble() <= INHERIT_PROBABILITY) {
                        young.setInfected();
                    }
                }
                nextFieldState.placeAnimal(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
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
     * A swordfish can breed if it has reached the breeding age.
     * 
     * @return true If they can start breeding.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}

