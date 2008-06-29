package hudson.plugins.vmware;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.plugins.vmware.vix.VixHostConfig;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 22:03:12
 */
public class VMwareVMConfig {
    private final VixHostConfig host;
    private final String vmxFilePath;

    @DataBoundConstructor
    public VMwareVMConfig(String vmxFilePath, VixHostConfig host) {
        this.vmxFilePath = vmxFilePath;
        this.host = host;
    }

    /**
     * Getter for property 'host'.
     *
     * @return Value for property 'host'.
     */
    public VixHostConfig getHost() {
        return host;
    }

    /**
     * Getter for property 'vmxFilePath'.
     *
     * @return Value for property 'vmxFilePath'.
     */
    public String getVmxFilePath() {
        return vmxFilePath;
    }
}
