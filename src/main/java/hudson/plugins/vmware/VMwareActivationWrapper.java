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
import hudson.plugins.vmware.vix.VMWareVIX;
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
    public String hostName;
    public String username;
    public String password;
    public String configFile;
    public int portNumber;
    public boolean suspend;
    public boolean waitForTools;

    public Environment setUp(Build build, Launcher launcher, BuildListener buildListener) throws IOException, InterruptedException {
        class EnvironmentImpl extends Environment {
            private final int vmHandle;
            private final int hostHandle;

            public EnvironmentImpl(int hostHandle, int vmHandle) {
                this.vmHandle = vmHandle;
                this.hostHandle = hostHandle;
            }

            public boolean tearDown(Build build, BuildListener buildListener) throws IOException, InterruptedException {
                if (suspend) {
                    buildListener.getLogger().println("[VMware] Suspending virtual machine.");
                    VMWareVIX.vixVMSuspend(vmHandle);
                } else {
                    buildListener.getLogger().println("[VMware] Powering off virtual machine.");
                    VMWareVIX.vixVMPowerOff(vmHandle);
                }
                buildListener.getLogger().println("[VMware] Disconnecting");
                VMWareVIX.vixHostDisconnect(hostHandle);
                buildListener.getLogger().println("[VMware] Done");
                return true;
            }
        }
        int hostHandle = 0;
        int vmHandle = 0;
        buildListener.getLogger().println("[VMware] Connecting to VMware Server host " + hostName + ":" + portNumber
                + " as user " + username);
        hostHandle = VMWareVIX.vixHostConnect(hostName, username, password, portNumber);
        if (hostHandle == 0) {
            buildListener.getLogger().println("[VMware] Could not connect to VMware Server");
            build.setResult(Result.FAILURE);
            return null;
        }
        try {
            buildListener.getLogger().println("[VMware] Opening virtual machine: " + configFile);
            vmHandle = VMWareVIX.vixVMOpen(hostHandle, configFile);
            buildListener.getLogger().println("[VMware] Powering up virtual machine.");
            VMWareVIX.vixVMPowerOn(vmHandle);
            if (waitForTools) {
                buildListener.getLogger().println("[VMware] Waiting for VMware Tools to start in virtual machine.");
                VMWareVIX.vixVMWaitForToolsInGuest(vmHandle);
            }
            buildListener.getLogger().println("[VMware] Ready");
        } catch (Throwable t) {
            buildListener.getLogger().println("[VMware] Unknown error: " + t.getMessage());
            t.printStackTrace(buildListener.getLogger());
            build.setResult(Result.FAILURE);
            VMWareVIX.vixHostDisconnect(hostHandle);
            return null;
        }
        return new EnvironmentImpl(hostHandle, vmHandle);
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
