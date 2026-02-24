package github.muhsenerdev.commons.core.command;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class DefaultCommandBus implements CommandBus {

    private final Map<Class<?>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    public DefaultCommandBus(List<CommandHandler<?, ?>> commandHandlers) {
        commandHandlers.forEach(handler -> handlers.put(handler.getCommandType(), handler));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C, R> R send(C command) {
        CommandHandler<C, R> handler = (CommandHandler<C, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new RuntimeException("No handler found for command: " + command.getClass().getName());
        }
        return handler.handle(command);
    }
}
