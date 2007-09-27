package hudson.plugins.vmware;

import hudson.Plugin;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildWrappers;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.net.URL;
import java.io.File;
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
        // 1. Check if we are running on windows

        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        // 2. Try and load the JNI library

        boolean libraryOK = false;

        try {
        if (isWindows) {
            LOGGER.info("OK, Running windows");
            final URL url = getClass().getResource("/VMwareVIXJNI.dll");
            final String dllUrl = url.toString();
            LOGGER.info("dllURL="+dllUrl);

            if (dllUrl.startsWith(URL_PREFIX)) {
                final String dllPath = dllUrl.substring(URL_PREFIX.length()).replace('/', '\\');
                final File dllFile = new File(dllPath);
                if (dllFile.exists()) {
                    System.load(dllPath);
                    libraryOK = true;
                }
            }
        }
        } catch (Throwable t) {
            LOGGER.severe("Problem loading JNI library: " + t.getMessage());
        }

        if (!libraryOK) {
            LOGGER.severe("Could not load required JNI library for VMware functionality.");
            return;
        }

        // 3. Add the BuildWrapper

        BuildWrappers.WRAPPERS.add(VMwareActivationWrapper.DESCRIPTOR);

//        BuildStep.PUBLISHERS.add(CoberturaPublisher.DESCRIPTOR);
    }

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());
}
