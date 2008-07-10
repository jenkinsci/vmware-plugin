package hudson.plugins.vmware;

import java.util.List;

import hudson.model.Descriptor;
import hudson.plugins.vmware.vix.VixHost;
import hudson.plugins.vmware.vix.VixHostConfig;
import hudson.plugins.vmware.vix.VixLibraryManager;
import hudson.plugins.vmware.vix.VixVirtualComputer;
import hudson.plugins.vmware.vix.VixVirtualComputerConfig;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.SlaveComputer;
import hudson.slaves.JNLPLauncher;
import hudson.util.StreamTaskListener;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 20-May-2008 21:48:04
 */
public class VMwareLauncher extends JNLPLauncher {

    private final VixVirtualComputerConfig virtualMachine;

    @DataBoundConstructor
    public VMwareLauncher(VixVirtualComputerConfig virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    public void launch(SlaveComputer slaveComputer, StreamTaskListener streamTaskListener) {
        streamTaskListener.getLogger().println("[VMware] Opening virtual machine...");
        VixHost host = VixLibraryManager.getHostInstance(virtualMachine.getHost());
        VixVirtualComputer vm = host.open(virtualMachine);
        try {
            streamTaskListener.getLogger().println("[VMware] Powering up virtual machine...");
            vm.powerOn();
            streamTaskListener.getLogger().println("[VMware] Launching slave process...");
        } finally {
            host.close(vm);
        }
        super.launch(slaveComputer, streamTaskListener);
    }



    public void afterDisconnect(SlaveComputer slaveComputer, StreamTaskListener streamTaskListener) {
        super.afterDisconnect(slaveComputer, streamTaskListener);
        streamTaskListener.getLogger().println("[VMware] Closing virtual machine...");
        VixHost host = VixLibraryManager.getHostInstance(virtualMachine.getHost());
        VixVirtualComputer vm = host.open(virtualMachine);
        try {
        streamTaskListener.getLogger().println("[VMware] Powering down virtual machine...");
        vm.powerOff();
        } finally {
            host.close(vm);
        }
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
