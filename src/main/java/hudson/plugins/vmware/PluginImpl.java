package hudson.plugins.vmware;

import hudson.Plugin;
import hudson.tasks.BuildWrappers;
import hudson.util.FormFieldValidator;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Entry point of vmware plugin.
 *
 * @author Stephen Connolly
 * @plugin
 */
public class PluginImpl extends Plugin {
    private static final ConcurrentMap<String, String> vmIPAddresses = new ConcurrentHashMap<String, String>();
    private final String URL_PREFIX = "file:/";

    public void start() throws Exception {
        BuildWrappers.WRAPPERS.add(VMwareActivationWrapper.DESCRIPTOR);
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
                    error(f + " is not a directory");
                    return;
                }

                File winDll = new File(f, "vix.dll");
                File linuxSO = new File(f, "libvix.so");
                if (!winDll.exists() && !linuxSO.exists()) {
                    error(f + " doesn't look like a VIX library directory");
                    return;
                }

                ok();
            }
        }.process();
    }

    public Map<String, String> getVmIPAddresses() {
        return Collections.unmodifiableMap(vmIPAddresses);
    }

    public void doSet(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        String ip1 = req.getParameter("override");
        String ip2 = req.getRemoteAddr();
        String ip = ip1 == null ? ip2 : ip1;
        if (key == null) {
            w.append("Must provide the 'name' parameter.\n");
            w.append("If the request is being forwarded through a proxy, the IP address to use can be set using the 'override' parameter.\n");
        } else {
            w.append(key + "=" + ip + "\n");
            setVMIP(key, ip);
        }
        w.append("Request originated from " + ip2 + ".");
        w.close();
    }

    public void doUnset(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        if (key == null) {
            w.append("Must provide the 'name' parameter.\n");
        } else {
            w.append(key + " cleared.\n");
            clearVMIP(key);
        }
        w.append("Request originated from " + req.getRemoteAddr() + ".");
        w.close();
    }

    public void doQuery(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Writer w = rsp.getCompressedWriter(req);
        String key = req.getParameter("name");
        if (key == null) {
            w.append("Must provide the 'name' parameter.\n");
        } else {
            w.append(getVMIP(key));
        }
        w.close();
    }

    public static void setVMIP(String key, String ip) {
        vmIPAddresses.put(key, ip);
    }

    public static void clearVMIP(String key) {
        vmIPAddresses.remove(key);
    }

    public static String getVMIP(String key) {
        return vmIPAddresses.get(key);
    }

    public static Set<String> getVMs() {
        return Collections.unmodifiableSet(vmIPAddresses.keySet());
    }


    private static final java.util.logging.Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());
}
