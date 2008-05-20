package hudson.plugins.vmware;

import org.kohsuke.stapler.Stapler;

import java.io.Serializable;

import hudson.util.EnumConverter;

/**
 * TODO javadoc.
*
* @author Stephen Connolly
* @since 20-May-2008 23:00:22
*/
public enum VMWrapperPowerDownMode implements Serializable {
    NORMAL("Power off"),
    SUSPEND("Suspend"),
    CREATE_POWER_OFF("Take snapshot before power off"),
    CREATE_NORMAL("Take snapshot after power off"),
    CREATE_SUSPEND("Take snapshot after suspend"),
    NOTHING("Do nothing"),;
    private String description;

    VMWrapperPowerDownMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    static {
        Stapler.CONVERT_UTILS.register(new EnumConverter(), VMWrapperPowerDownMode.class);
    }
}
