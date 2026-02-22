package github.muhsenerdev.plans.domain.shared;

public enum Interval {
    MONTHLY,
    YEARLY,
    INFINITE;

    public static Interval fromStringOrNull(String value) {
        for (Interval interval : Interval.values()) {
            if (interval.name().equals(value)) {
                return interval;
            }
        }
        return null;
    }

    public boolean isInfinite() {
        return this == INFINITE;
    }

    public boolean isMonthly() {
        return this == MONTHLY;
    }

    public boolean isYearly() {
        return this == YEARLY;
    }
}
