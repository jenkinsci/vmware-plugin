package hudson.plugins.vmware;

import java.util.Collection;

/**
 * Represents a host for running virtual computers.
 *
 * @author Stephen Connolly
 */
public interface VirtualComputerHost {
    /**
     * Returns {@code true} if the connection to the host is open.
     *
     * @return {@code true} if the connection to the host is open.
     */
    boolean isConnected();

    /**
     * Opens the connection to the host.
     */
    void open();

    /**
     * Closes the connection to the host.
     */
    void close();

    /**
     * If the connection to the host is open, returns the virtual computers registered on the host.
     *
     * @return the virtual computers registered on the host.
     * @throws IllegalStateException if the connection is not open.
     */
    Collection<VirtualComputer> getVirtualComputers();
}
