package github.muhsenerdev.commons.core.query;

public interface QueryHandler<Q, R> {
    R handle(Q query);

    Class<Q> getQueryType();
}
