package estimation;

import java.time.LocalDateTime;

public interface ModelHandler
{
    double BuildModel();
    DelayEstimation Predict(String doctorsName, LocalDateTime meetingDateTime);
}