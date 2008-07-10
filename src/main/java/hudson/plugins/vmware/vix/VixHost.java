package hudson.plugins.vmware.vix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import hudson.plugins.vmware.VMwareRuntimeException;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 21:57:20
 */
public class VixHost extends VixObject {

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(VixHost.class.getName());

    /**
     * The host config.
     */
    private final VixHostConfig config;

    /**
     * The Vix handle to the host.
     *
     * @guarded-by #handleLock
     */
    private int handle = 0;

    /**
     * Guard lock for {@linkplain #handle}
     */
    private final Object handleLock = new Object();

    /**
     * The virtual computers open on this host.
     *
     * @guarded-by #computersLock
     */
    private final Map<String, VixVirtualComputer> computers = new HashMap<String, VixVirtualComputer>();

    /**
     * Guard lock for {@linkplain #computers}
     */
    private final Object computersLock = new Object();

    /**
     * Creates a new VixHost instance.
     *
     * @param config The host config
     *
     * @return the instance
     */
    static VixHost newInstance(VixHostConfig config) {
        VixHost host = new VixHost(config);
        host.connect();
        return host;
    }

    /**
     * Constructor VixHost creates a new VixHost instance.
     *
     * @param config of type VixHostConfig
     */
    private VixHost(VixHostConfig config) {
        super(VixLibraryManager.getVixInstance(config.getVixLibraryPath()));
        this.config = config;
    }

    /**
     * Connects to the Vix host.
     */
    private void connect() {
        checkNotConnected();
        synchronized (handleLock) {
            LOGGER.info("Connecting to " + config.getHostName());
            int jobHandle = 0;
            try {
                switch (config.getHostType()) {
                    case VMWARE_SERVER:
                    default:
                        jobHandle = getLibrary().VixHost_Connect(1, Vix.ServiceProvider.VMWARE_SERVER,
                                config.getHostName(), config.getPortNumber(), config.getUsername(),
                                config.getPassword(),
                                0, Vix.Handle.INVALID, null, null);
                        break;
                }
                if (jobHandle == 0) {
                    throw new VMwareRuntimeException("Unknown error");
                }

                LOGGER.fine("Waiting to connect...");

                handle = waitForJobAndGetJobResultHandle(jobHandle);

                LOGGER.info("Connected");
            } finally {
                getLibrary().Vix_ReleaseHandle(jobHandle);
            }
        }
    }

    /**
     * Disconnect from the vix host.
     */
    public void disconnect() {
        synchronized (computersLock) {
            while (!computers.isEmpty()) {
                close(computers.values().iterator().next());
            }
            synchronized (handleLock) {
                getLibrary().VixHost_Disconnect(handle);
                handle = 0;
            }
        }
    }

    /**
     * Opens a virtual computer on the host.
     *
     * @param config the config of the virtual computer.
     *
     * @return the virtual computer.
     */
    public synchronized VixVirtualComputer open(VixVirtualComputerConfig config) {
        checkConnected();
        synchronized (computersLock) {
            if (computers.containsKey(config.getVmxFilePath())) {
                return computers.get(config.getVmxFilePath());
            }
            VixVirtualComputer computer;
            computers.put(config.getVmxFilePath(), computer = VixVirtualComputer.newInstance(this, config));
            return computer;
        }
    }

    /**
     * Closes a virtual computer on the host.
     *
     * @param computer the virtual computer to close.
     */
    public synchronized void close(VixVirtualComputer computer) {
        checkConnected();
        synchronized (computersLock) {
            for (Iterator<Map.Entry<String, VixVirtualComputer>> i = computers.entrySet().iterator(); i.hasNext();) {
                Map.Entry<String, VixVirtualComputer> entry = i.next();
                if (computer.equals(entry.getValue())) {
                    i.remove();
                    computer.close();
                }
            }
        }
    }

    /**
     * Check that we have a valid handle.
     *
     * @throws IllegalStateException if not connected.
     */
    private void checkConnected() {
        synchronized (handleLock) {
            if (handle == 0 || getLibrary() == null) {
                throw new IllegalStateException("Not connected.");
            }
        }
    }

    /**
     * Check that we have an invalid handle.
     *
     * @throws IllegalStateException if connected.
     */
    private void checkNotConnected() {
        synchronized (handleLock) {
            if (handle != 0) {
                throw new IllegalStateException("Alreadt connected.");
            }
        }
    }

    /**
     * Getter for property 'handle'.
     *
     * @return Value for property 'handle'.
     */
    int getHandle() {
        synchronized (handleLock) {
            return handle;
        }
    }
}
