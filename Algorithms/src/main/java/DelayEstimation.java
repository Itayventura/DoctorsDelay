import java.security.InvalidParameterException;

public class DelayEstimation
{
    public enum EstimationType
    {
        Small,
        Medium,
        Large
    }

    private EstimationType estimationType;
    private int accuracyEstimationPercentage;

    public DelayEstimation(EstimationType type, int accuracyPercentage) throws InvalidParameterException
    {
        estimationType = type;

        if(accuracyPercentage > 100 || accuracyPercentage < 0)
        {
            throw new InvalidParameterException("Error: Percentage should be between 0 and 100");
        }

        accuracyEstimationPercentage = accuracyPercentage;
    }

    public DelayRange getTypeRange()
    {
        switch (estimationType)
        {
            case Small:
                return new DelayRange(0,15);
            case Medium:
                return new DelayRange(16,30);
            case Large:
                return new DelayRange(31,12*60);
            default:
                return new DelayRange(0,15);
        }
    }

    public int getEstimationAccuracyPercentage()
    {
        return accuracyEstimationPercentage;
    }
}
