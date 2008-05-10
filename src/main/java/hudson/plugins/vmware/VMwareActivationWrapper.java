package hudson.plugins.vmware;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapper;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 26-Sep-2007 16:06:28
 */
public class VMwareActivationWrapper extends BuildWrapper implements ResourceActivity {
    public transient String vixLibraryPath;
    public transient String hostName;
    public transient String username;
    public transient String password;
    public transient int portNumber;
    public transient boolean suspend;
    public transient boolean waitForTools;
    public transient boolean revert;
    public transient String configFile;
    private List<VMActivationConfig> machines;

    private void importOldConfig() {
        assert machines == null;
        machines = new ArrayList<VMActivationConfig>();
        VMwareHostConfig hostConfig = null;
        if (vixLibraryPath != null) {
            // pull legacy config
            hostConfig = new VMwareHostConfig(vixLibraryPath, hostName, portNumber, Host.HostType.VMWARE_SERVER, username, password);
            boolean found = false;
            for (VMwareHostConfig h : DESCRIPTOR.getHosts()) {
                if (h.equals(hostConfig)) {
                    found = true;
                    hostConfig = h;
                    break;
                }
            }
            if (!found) {
                hostConfig.setName(hostConfig.getUsername() + "@" + hostConfig.getHostType() + ":" + hostConfig
                        .getPortNumber());
                DESCRIPTOR.addHost(hostConfig);
            }

        }
        if (hostConfig == null) {
            hostConfig = DESCRIPTOR.getHosts().get(0);
        }
        if (configFile != null && !"".equals(configFile)) {
            VMActivationConfig cfg = new VMActivationConfig();
            cfg.setVmxFilePath(configFile);
            cfg.setHostConfig(hostConfig);
            if (waitForTools) {
                cfg.setPowerUpMode(revert ? VMWrapperPowerUpMode.REVERT_WAIT : VMWrapperPowerUpMode.NORMAL_WAIT);
            } else {
                cfg.setPowerUpMode(revert ? VMWrapperPowerUpMode.REVERT : VMWrapperPowerUpMode.NORMAL);
            }
            cfg.setPowerDownMode(suspend ? VMWrapperPowerDownMode.SUSPEND : VMWrapperPowerDownMode.NORMAL);
            machines.add(cfg);
        }
    }

    public List<VMActivationConfig> getMachines() {
        if (machines == null) {
            importOldConfig();
        }
        return machines;
    }

    public void setMachines(List<VMActivationConfig> machines) {
        this.machines = machines;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener buildListener) throws IOException, InterruptedException {
        final class VMC {
            private final VirtualMachine vm;
            private final Host host;
            private long powerTime;
            private final VMActivationConfig cfg;

            public VMC(VirtualMachine vm, Host host, VMActivationConfig cfg) {
                this.vm = vm;
                this.host = host;
                this.powerTime = powerTime;
                this.cfg = cfg;
            }

            public void prepareFeedback(BuildListener listener) {
                if (cfg.isWaitForFeedback()) {
                    listener.getLogger().println("[VMware] Unsetting " + cfg.getFeedbackKey() + " for "
                            + cfg.getVmxFilePath() + " on " + cfg.getHost());
                    PluginImpl.clearVMIP(cfg.getFeedbackKey());
                    PluginImpl.watchVMIP(cfg.getFeedbackKey());
                }
            }

            public boolean awaitFeedback(BuildListener listener) {
                if (cfg.isWaitForFeedback()) {
                    listener.getLogger().println("[VMware] Waiting on " + cfg.getFeedbackKey() + " from "
                            + cfg.getVmxFilePath() + " on " + cfg.getHost());
                    try {
                        final boolean result = PluginImpl.awaitVMIP(cfg.getFeedbackKey(),
                                cfg.getFeedbackTimeout(), TimeUnit.SECONDS);
                        if (result) {
                            listener.getLogger().println("[VMware] " + cfg.getFeedbackKey() + "=" + PluginImpl
                                    .getVMIP(cfg.getFeedbackKey()));
                        } else {
                            listener.getLogger().println("[VMware] Timed out!");
                        }
                        return result;
                    } catch (InterruptedException e) {
                        listener.getLogger().println("[VMware] Interrupted...");
                        e.printStackTrace(listener.getLogger());
                        return false;
                    }
                }
                return true;
            }

            public void powerUp(BuildListener listener) {
                listener.getLogger()
                        .println("[VMware] Pre-build for " + cfg.getVmxFilePath() + " on " + cfg.getHost() + "...");
                switch (cfg.getPowerUpMode()) {
                    case NOTHING:
                        break;
                    case REVERT:
                    case REVERT_WAIT:
                        listener.getLogger().println("[VMware] Reverting virtual machine to current snapshot.");
                        vm.revertToSnapshot();
                    case NORMAL:
                    case NORMAL_WAIT:
                        listener.getLogger().println("[VMware] Powering up virtual machine.");
                        vm.powerOn();
                        break;
                    default:
                        break;
                }
                switch (cfg.getPowerUpMode()) {
                    case NORMAL_WAIT:
                    case REVERT_WAIT:
                        listener.getLogger()
                                .println("[VMware] Waiting for VMware Tools to start in virtual machine.");
                        vm.waitForToolsInGuest(cfg.waitTimeout);
                        break;
                    default:
                        break;
                }
                powerTime = System.currentTimeMillis();
                listener.getLogger().println("[VMware] Pre-build for " + cfg.getVmxFilePath() + " on " + cfg
                        .getHost() + " completed.");
            }

            public void powerDown(BuildListener buildListener) {
                buildListener.getLogger().println("[VMware] Post-build for " + cfg.getVmxFilePath() + " on " + cfg
                        .getHost() + "...");

                while (System.currentTimeMillis() < powerTime + 10000L) {
                    buildListener.getLogger().println("[VMware] Ensuring VM has completed BIOS boot sequence...");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }

                switch (cfg.getPowerDownMode()) {
                    case CREATE_POWER_OFF:
                        buildListener.getLogger().println("[VMware] Taking snapshot.");
                        vm.createSnapshot("", "", true);
                        break;
                    case NOTHING:
                    case NORMAL:
                    case SUSPEND:
                    case CREATE_SUSPEND:
                    case CREATE_NORMAL:
                    default:
                        break;
                }

                switch (cfg.getPowerDownMode()) {
                    case SUSPEND:
                    case CREATE_SUSPEND:
                        buildListener.getLogger().println("[VMware] Suspending virtual machine.");
                        vm.suspend();
                        break;
                    case NORMAL:
                    case CREATE_POWER_OFF:
                    case CREATE_NORMAL:
                        buildListener.getLogger().println("[VMware] Powering off virtual machine.");
                        vm.powerOff();
                        break;
                    case NOTHING:
                    default:
                        break;
                }

                switch (cfg.getPowerDownMode()) {
                    case CREATE_SUSPEND:
                        buildListener.getLogger().println("[VMware] Taking snapshot.");
                        vm.createSnapshot("", "", true);
                        break;
                    case CREATE_NORMAL:
                        buildListener.getLogger().println("[VMware] Taking snapshot.");
                        vm.createSnapshot("", "", false);
                        break;
                    case NOTHING:
                    case NORMAL:
                    case SUSPEND:
                    case CREATE_POWER_OFF:
                    default:
                        break;
                }

                vm.close();
                buildListener.getLogger().println("[VMware] Disconnecting");
                host.disconnect();
                buildListener.getLogger().println("[VMware] Done");

                buildListener.getLogger().println("[VMware] Post-build for " + cfg.getVmxFilePath() + " on " + cfg
                        .getHost() + " completed.");
            }

        }
        class EnvironmentImpl extends Environment {
            private final List<VMC> vms;

            public EnvironmentImpl(List<VMC> vms) {
                this.vms = vms;
            }

            public boolean tearDown(Build build, BuildListener buildListener) throws IOException, InterruptedException {
                for (VMC vm : vms) {
                    vm.powerDown(buildListener);
                }
                return true;
            }

            @Override
            public void buildEnvVars(Map<String, String> map) {
                super.buildEnvVars(map);
                for (String key : PluginImpl.getVMs()) {
                    map.put(key, PluginImpl.getVMIP(key));
                }
            }
        }
        List<VMC> vms = new ArrayList<VMC>();
        try {
            VMware library = null;
            String lastLibrary = null;
            for (VMActivationConfig machine : machines) {
                final VMwareHostConfig config = machine.getHostConfig();
                if (lastLibrary == null || !lastLibrary.equals(config.getVixLibraryPath())) {
                    lastLibrary = config.getVixLibraryPath();
                    library = new VMware(lastLibrary);
                }
                buildListener.getLogger()
                        .println("[VMware] Connecting to VMware Server host " + config.getHostName() +
                                ":" + config.getPortNumber() + " as user " + config.getUsername());
                Host host = library.connect(config.getHostType(), config.getHostName(), config.getPortNumber(),
                        config.getUsername(), config.getPassword());
                try {
                    buildListener.getLogger().println("[VMware] Opening virtual machine: " + machine.getVmxFilePath());
                    VirtualMachine vm = host.open(machine.getVmxFilePath());
                    try {
                        final VMC vmc = new VMC(vm, host, machine);
                        vmc.prepareFeedback(buildListener);
                        vmc.powerUp(buildListener);
                        vms.add(vmc);
                    } catch (VMwareRuntimeException e) {
                        vm.close();
                        throw e;
                    }
                } catch (VMwareRuntimeException e) {
                    host.disconnect();
                    throw e;
                }
            }
        } catch (VMwareRuntimeException e) {
            buildListener.getLogger().println("[VMware] VMware VIX error: " + e.getMessage());
            e.printStackTrace(buildListener.getLogger());
            build.setResult(Result.FAILURE);
            for (VMC vmc : vms) {
                vmc.powerDown(buildListener);
            }
            return null;
        }
        for (VMC vmc : vms) {
            if (!vmc.awaitFeedback(buildListener)) {
                build.setResult(Result.FAILURE);
                for (VMC vmc2 : vms) {
                    vmc2.powerDown(buildListener);
                }
                return null;
            }
        }
        return new EnvironmentImpl(vms);
    }

    public Descriptor<BuildWrapper> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public ResourceList getResourceList() {
        ResourceList resources = new ResourceList();
        for (VMActivationConfig machine : machines) {
            resources.w(new Resource(machine.getHost() + "/" + machine.getVmxFilePath()));
        }
        return resources;
    }

    public String getDisplayName() {
        return DESCRIPTOR.getDisplayName();
    }

    public static final class DescriptorImpl extends Descriptor<BuildWrapper> {
        private List<VMwareHostConfig> hosts;

        DescriptorImpl() {
            super(VMwareActivationWrapper.class);
            load();
        }

        public String getDisplayName() {
            return "VMware Server VIX Virtual Machine Activation";
        }

        public VMwareActivationWrapper newInstance(StaplerRequest req) throws FormException {
            VMwareActivationWrapper w = new VMwareActivationWrapper();
            req.bindParameters(w, "vmware-activation.");
            w.setMachines(req.bindParametersToList(VMActivationConfig.class, "vmware-activation.machine."));
            return w;
        }

        public boolean configure(StaplerRequest req) throws FormException {
            req.bindParameters(this, "vmware.");
            hosts = req.bindParametersToList(VMwareHostConfig.class, "vmware.host.");
            save();
            return super.configure(req);
        }

        public List<VMwareHostConfig> getHosts() {
            if (hosts == null) {
                hosts = new ArrayList<VMwareHostConfig>();
                // provide default if we have none
                hosts.add(new VMwareHostConfig(true));
            }
            return hosts;
        }

        public void setHosts(List<VMwareHostConfig> hosts) {
            this.hosts = hosts;
        }

        public VMwareHostConfig getHost(String name) {
            for (VMwareHostConfig host : hosts) {
                if (name.equals(host.getName())) {
                    return host;
                }
            }
            return null;
        }

        public String[] getHostNames() {
            String[] result = new String[hosts.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = hosts.get(i).getName();
            }
            return result;
        }

        public void addHost(VMwareHostConfig hostConfig) {
            hosts.add(hostConfig);
            save();
        }

        public String findDefaultVixLibraryPath() {
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                if (new File("C:\\Program Files\\VMware\\VMware VIX\\server-1\\32bit\\vix.dll").exists()) {
                    return "C:\\Program Files\\VMware\\VMware VIX\\server-1\\32bit";
                } else if (new File("C:\\Program Files\\VMware\\VMware VIX\\vix.dll").exists()) {
                    return "C:\\Program Files\\VMware\\VMware VIX";
                } else {
                    return "";
                }
            } else {
                if (new File("/usr/lib/vmware-vix/lib/server-1/32bit/libvix.so").exists()) {
                    return "/usr/lib/vmware-vix/lib/server-1/32bit";
                } else if (new File("/usr/lib/vmware-vix/lib/libvix.so").exists()) {
                    return "/usr/lib/vmware-vix/lib";
                } else {
                    return "";
                }
            }
        }

        public VMWrapperPowerUpMode[] getPowerUpModes() {
            return VMWrapperPowerUpMode.values();
        }

        public VMWrapperPowerDownMode[] getPowerDownModes() {
            return VMWrapperPowerDownMode.values();
        }

    }

    public static final class VMwareHostConfig implements Serializable {
        public String name;
        public String vixLibraryPath;
        public String hostName;
        public int portNumber;
        public Host.HostType hostType;
        public String username;
        public String password;

        public VMwareHostConfig() {
        }

        public VMwareHostConfig(boolean dummy) {
            name = "(default)";
            hostType = Host.HostType.VMWARE_SERVER;
            portNumber = 902;
            vixLibraryPath = DESCRIPTOR.findDefaultVixLibraryPath();
            hostName = "";
            username = "";
            password = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public VMwareHostConfig(String vixLibraryPath, String hostName, int portNumber, Host.HostType hostType, String username, String password) {
            this.vixLibraryPath = vixLibraryPath;
            this.hostName = hostName;
            this.portNumber = portNumber;
            this.hostType = hostType;
            this.username = username;
            this.password = password;
        }

        /**
         * Getter for property 'vixLibraryPath'.
         *
         * @return Value for property 'vixLibraryPath'.
         */
        public String getVixLibraryPath() {
            return vixLibraryPath == null || "".equals(vixLibraryPath) ?
                    DESCRIPTOR.findDefaultVixLibraryPath() :
                    vixLibraryPath;
        }

        /**
         * Setter for property 'vixLibraryPath'.
         *
         * @param vixLibraryPath Value to set for property 'vixLibraryPath'.
         */
        public void setVixLibraryPath(String vixLibraryPath) {
            this.vixLibraryPath = vixLibraryPath;
        }

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
         * Setter for property 'portNumber'.
         *
         * @param portNumber Value to set for property 'portNumber'.
         */
        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber == 0 ? 902 : portNumber;
        }

        /**
         * Getter for property 'hostType'.
         *
         * @return Value for property 'hostType'.
         */
        public Host.HostType getHostType() {
            return hostType == null ? Host.HostType.VMWARE_SERVER : hostType;
        }

        /**
         * Setter for property 'hostType'.
         *
         * @param hostType Value to set for property 'hostType'.
         */
        public void setHostType(Host.HostType hostType) {
            this.hostType = hostType;
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

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VMwareHostConfig that = (VMwareHostConfig) o;

            if (portNumber != that.portNumber) return false;
            if (hostName != null ? !hostName.equals(that.hostName) : that.hostName != null) return false;
            if (hostType != that.hostType) return false;
            if (username != null ? !username.equals(that.username) : that.username != null) return false;
            if (vixLibraryPath != null ? !vixLibraryPath.equals(that.vixLibraryPath) : that.vixLibraryPath != null)
                return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (vixLibraryPath != null ? vixLibraryPath.hashCode() : 0);
            result = 31 * result + (hostName != null ? hostName.hashCode() : 0);
            result = 31 * result + portNumber;
            result = 31 * result + (hostType != null ? hostType.hashCode() : 0);
            result = 31 * result + (username != null ? username.hashCode() : 0);
            return result;
        }
    }

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
    }

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
    }

    public static final class VMActivationConfig implements Serializable {
        private String vmxFilePath;
        private String host;
        private transient VMwareHostConfig hostConfig;
        private VMWrapperPowerUpMode powerUpMode;
        private VMWrapperPowerDownMode powerDownMode;
        private int waitTimeout;
        private boolean waitForFeedback;
        private String feedbackKey;
        private int feedbackTimeout;

        public VMActivationConfig() {
        }

        public int getWaitTimeout() {
            return waitTimeout <= 0 ? 300 : waitTimeout;
        }

        public void setWaitTimeout(int waitTimeout) {
            this.waitTimeout = waitTimeout;
        }

        public String getVmxFilePath() {
            return vmxFilePath;
        }

        public void setVmxFilePath(String vmxFilePath) {
            this.vmxFilePath = vmxFilePath;
        }

        public VMwareHostConfig getHostConfig() {
            if (hostConfig == null && host != null && !"".equals(host)) {
                setHostConfig(DESCRIPTOR.getHost(host));
            }
            return hostConfig;
        }

        public void setHostConfig(VMwareHostConfig hostConfig) {
            this.hostConfig = hostConfig;
        }

        public VMWrapperPowerUpMode getPowerUpMode() {
            return powerUpMode;
        }

        public void setPowerUpMode(VMWrapperPowerUpMode powerUpMode) {
            this.powerUpMode = powerUpMode;
        }

        public VMWrapperPowerDownMode getPowerDownMode() {
            return powerDownMode;
        }

        public void setPowerDownMode(VMWrapperPowerDownMode powerDownMode) {
            this.powerDownMode = powerDownMode;
        }

        public String getPreBuild() {
            return powerUpMode == null ? null : powerUpMode.toString();
        }

        public void setPreBuild(String powerUpMode) {
            this.powerUpMode = VMWrapperPowerUpMode.valueOf(powerUpMode);
        }

        public String getPostBuild() {
            return powerDownMode == null ? null : powerDownMode.toString();
        }

        public void setPostBuild(String powerDownMode) {
            this.powerDownMode = VMWrapperPowerDownMode.valueOf(powerDownMode);
        }

        public String getHost() {
            if (hostConfig == null) {
                return host;
            }
            return host = hostConfig.getName();
        }

        public void setHost(String name) {
            setHostConfig(DESCRIPTOR.getHost(host = name));
        }


        public boolean isWaitForFeedback() {
            return waitForFeedback && feedbackKey != null && feedbackKey.trim().length() > 0;
        }

        public void setWaitForFeedback(boolean waitForFeedback) {
            this.waitForFeedback = waitForFeedback;
        }

        public String getFeedbackKey() {
            return feedbackKey;
        }

        public void setFeedbackKey(String feedbackKey) {
            this.feedbackKey = feedbackKey;
        }

        public int getFeedbackTimeout() {
            return feedbackTimeout < 0 ? 300 : feedbackTimeout;
        }

        public void setFeedbackTimeout(int feedbackTimeout) {
            this.feedbackTimeout = feedbackTimeout;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(VMwareActivationWrapper.class.getName());
}
