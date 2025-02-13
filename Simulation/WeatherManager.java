import java.util.Random;

/**
 * The weather manager class manages the different types of weather
 * phenome
 */
public class WeatherManager {
    private Weather currentWeather;
    private double timeRemaining; // The simulation steps remaining in the current weather.
    private Random random;
    
    /**
     * 
     */
    public WeatherManager() {
        random = new Random();
        currentWeather = Weather.CLEAR;
        timeRemaining = randomDuration();
    }
    
    /**
     * 
     */
    public void update(double deltaTime) {
        timeRemaining -= deltaTime;
        if (timeRemaining <= 0) {
            changeWeather();
        }
    }
    
    private void changeWeather() {
        // Randomly select a weather type.
        Weather[] types = Weather.values();
        currentWeather = types[random.nextInt(types.length)];
        timeRemaining = randomDuration();
    }
    
    private double randomDuration() {
        // For example, weather lasts between 20 and 40 simulation steps.
        return 20 + random.nextInt(21);
    }
    
    public Weather getCurrentWeather() {
        return currentWeather;
    }
    
    // Modifier for predator hunting efficiency (e.g., chance to catch prey)
    public double getPredatorHuntingModifier() {
        if (currentWeather == Weather.FOG) {
            return 0.7; // 30% reduction in hunting success
        }
        return 1.0;
    }
    
    // Modifier for prey feeding efficiency (e.g., chance to successfully eat)
    public double getPreyFeedingModifier() {
        if (currentWeather == Weather.FOG) {
            return 0.7;
        }
        return 1.0;
    }
    
    // Modifier for plant growth (fewer offspring when foggy)
    public double getPlantGrowthModifier() {
        if (currentWeather == Weather.FOG) {
            return 0.8;
        }
        return 1.0;
    }
}
