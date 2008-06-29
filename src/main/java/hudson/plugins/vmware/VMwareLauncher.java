package hudson.plugins.vmware;

import hudson.model.Descriptor;
import hudson.plugins.vmware.vix.VixHostConfig;
import hudson.plugins.vmware.vix.VixVirtualComputerConfig;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.SlaveComputer;
import hudson.util.StreamTaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 21:48:04
 */
public class VMwareLauncher extends ComputerLauncher {
    private final VixVirtualComputerConfig virtualMachine;

    @DataBoundConstructor
    public VMwareLauncher(VixVirtualComputerConfig virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void launch(SlaveComputer slaveComputer, StreamTaskListener streamTaskListener) {
        streamTaskListener.getLogger().println("[VMware] Opening virtual machine...");
        VMware instance = VMware.getSingleton(virtualMachine.getHost().getVixLibraryPath());
        Host host = instance.connect(virtualMachine.getHost());
        VirtualMachine vm = host.open(virtualMachine.getVmxFilePath());
        streamTaskListener.getLogger().println("[VMware] Powering up virtual machine...");
        vm.powerOn();
        streamTaskListener.getLogger().println("[VMware] Launching slave process...");

        throw new UnsupportedOperationException("This is not implemented yet");
    }

    public VixVirtualComputerConfig getVirtualMachine() {
        return virtualMachine;
    }

    public Descriptor<ComputerLauncher> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<ComputerLauncher> DESCRIPTOR = new DescriptorImpl();

    private static class DescriptorImpl extends Descriptor<ComputerLauncher> {
        protected DescriptorImpl() {
            super(VMwareLauncher.class);
        }

        public String getDisplayName() {
            return "Launch a VMware virtual machine based slave";
        }

        public List<VixHostConfig> getHosts() {
            return VMwareActivationWrapper.DESCRIPTOR.getHosts();
        }

    }
}
