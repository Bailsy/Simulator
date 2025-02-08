/**
 * Write a description of class Time here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Time
{
    private static final int DAY_TIME = 20;
    private static final int NIGHT_TIME = 6;
    private static int hour;
    private static int minute;
    /**
     * Constructor for objects of class Time
     */
    public Time()
    {
        hour = 12;
        minute = 0;
    }
    
    public int getHour()
    {
        return hour;
    }
    
    public int getMinute()
    {
        return minute;
    }
    
    public void increment()
    {
        minute = (minute + 10) % 60;
        if(minute == 0) {
            hour = (hour + 1) % 24;
        }
    }
    
    public static boolean isNight()
    {
        return hour > DAY_TIME && hour < NIGHT_TIME;
    }
}
