package hudson.plugins.vmware;

import hudson.Plugin;
import hudson.util.FormFieldValidator;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildWrappers;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Entry point of vmware plugin.
 *
 * @author Stephen Connolly
 * @plugin
 */
public class PluginImpl extends Plugin {
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


    private static final java.util.logging.Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());
}
