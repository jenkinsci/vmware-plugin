package hudson.plugins.vmware.vix;

import com.sun.jna.Native;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for loading one and only one copy of each Vix library.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 22:01:32
 */
public class VixLibraryManager {
    /**
     * The logger.
     */
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(VixLibraryManager.class.getName());

    /**
     * There are problems if we load the same library more than once, also libraries cannot be unloaded, so once they
     * are in they are in for good.
     * <p/>
     * Guarded by {@link #VIX_INSTANCES_LOCK}.
     */
    private static final Map<String, Vix> VIX_INSTANCES = new HashMap<String, Vix>();

    /**
     * Lock for accessing {@link #VIX_INSTANCES}.
     */
    private static final Object VIX_INSTANCES_LOCK = new Object();

    /**
     * There are problems if we have two handles for the same host.
     */
    private static final Map<VixHostConfig, VixHost> HOST_INSTANCES = new HashMap<VixHostConfig, VixHost>();

    /**
     * Lock for accessing {@link #VIX_INSTANCES}.
     */
    private static final Object HOST_INSTANCES_LOCK = new Object();

    /**
     * Gets a {@link Vix} instance for a given path.
     *
     * @param libraryPath The path to vix.
     * @return The vix instance or {@code null} if it could not be loaded.
     */
    public static Vix getVixInstance(String libraryPath) {
        synchronized (VIX_INSTANCES_LOCK) {
            File path = new File(libraryPath);
            try {
                libraryPath = path.getCanonicalPath();
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Could not get canonical path, reverting to absolute", e);
                libraryPath = path.getAbsolutePath();
            }
            Vix instance = VIX_INSTANCES.get(libraryPath);
            if (instance == null) {
                LOGGER.log(Level.INFO, "Attempting to load VMware libraries at path {0}", libraryPath);
                try {
                    final String oldProp = System.getProperty("jna.library.path");
                    try {
                        System.setProperty("jna.library.path", libraryPath);
                        instance = (Vix) Native.synchronizedLibrary((Vix) Native.loadLibrary("vix", Vix.class));
                        VIX_INSTANCES.put(libraryPath, instance);
                        LOGGER.log(Level.INFO, "VMware libraries at path {0} loaded", libraryPath);
                    } finally {
                        if (oldProp != null) {
                            System.setProperty("jna.library.path", oldProp);
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.log(Level.SEVERE, "VMware libraries at path " + libraryPath + " failed to load", t);
                }

            }
            return instance;
        }
    }

    /**
     * Gets the {@link hudson.plugins.vmware.vix.VixHost} for a given {@link hudson.plugins.vmware.vix.VixHostConfig}
     *
     * @param config of type VixHostConfig
     * @return VixHost
     */
    public static VixHost getHostInstance(VixHostConfig config) {
        synchronized (HOST_INSTANCES_LOCK) {
            VixHost host = HOST_INSTANCES.get(config);
            if (host == null) {
                host = VixHost.newInstance(config);
                HOST_INSTANCES.put(config, host);
            }
            return host;
        }
    }

}
