import java.util.HashMap;
import java.util.Map;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 * 
 * @author Nicolás Alcalá Olea and Bailey Crossan
 */
public class FieldStats
{
    // Counters for each type of entity (fox, rabbit, etc.) in the simulation.
    private final Map<Class<?>, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of animal that
        // we might find
        counters = new HashMap<>();
        countsValid = true;
    }

    /**
     * Get details of what is in the field.
     * 
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder details = new StringBuilder();
        if(!countsValid) {
            generatePlantCounts(field);
            generateAnimalCounts(field);
        }
        for(Class<?> key : counters.keySet()) {
            Counter info = counters.get(key);
            details.append(info.getName())
            .append(": ")
            .append(info.getCount())
            .append(' ');
        }
        return details.toString();
    }

    /**
     * Invalidate the current set of statistics; reset all 
     * counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(Class<?> key : counters.keySet()) {
            Counter count = counters.get(key);
            count.reset();
        }
    }

    /**
     * Increment the count for one class of animal.
     * 
     * @param animalClass The class of animal to increment.
     */
    public void incrementCount(Class<?> organismClass)
    {
        Counter count = counters.get(organismClass);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(organismClass.getName());
            counters.put(organismClass, count);
        }
        count.increment();
    }

    /**
     * Indicate that an animal count has been completed.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable.
     * I.e., should it continue to run.
     * 
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return field.isViable();
    }

    /**
     * Generate counts of the number of animals.
     * These are not kept up to date as animals are
     * placed in the field, but only when a request
     * is made for the information.
     * 
     * @param field The field to generate the stats for.
     */
    private void generateAnimalCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal animal = field.getAnimalAt(new Location(row, col));
                if(animal != null) {
                    incrementCount(animal.getClass());
                }
            }
        }
        countsValid = true;
    }

    /**
     * Generate counts of the number of plants.
     * These are not kept up to date as plants are
     * placed in the field, but only when a request
     * is made for the information.
     * 
     * @param field The field to generate the stats for.
     */
    private void generatePlantCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Plant plant = field.getPlantAt(new Location(row, col));
                if(plant != null) {
                    incrementCount(plant.getClass());
                }
            }
        }
        countsValid = true;
    }
}
