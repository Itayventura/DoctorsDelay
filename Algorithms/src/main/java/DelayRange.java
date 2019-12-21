public class DelayRange
{
    private int minimalDelay;
    private int maximalDelay;

    public DelayRange(int minimum,int maximum)
    {
        minimalDelay = minimum;
        maximalDelay = maximum;
    }

    public int getMinimalDelay()
    {
        return minimalDelay;
    }

    public int getMaximalDelay()
    {
        return maximalDelay;
    }
}