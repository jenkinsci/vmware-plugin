package hudson.plugins.vmware;

import hudson.remoting.Future;

import java.util.Set;

/**
 * Represents a virtual computer. Virtual computers run on a virtual computer host.
 * @author Stephen Connolly
 */
public interface VirtualComputer {
    String getName();
    VirtualComputerHost getHost();
    State getState();
    Set<Command> getAvailableCommands();

    public static enum State {
        POWERED_OFF,
        POWERING_UP,
        POWERING_DOWN,
        RUNNING,
        SUSPENDED        
    }

    public static enum Command {
        TURN_ON,
        TURN_OFF,
        SUSPEND,
        RESUME,
        TAKE_SNAPSHOT,
        REVERT_TO_SNAPSHOT,        
        ;


    }

    public static interface Operation extends Future<Boolean> {

    }

}
