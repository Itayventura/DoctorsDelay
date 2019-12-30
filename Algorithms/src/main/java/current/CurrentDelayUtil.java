package current;

import entities.Delay;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrentDelayUtil {
    public static final int MINUTES_DURATION = (int) TimeUnit.HOURS.toMinutes(3);

    public static double getPredictedDelay(List<Delay> delays, LocalDateTime startTime) {
        if (delays.size() == 1) {
            return delays.get(0).getReportedDelay();
        }
        List<Integer> predictions = new ArrayList<>(delays.size() - 1);
        delays.sort(Comparator.comparing(Delay::getReportTimestamp));
        for (int i = 0; i < delays.size() - 1; i ++) {
            for (int j = i + 1; j < delays.size(); j ++) {
                try {
                    predictions.add(predict(delays.get(i), delays.get(j), startTime));
                } catch (Throwable e) {} //continue...
            }
        }
        removeOutliers(predictions);
        return predictions.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public static int getCurrentAccuracy(int doctorsInterval, int reports) {
        doctorsInterval = doctorsInterval > 0 ? doctorsInterval : 10;
        final int maxReportsPossible = MINUTES_DURATION / doctorsInterval;
        int percentage = (int)(100 * ((float) reports / (0.5 * maxReportsPossible)));
        return Math.min(100, percentage);
    }

    public static int predict(Delay delay1, Delay delay2, LocalDateTime startTime) {
        double slope = (float)(delay2.getReportedDelay() - delay1.getReportedDelay()) /
                ChronoUnit.MINUTES.between(delay1.getReportTimestamp(), delay2.getReportTimestamp());
        long reportTimestampRelative = ChronoUnit.MINUTES.between(startTime, delay1.getReportTimestamp());
        double intersection = delay1.getReportedDelay() - (slope * reportTimestampRelative);
        return (int)((slope * MINUTES_DURATION) + intersection);
    }

    public static void removeOutliers(List<Integer> predictions) {
        double mean = predictions.stream().mapToInt(Integer::intValue).average().orElse(0);
        int n = predictions.size() > 1 ? predictions.size() : 2;
        double s = Math.sqrt((predictions.stream().mapToDouble(p -> Math.pow((p - mean), 2)).sum()) / (n - 1));
        double upper = mean + s;
        double lower = mean - s;
        predictions.removeIf(p -> p > upper || p < lower);
    }

}
