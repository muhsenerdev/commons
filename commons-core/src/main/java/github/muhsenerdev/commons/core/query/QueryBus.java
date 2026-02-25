package github.muhsenerdev.commons.core.query;

public interface QueryBus {
    <Q, R> R execute(Q query);
}
