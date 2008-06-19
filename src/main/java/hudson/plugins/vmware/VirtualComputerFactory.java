package hudson.plugins.vmware;

import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;

/**
 * A factory for creating {@linkplain VirtualComputer}s.
 *
 * @author Stephen Connolly
 */
public abstract class VirtualComputerFactory {
    public abstract VirtualComputer newInstance(StaplerRequest staplerRequest, JSONObject jsonObject);
}
