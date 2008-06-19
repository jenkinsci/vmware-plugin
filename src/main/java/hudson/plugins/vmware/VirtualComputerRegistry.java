package hudson.plugins.vmware;

/**
 * Maintains a registry of virtual computers.
 *
 * @author Stephen Connolly
 */
public final class VirtualComputerRegistry {
    public void register(String name, VirtualComputer computer) {
        throw new UnsupportedOperationException("To be implemented");
    }
    public void unregister(String name, VirtualComputer computer) {
        throw new UnsupportedOperationException("To be implemented");
    }
    public VirtualComputer getVirtualComputer(String name) {
        throw new UnsupportedOperationException("To be implemented");
    }
}
