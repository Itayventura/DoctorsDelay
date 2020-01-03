package estimation;

import java.time.LocalDateTime;

public class ModelDetails
{
    private LocalDateTime lastModelUpdatedTime;
    private double modelAccuracy;

    public ModelDetails(LocalDateTime lastModelUpdatedTime, double modelAccuracy)
    {
        this.lastModelUpdatedTime = lastModelUpdatedTime;
        this.modelAccuracy = modelAccuracy;
    }

    public LocalDateTime getLastModelUpdatedTime() { return this.lastModelUpdatedTime;}
    public double getModelAccuracy() { return this.modelAccuracy; }
}