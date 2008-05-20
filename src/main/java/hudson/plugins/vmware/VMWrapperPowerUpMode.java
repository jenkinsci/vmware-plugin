package hudson.plugins.vmware;

import org.kohsuke.stapler.Stapler;

import java.io.Serializable;

import hudson.util.EnumConverter;

/**
 * TODO javadoc.
*
* @author Stephen Connolly
* @since 20-May-2008 22:58:44
*/
public enum VMWrapperPowerUpMode implements Serializable {
    NORMAL_WAIT("Power up and wait for VMware Tools to start"),
    REVERT_WAIT("Revert to last snapshot, power up and wait for VMware Tools to start"),
    NORMAL("Power up (VMware Tools not installed)"),
    REVERT("Revert to last snapshot and power up (VMware Tools not installed)"),
    NOTHING("Do nothing"),;
    private String description;

    VMWrapperPowerUpMode(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    static {
        Stapler.CONVERT_UTILS.register(new EnumConverter(), VMWrapperPowerUpMode.class);
    }
}
