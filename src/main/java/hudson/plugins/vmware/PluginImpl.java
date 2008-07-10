package hudson.plugins.vmware;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.slaves.ComputerLauncher;
import hudson.tasks.BuildWrappers;
import hudson.util.FormFieldValidator;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Entry point of vmware plugin.
 *
 * @author Stephen Connolly
 * @plugin
 */
public class PluginImpl extends Plugin {

    /**
     * Returns the plugin instance.
     *
     * @return The plugin instance.
     */
    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }

    private static class VixReference {
        private final Closeable closeable;
        private int refCount;

        public VixReference(Closeable closeable) {
            closeable.getClass();
            this.closeable = closeable;
            this.refCount = 1;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VixReference that = (VixReference) o;

            // use object identity
            return closeable == that.closeable;
        }

        public int hashCode() {
            return System.identityHashCode(closeable);
        }

        public synchronized void inc() {
            refCount++;
        }

        public synchronized void dec() throws IOException {
            refCount--;
            if (refCount == 0) {
                closeable.close();
            }
        }
    }

    private static final ConcurrentMap<String, String> vmIPAddresses = new ConcurrentHashMap<String, String>();
    private static final ConcurrentMap<String, CountDownLatch> nameLatches = new ConcurrentHashMap<String, CountDownLatch>();
    private final String URL_PREFIX = "file:/";

    public void start() throws Exception {
        BuildWrappers.WRAPPERS.add(VMwareActivationWrapper.DESCRIPTOR);
        ComputerLauncher.LIST.add(VMwareLauncher.DESCRIPTOR);
    }

    public static String findDefaultVixLibraryPath() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            if (new File("C:\\Program Files\\VMware\\VMware VIX\\server-1\\32bit\\vix.dll").exists()) {
                return "C:\\Program Files\\VMware\\VMware VIX\\server-1\\32bit";
            } else if (new File("C:\\Program Files\\VMware\\VMware VIX\\vix.dll").exists()) {
                return "C:\\Program Files\\VMware\\VMware VIX";
            } else {
                return "";
            }
        } else {
            if (new File("/usr/lib/vmware-vix/lib/server-1/32bit/libvix.so").exists()) {
                return "/usr/lib/vmware-vix/lib/server-1/32bit";
            } else if (new File("/usr/lib/vmware-vix/lib/libvix.so").exists()) {
                return "/usr/lib/vmware-vix/lib";
            } else {
                return "";
            }
        }
    }

    /**
     * Checks if the VIX path is a valid VIX path.
     */
    public void doVixLibraryCheck(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        // this can be used to check the existence of a file on the server, so needs to be protected
        new FormFieldValidator(req, rsp, true) {
            public void check() throws IOException, ServletException {
                File f = getFileParameter("value");
                if (!f.isDirectory()) {
                    error(Messages.PluginImpl_NotADirectory(f));
                    return;
                }

                File winDll = new File(f, "vix.dll");
                File linuxSO = new File(f, "libvix.so");
                if (!winDll.exists() && !linuxSO.exists()) {
                    error(Messages.PluginImpl_NotAVixLibraryDirectory(f));
                    return;
                }

                ok();
            }
        }.process();
    }

    /**
     * Gets the current name-value pairs of virtual machine names and IP addresses.
     *
     * @return The name-value pairs.
     */
    public Map<String, String> getVmIPAddresses() {
        return Collections.unmodifiableMap(vmIPAddresses);
    }

    /**
     * Stapler handler for setting a VM IP.
     *
     * @param req The request.
     * @param rsp The response.
     * @throws IOException If there are problems with IO.
     */
    public void doSet(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        String ip1 = req.getParameter("override");
        String ip2 = req.getRemoteAddr();
        String ip = ip1 == null ? ip2 : ip1;
        if (key == null) {
            w.append(Messages.PluginImpl_MissingParameterName() + "\n");
            w.append(Messages.PluginImpl_HowToOverrideIP() + "\n");
        } else {
            w.append(Messages.PluginImpl_IPAddressSet(key, ip) + "\n");
            setVMIP(key, ip);
        }
        w.append(Messages.PluginImpl_RequestOriginatedFrom(ip2));
        w.close();
    }

    /**
     * Stapler handler for unsetting a VM IP.
     *
     * @param req The request.
     * @param rsp The response.
     * @throws IOException If there are problems with IO.
     */
    public void doUnset(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        if (key == null) {
            w.append(Messages.PluginImpl_MissingParameterName() + "\n");
        } else {
            w.append(Messages.PluginImpl_IPAddressCleared(key) + "\n");
            clearVMIP(key);
        }
        w.append(Messages.PluginImpl_RequestOriginatedFrom(req.getRemoteAddr()));
        w.close();
    }

    /**
     * Stapler handler for querying a VM IP.
     *
     * @param req The request.
     * @param rsp The response.
     * @throws IOException If there are problems with IO.
     */
    public void doQuery(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        if (key == null) {
            w.append(Messages.PluginImpl_MissingParameterName() + "\n");
        } else {
            w.append(getVMIP(key));
        }
        w.close();
    }

    /**
     * Waits until the specified key has been set. Will return immediately if the key is already set.
     *
     * @param key The key to look for.
     * @throws InterruptedException If interrupted.
     */
    public static void awaitVMIP(String key) throws InterruptedException {
        if (vmIPAddresses.containsKey(key)) {
            return;
        }
        watchVMIP(key);
        final CountDownLatch latch = nameLatches.get(key);
        assert latch != null;
        latch.await();
    }

    /**
     * Waits at most <code>timeout</code> for the specified key to be set.  Will return immediately if the key is
     * already set.
     *
     * @param key     The key to look for.
     * @param timeout The timeout.
     * @param unit    The units of the timeout.
     * @return <code>true</code> if the key has been set.
     * @throws InterruptedException If interrupted.
     */
    public static boolean awaitVMIP(String key, long timeout, TimeUnit unit) throws InterruptedException {
        if (vmIPAddresses.containsKey(key)) {
            return true;
        }
        watchVMIP(key);
        final CountDownLatch latch = nameLatches.get(key);
        assert latch != null;
        return latch.await(timeout, unit);
    }

    public static void watchVMIP(String key) {
        if (!nameLatches.containsKey(key)) {
            nameLatches.putIfAbsent(key, new CountDownLatch(1));
        }
    }

    /**
     * Sets the key, releasing any threads that were waiting for it to be set.
     *
     * @param key The name.
     * @param ip  The value.
     */
    public static void setVMIP(String key, String ip) {
        vmIPAddresses.put(key, ip);
        final CountDownLatch latch = nameLatches.get(key);
        if (latch != null) {
            latch.countDown();
            nameLatches.remove(key, latch);
        }
    }

    /**
     * Clears the key.
     *
     * @param key The name.
     */
    public static void clearVMIP(String key) {
        vmIPAddresses.remove(key);
    }

    /**
     * Returns the current value of the key.
     *
     * @param key The key.
     * @return The current value or <code>null</code> if empty.
     */
    public static String getVMIP(String key) {
        return vmIPAddresses.get(key);
    }

    /**
     * Gets a set of all the current names.
     *
     * @return all the current names.
     */
    public static Set<String> getVMs() {
        return Collections.unmodifiableSet(vmIPAddresses.keySet());
    }

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());
}
