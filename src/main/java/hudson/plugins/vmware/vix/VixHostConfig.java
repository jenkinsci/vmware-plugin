package hudson.plugins.vmware.vix;

import hudson.plugins.vmware.HostType;
import hudson.plugins.vmware.PluginImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 22:04:10
 */
public final class VixHostConfig implements Serializable {
// ------------------------------ FIELDS ------------------------------

    public String name;
    public String vixLibraryPath;
    public String hostName;
    public int portNumber;
    public HostType hostType;
    public String username;
    public String password;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructs a new VixHostConfig.
     */
    public VixHostConfig() {
    }

    public VixHostConfig(boolean dummy) {
        name = "(default)";
        hostType = HostType.VMWARE_SERVER;
        portNumber = 902;
        vixLibraryPath = PluginImpl.findDefaultVixLibraryPath();
        hostName = "";
        username = "";
        password = "";
    }

    public VixHostConfig(String vixLibraryPath, String hostName, int portNumber, HostType hostType, String username, String password) {
        this.vixLibraryPath = vixLibraryPath;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.hostType = hostType;
        this.username = username;
        this.password = password;
    }

    @DataBoundConstructor
    public VixHostConfig(String name, String vixLibraryPath, String hostName, int portNumber, HostType hostType, String username, String password) {
        this.name = name;
        this.vixLibraryPath = vixLibraryPath;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.hostType = hostType;
        this.username = username;
        this.password = password;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    /**
     * Getter for property 'hostName'.
     *
     * @return Value for property 'hostName'.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Setter for property 'hostName'.
     *
     * @param hostName Value to set for property 'hostName'.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'password'.
     *
     * @return Value for property 'password'.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for property 'password'.
     *
     * @param password Value to set for property 'password'.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for property 'portNumber'.
     *
     * @return Value for property 'portNumber'.
     */
    public int getPortNumber() {
        if (portNumber == 0) {
            portNumber = 902;
        }
        return portNumber;
    }

    /**
     * Getter for property 'username'.
     *
     * @return Value for property 'username'.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for property 'username'.
     *
     * @param username Value to set for property 'username'.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter for property 'hostType'.
     *
     * @param hostType Value to set for property 'hostType'.
     */
    public void setHostType(HostType hostType) {
        this.hostType = hostType;
    }

    /**
     * Setter for property 'vixLibraryPath'.
     *
     * @param vixLibraryPath Value to set for property 'vixLibraryPath'.
     */
    public void setVixLibraryPath(String vixLibraryPath) {
        this.vixLibraryPath = vixLibraryPath;
    }

// ------------------------ CANONICAL METHODS ------------------------

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VixHostConfig that = (VixHostConfig) o;

        if (portNumber != that.portNumber) return false;
        if (hostName != null ? !hostName.equals(that.hostName) : that.hostName != null) return false;
        if (hostType != that.hostType) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (vixLibraryPath != null ? !vixLibraryPath.equals(that.vixLibraryPath) : that.vixLibraryPath != null)
            return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int result;
        result = (vixLibraryPath != null ? vixLibraryPath.hashCode() : 0);
        result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
        result = 31 * result + portNumber;
        result = 31 * result + (hostType != null ? hostType.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "VixHostConfig{" +
                "name='" + name + '\'' +
                ", vixLibraryPath='" + vixLibraryPath + '\'' +
                ", hostName='" + hostName + '\'' +
                ", portNumber=" + portNumber +
                ", hostType=" + hostType +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Getter for property 'hostType'.
     *
     * @return Value for property 'hostType'.
     */
    public HostType getHostType() {
        return hostType == null ? HostType.VMWARE_SERVER : hostType;
    }

    /**
     * Getter for property 'vixLibraryPath'.
     *
     * @return Value for property 'vixLibraryPath'.
     */
    public String getVixLibraryPath() {
        return vixLibraryPath == null || "".equals(vixLibraryPath) ?
                PluginImpl.findDefaultVixLibraryPath() :
                vixLibraryPath;
    }

    /**
     * Setter for property 'portNumber'.
     *
     * @param portNumber Value to set for property 'portNumber'.
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber == 0 ? 902 : portNumber;
    }
}
