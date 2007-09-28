/*
 * Copyright (c) 2007 Avaya Inc.
 *
 * All rights reserved.
 */

package hudson.plugins.vmware;

import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 26-Sep-2007 16:06:28
 */
public class VMwareActivationWrapper extends BuildWrapper {
    public String vixLibraryPath;
    public String hostName;
    public String username;
    public String password;
    public String configFile;
    public int portNumber;
    public boolean suspend;
    public boolean waitForTools;
    public boolean revert;

    public Environment setUp(Build build, Launcher launcher, BuildListener buildListener) throws IOException, InterruptedException {
        class EnvironmentImpl extends Environment {
            private final VirtualMachine vm;
            private final Host host;
            private final long powerTime;

            public EnvironmentImpl(Host host, VirtualMachine vm, long powerTime) {
                this.vm = vm;
                this.host = host;
                this.powerTime = powerTime;
            }

            public boolean tearDown(Build build, BuildListener buildListener) throws IOException, InterruptedException {
                while (System.currentTimeMillis() < powerTime + 10000L) {
                    buildListener.getLogger().println("[VMware] Ensuring VM has completed BIOS boot sequence...");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
                if (suspend) {
                    buildListener.getLogger().println("[VMware] Suspending virtual machine.");
                    vm.suspend();
                } else {
                    buildListener.getLogger().println("[VMware] Powering off virtual machine.");
                    vm.powerOff();
                }
                vm.close();
                buildListener.getLogger().println("[VMware] Disconnecting");
                host.disconnect();
                buildListener.getLogger().println("[VMware] Done");
                return true;
            }
        }
        try {
            VMware library;
            if (vixLibraryPath == null || "".equals(vixLibraryPath.trim())) {
                library = new VMware();
            } else {
                library = new VMware(vixLibraryPath);
            }
            buildListener.getLogger().println("[VMware] Connecting to VMware Server host " + hostName + ":" + portNumber
                    + " as user " + username);
            Host host = library.connect(Host.HostType.VMWARE_SERVER, hostName, portNumber, username, password);
            try {
                buildListener.getLogger().println("[VMware] Opening virtual machine: " + configFile);
                VirtualMachine vm = host.open(configFile);
                try {
                    if (revert) {
                        buildListener.getLogger().println("[VMware] Reverting virtual machine to current snapshot.");
                        vm.revertToSnapshot();
                    }
                    buildListener.getLogger().println("[VMware] Powering up virtual machine.");
                    vm.powerOn();
                    long powerTime = System.currentTimeMillis();
                    if (waitForTools) {
                        buildListener.getLogger()
                                .println("[VMware] Waiting for VMware Tools to start in virtual machine.");
                        vm.waitForToolsInGuest(0);
                    }
                    buildListener.getLogger().println("[VMware] Ready");
                    return new EnvironmentImpl(host, vm, powerTime);
                } catch (VMwareRuntimeException e) {
                    vm.close();
                    throw e;
                }
            } catch (VMwareRuntimeException e) {
                host.disconnect();
                throw e;
            }
        } catch (VMwareRuntimeException e) {
            buildListener.getLogger().println("[VMware] VMware VIX error: " + e.getMessage());
            e.printStackTrace(buildListener.getLogger());
            build.setResult(Result.FAILURE);
            return null;
        }
    }

    public Descriptor<BuildWrapper> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends Descriptor<BuildWrapper> {
        DescriptorImpl() {
            super(VMwareActivationWrapper.class);
        }

        public String getDisplayName() {
            return "VMware Server VIX Virtual Machine Activation";
        }

        public VMwareActivationWrapper newInstance(StaplerRequest req) throws FormException {
            VMwareActivationWrapper w = new VMwareActivationWrapper();
            req.bindParameters(w, "vmware-activation.");
            return w;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(VMwareActivationWrapper.class.getName());
}
