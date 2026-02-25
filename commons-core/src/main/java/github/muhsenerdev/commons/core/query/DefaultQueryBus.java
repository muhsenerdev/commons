package github.muhsenerdev.commons.core.query;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class DefaultQueryBus implements QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    public DefaultQueryBus(List<QueryHandler<?, ?>> queryHandlers) {
        queryHandlers.forEach(handler -> handlers.put(handler.getQueryType(), handler));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q, R> R execute(Q query) {
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(query.getClass());
        if (handler == null) {
            throw new RuntimeException("No handler found for query: " + query.getClass().getName());
        }
        return handler.handle(query);
    }
}
