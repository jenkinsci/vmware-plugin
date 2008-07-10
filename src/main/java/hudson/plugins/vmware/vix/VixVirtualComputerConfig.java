package hudson.plugins.vmware.vix;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 22:03:12
 */
public final class VixVirtualComputerConfig {
// ------------------------------ FIELDS ------------------------------

    private final VixHostConfig host;
    private final String vmxFilePath;

// --------------------------- CONSTRUCTORS ---------------------------

    @DataBoundConstructor
    public VixVirtualComputerConfig(String vmxFilePath, VixHostConfig host) {
        vmxFilePath.getClass(); // throw NPE if null
        host.getClass(); // throw NPE if null
        this.vmxFilePath = vmxFilePath;
        this.host = host;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

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

// ------------------------ CANONICAL METHODS ------------------------

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VixVirtualComputerConfig that = (VixVirtualComputerConfig) o;

        if (!host.equals(that.host)) return false;
        if (!vmxFilePath.equals(that.vmxFilePath)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int result;
        result = host.hashCode();
        result = 31 * result + vmxFilePath.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "VixVirtualComputerConfig{" +
                "host=" + host +
                ", vmxFilePath='" + vmxFilePath + '\'' +
                '}';
    }

    /**
     * Returns a pseudo-uri for the host.
     *
     * @return The pseudo-uri
     */
    public String toPseudoUri() {
        return host.toPseudoUri() + vmxFilePath.replace('\\','/');
    }

}
