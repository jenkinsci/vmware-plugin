package hudson.plugins.vmware.vix;

import java.io.Serializable;

import hudson.plugins.vmware.HostType;
import hudson.plugins.vmware.PluginImpl;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Configuration details for a Vix based host.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 22:04:10
 */
public final class VixHostConfig implements Serializable {

// ------------------------------ FIELDS ------------------------------

    /**
     * Field name
     */
    public final String name;
    /**
     * Field vixLibraryPath
     */
    public final String vixLibraryPath;
    /**
     * Field hostName
     */
    public final String hostName;
    /**
     * Field portNumber
     */
    public final int portNumber;
    /**
     * Field hostType
     */
    public final HostType hostType;
    /**
     * Field username
     */
    public final String username;
    /**
     * Field password
     */
    public final String password;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructs a new VixHostConfig.
     */
    public VixHostConfig() {
        name = "(default)";
        hostType = HostType.VMWARE_SERVER;
        portNumber = 902;
        vixLibraryPath = PluginImpl.findDefaultVixLibraryPath();
        hostName = "";
        username = "";
        password = "";
    }

    /**
     * Constructor VixHostConfig creates a new VixHostConfig instance.
     *
     * @param vixLibraryPath of type String
     * @param hostName       of type String
     * @param portNumber     of type int
     * @param hostType       of type HostType
     * @param username       of type String
     * @param password       of type String
     */
    public VixHostConfig(String vixLibraryPath, String hostName, int portNumber, HostType hostType, String username,
                         String password) {
        this.name = hostName;
        this.vixLibraryPath = vixLibraryPath;
        this.hostName = hostName;
        this.portNumber = portNumber == 0 ? 902 : portNumber;
        this.hostType = hostType;
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor VixHostConfig creates a new VixHostConfig instance.
     *
     * @param name           of type String
     * @param vixLibraryPath of type String
     * @param hostName       of type String
     * @param portNumber     of type int
     * @param hostType       of type HostType
     * @param username       of type String
     * @param password       of type String
     */
    @DataBoundConstructor
    public VixHostConfig(String name, String vixLibraryPath, String hostName, int portNumber, HostType hostType,
                         String username, String password) {
        this.name = name;
        this.vixLibraryPath = vixLibraryPath;
        this.hostName = hostName;
        this.portNumber = portNumber == 0 ? 902 : portNumber;
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
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
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
     * Getter for property 'portNumber'.
     *
     * @return Value for property 'portNumber'.
     */
    public int getPortNumber() {
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

// ------------------------ CANONICAL METHODS ------------------------

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VixHostConfig that = (VixHostConfig) o;

        if (portNumber != that.portNumber) {
            return false;
        }
        if (hostName != null ? !hostName.equals(that.hostName) : that.hostName != null) {
            return false;
        }
        if (hostType != that.hostType) {
            return false;
        }
        if (username != null ? !username.equals(that.username) : that.username != null) {
            return false;
        }
        if (vixLibraryPath != null ? !vixLibraryPath.equals(that.vixLibraryPath) : that.vixLibraryPath != null) {
            return false;
        }

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

    /**
     * Returns a pseudo-uri for the host.
     * @return The pseudo-uri
     */
    public String toPseudoUri() {
        return "vix://" + username + "@" + hostName + ":" + portNumber + "/";
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

}
