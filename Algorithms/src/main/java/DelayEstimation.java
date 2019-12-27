import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

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

    public static HashMap<String, EstimationType> StringToEstimationType = new HashMap<String, EstimationType>()
    {{
        put("S", EstimationType.Small);
        put("M", EstimationType.Medium);
        put("L", EstimationType.Large);
    }};

    public DelayEstimation(EstimationType type, int accuracyPercentage)
    {
        estimationType = type;
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
                return new DelayRange(0,0);
        }
    }

    public int getEstimationAccuracyPercentage()
    {
        return accuracyEstimationPercentage;
    }
    //public EstimationType getEstimationType(){ return estimationType; }
}
