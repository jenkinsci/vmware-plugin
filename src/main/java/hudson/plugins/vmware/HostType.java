package hudson.plugins.vmware;

import org.kohsuke.stapler.Stapler;
import hudson.util.EnumConverter;

/**
 * TODO javadoc.
*
* @author Stephen Connolly
* @since 20-May-2008 22:55:55
*/
public enum HostType {
    VMWARE_SERVER;

    static {
        Stapler.CONVERT_UTILS.register(new EnumConverter(), HostType.class);
    }
}
