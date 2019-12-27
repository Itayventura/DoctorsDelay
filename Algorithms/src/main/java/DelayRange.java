public class DelayRange
{
    private int minimalDelay;
    private int maximalDelay;
    DelayEstimation.EstimationType estimationType;

    public DelayRange(int minimum, int maximum, DelayEstimation.EstimationType type)
    {
        minimalDelay = minimum;
        maximalDelay = maximum;
        estimationType = type;
    }

    public int getMinimalDelay()
    {
        return minimalDelay;
    }

    public int getMaximalDelay()
    {
        return maximalDelay;
    }
    public DelayEstimation.EstimationType getEstimationType(){ return estimationType; }

}