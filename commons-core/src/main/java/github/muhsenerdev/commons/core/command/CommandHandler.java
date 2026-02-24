package github.muhsenerdev.commons.core.command;

public interface CommandHandler<C, R> {
    R handle(C command);

    Class<C> getCommandType();
}
