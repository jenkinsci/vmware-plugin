package hudson.plugins.vmware.vix;

import com.sun.jna.ptr.IntByReference;
import hudson.plugins.vmware.VMwareRuntimeException;

import java.util.logging.Logger;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 21:57:34
 */
public class VixVirtualComputer extends VixObject {
    private VixVirtualComputerConfig config;
    private final Object handleLock = new Object();

    public static VixVirtualComputer newInstance(VixHost hostPath, VixVirtualComputerConfig config) {
        return new VixVirtualComputer(hostPath.getLibrary(), hostPath.getHandle(), config);
    }

    private static final Logger LOGGER = Logger.getLogger(VixVirtualComputer.class.getName());
    private int handle = 0;

    VixVirtualComputer(Vix library, int hostHandle, VixVirtualComputerConfig config) {
        super(library);
        this.config = config;
        open(hostHandle);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("VixVirtualComputer");
        buf.append('[');
        buf.append("config = ");
        buf.append(config);
        buf.append(']');
        buf.append("->");
        buf.append(super.toString());
        return buf.toString();
    }

    private void open(int hostHandle) {
        int jobHandle = 0;
        try {
            LOGGER.info("Trying to open virtual machine " + config.getVmxFilePath());

            jobHandle = getLibrary().VixVM_Open(hostHandle, config.getVmxFilePath(), null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            handle = waitForJobAndGetJobResultHandle(jobHandle);

            LOGGER.info("Opened");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void close() {
        getLibrary().Vix_ReleaseHandle(handle);
        handle = 0;
    }

    public void powerOn() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Powering up virtual machine");

            jobHandle = getLibrary().VixVM_PowerOn(handle, Vix.VMPPowerOp.NORMAL, Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Powered up");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void powerOff() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = getLibrary().VixVM_PowerOff(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void suspend() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = getLibrary().VixVM_Suspend(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void reset() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = getLibrary().VixVM_Reset(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void waitForToolsInGuest(int timeoutInSeconds) {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Waiting for Tools to start in guest");

            jobHandle = getLibrary().VixVM_WaitForToolsInGuest(handle, timeoutInSeconds, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Tools started in guest");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void guestLogin(String username, String password) {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Login...");

            jobHandle = getLibrary().VixVM_LoginInGuest(handle, username, password, 0, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Login OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void guestLoginAnonymous() {
        guestLogin(Vix.UserName.ANONYMOUS, null);
    }

    public void guestLoginAdministrator() {
        guestLogin(Vix.UserName.ADMINISTRATOR, null);
    }

    public void guestLoginConsole() {
        guestLogin(Vix.UserName.CONSOLE, null);
    }

    public void guestLogout() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Logout...");

            jobHandle = getLibrary().VixVM_LogoutFromGuest(handle, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Logout OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public int guestExec(String execGuestPath, String args, boolean wait, boolean activateWindow) {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info(wait ? "Executing process in guest" : "Spawning process in guest");

            jobHandle = getLibrary().VixVM_RunProgramInGuest(handle, execGuestPath, args,
                    (wait ? 0 : Vix.RunProgram.RETURN_IMMEDIATELY) +
                            (activateWindow ? Vix.RunProgram.ACTIVATE_WINDOW : 0),
                    Vix.Handle.INVALID, null, null);

            LOGGER.fine("Waiting...");

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            waitForJob(jobHandle);

            LOGGER.info("Exec OK");

            IntByReference exitCode = new IntByReference();
            checkError(getLibrary().Vix_GetProperties(jobHandle,
                    Vix.Property.JOB_RESULT_GUEST_PROGRAM_EXIT_CODE,
                    exitCode,
                    Vix.Property.NONE));

            return exitCode.getValue();
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public int guestExec(String execGuestPath, String args, boolean wait) {
        return guestExec(execGuestPath, args, wait, false);
    }

    public int guestExec(String execGuestPath, String args) {
        return guestExec(execGuestPath, args, true, false);
    }

    public void guestOpenUrl(String url) {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Open URL in guest");

            jobHandle = getLibrary().VixVM_OpenUrlInGuest(handle, url, 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Url opened OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void createSnapshot(String name, String description, boolean includeMemory) {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Creating snapshot");

            jobHandle = getLibrary().VixVM_CreateSnapshot(handle, name, description, includeMemory ? Vix.Snapshot.INCLUDE_MEMORY : 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Created snapshot OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void removeSnapshot() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Removing snapshot");

            IntByReference snapshotHandle = new IntByReference();
            checkError(getLibrary().VixVM_GetRootSnapshot(handle, 0, snapshotHandle));

            jobHandle = getLibrary().VixVM_RemoveSnapshot(handle, snapshotHandle.getValue(), 0, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Removed snapshot OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void revertToSnapshot() {
        checkOpen();

        int jobHandle = 0;
        try {
            LOGGER.info("Reverting to snapshot");

            IntByReference snapshotHandle = new IntByReference();
            checkError(getLibrary().VixVM_GetRootSnapshot(handle, 0, snapshotHandle));

            jobHandle = getLibrary().VixVM_RevertToSnapshot(handle, snapshotHandle.getValue(), 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            waitForJob(jobHandle);

            LOGGER.info("Revert to snapshot OK");
        } finally {
            getLibrary().Vix_ReleaseHandle(jobHandle);
        }
    }

    private void checkOpen() {
        synchronized (handleLock) {
        if (handle == 0 || getLibrary() == null) {
            throw new IllegalStateException("Not connected.");
        }
        }
    }


}
