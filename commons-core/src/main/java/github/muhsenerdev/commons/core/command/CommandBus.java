package github.muhsenerdev.commons.core.command;

public interface CommandBus {
    <C, R> R send(C command);
}
