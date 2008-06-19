package hudson.plugins.vmware;

/**
 * Represents a virtual computer. Virtual computers run on a virtual computer host.
 * @author Stephen Connolly
 */
public interface VirtualComputer {
    VirtualComputerHost getHost();
    State getState();

    public static enum State {
        POWERED_OFF,
        POWERING_UP,
        POWERING_DOWN,
        RUNNING,
        SUSPENDED        
    }
}
