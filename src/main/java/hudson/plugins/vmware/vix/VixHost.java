package hudson.plugins.vmware.vix;

import hudson.plugins.vmware.VMware;
import hudson.plugins.vmware.HostType;
import hudson.plugins.vmware.VMwareRuntimeException;
import hudson.plugins.vmware.VirtualMachine;

import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 21:57:20
 */
public class VixHost extends VixObject {
    private static final Logger LOGGER = Logger.getLogger(VixHost.class.getName());
    private final VixHostConfig config;
    private int handle = 0;
    private final Map<String, VixVirtualComputer> computers = new HashMap<String, VixVirtualComputer>();

    public static VixHost newInstance(VixHostConfig config) {
        VixHost host = new VixHost(config);
        host.connect();
        return host;
    }

    private VixHost(VixHostConfig config) {
        super(VixLibraryManager.getVixInstance(config.getVixLibraryPath()));
        this.config = config;
    }

    private void connect() {
        LOGGER.info("Connecting to " + config.getHostName());
        int jobHandle = 0;
        try {
            switch (config.getHostType()) {
                case VMWARE_SERVER:
                default:
                    jobHandle = getLibrary().VixHost_Connect(1, Vix.ServiceProvider.VMWARE_SERVER,
                            config.getHostName(), config.getPortNumber(), config.getUsername(), config.getPassword(),
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

    public void disconnect() {
        if (usageCount.decrementAndGet() <= 0) {
            getLibrary().VixHost_Disconnect(handle);
            handle = 0;
        }
    }

    public synchronized VixVirtualComputer open(String configFileHostPath) {
        checkConnected();
        if (computers.containsKey(configFileHostPath)) {
            return computers.get(configFileHostPath);
        }
        VixVirtualComputer computer;
        computers.put(computer = VixVirtualComputer.newInstance(this, configFileHostPath)));
        return computer;
    }

    public synchronized void close(VixVirtualComputer computer) {
        checkConnected();
        computers.remove()
    }

    private void checkConnected() {
        if (handle == 0 || getLibrary() == null) {
            throw new IllegalStateException("Not connected.");
        }
    }
    private void checkNotConnected() {
        if (handle != 0) {
            throw new IllegalStateException("Alreadt connected.");
        }
    }

}
