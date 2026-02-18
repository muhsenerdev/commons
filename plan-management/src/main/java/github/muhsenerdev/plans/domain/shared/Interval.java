package github.muhsenerdev.plans.domain.shared;

public enum Interval {
    MONTHLY,
    YEARLY;

    public static Interval fromStringOrNull(String value) {
        for (Interval interval : Interval.values()) {
            if (interval.name().equals(value)) {
                return interval;
            }
        }
        return null;
    }
}
