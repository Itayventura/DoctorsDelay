import java.time.LocalDateTime;

public class HttpCommunicationsMocker implements ModelHandler
{
    private DelayEstimation.EstimationType estimationType;
    private int accuracy;

    public HttpCommunicationsMocker() {}

    public HttpCommunicationsMocker(DelayEstimation.EstimationType estimationType, int accuracy)
    {
        this.accuracy = accuracy;
        this.estimationType = estimationType;
    }

    @Override
    public double BuildModel() {
        return 0;
    }

    @Override
    public DelayEstimation Predict(String doctorsName, LocalDateTime meetingDateTime)
    {
        return new DelayEstimation(estimationType, accuracy);
    }
}