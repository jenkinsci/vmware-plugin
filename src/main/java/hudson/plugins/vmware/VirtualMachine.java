package hudson.plugins.vmware;

import com.sun.jna.ptr.IntByReference;
import hudson.plugins.vmware.vix.Vix;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Stephen Connolly
 * @since 28-Sep-2007 09:56:34
 */
public class VirtualMachine {
    private static final Logger LOGGER = Logger.getLogger(Host.class.getName());
    private int handle = 0;
    private final VMware lib;

    VirtualMachine(VMware library, int hostHandle, String configFileHostPath) {
        this.lib = library;
        int jobHandle = 0;
        try {
            LOGGER.info("Trying to open virtual machine " + configFileHostPath);

            jobHandle = lib.getInstance().VixVM_Open(hostHandle, configFileHostPath, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            handle = lib.waitForJobAndGetJobResultHandle(jobHandle);

            LOGGER.info("Opened");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void close() {
        lib.getInstance().Vix_ReleaseHandle(handle);
        handle = 0;
    }

    public void powerOn() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Powering up virtual machine");

            jobHandle = lib.getInstance().VixVM_PowerOn(handle, Vix.VMPPowerOp.NORMAL, Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Powered up");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void powerOff() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = lib.getInstance().VixVM_PowerOff(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void suspend() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = lib.getInstance().VixVM_Suspend(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void reset() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Powering off virtual machine");

            jobHandle = lib.getInstance().VixVM_Reset(handle, Vix.VMPPowerOp.NORMAL, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Powered down");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void waitForToolsInGuest(int timeoutInSeconds) {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Waiting for Tools to start in guest");

            jobHandle = lib.getInstance().VixVM_WaitForToolsInGuest(handle, timeoutInSeconds, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Tools started in guest");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void guestLogin(String username, String password) {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Login...");

            jobHandle = lib.getInstance().VixVM_LoginInGuest(handle, username, password, 0, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Login OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
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
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Logout...");

            jobHandle = lib.getInstance().VixVM_LogoutFromGuest(handle, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Logout OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public int guestExec(String execGuestPath, String args, boolean wait, boolean activateWindow) {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info(wait ? "Executing process in guest" : "Spawning process in guest");

            jobHandle = lib.getInstance().VixVM_RunProgramInGuest(handle, execGuestPath, args,
                    (wait ? 0 : Vix.RunProgram.RETURN_IMMEDIATELY) +
                            (activateWindow ? Vix.RunProgram.ACTIVATE_WINDOW : 0),
                    Vix.Handle.INVALID, null, null);

            LOGGER.fine("Waiting...");

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            lib.waitForJob(jobHandle);

            LOGGER.info("Exec OK");

            IntByReference exitCode = new IntByReference();
            lib.checkError(lib.getInstance().Vix_GetProperties(jobHandle,
                    Vix.Property.JOB_RESULT_GUEST_PROGRAM_EXIT_CODE,
                    exitCode,
                    Vix.Property.NONE));

            return exitCode.getValue();
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public int guestExec(String execGuestPath, String args, boolean wait) {
        return guestExec(execGuestPath, args, wait, false);
    }

    public int guestExec(String execGuestPath, String args) {
        return guestExec(execGuestPath, args, true, false);
    }

    public void guestOpenUrl(String url) {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Open URL in guest");

            jobHandle = lib.getInstance().VixVM_OpenUrlInGuest(handle, url, 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Url opened OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void createSnapshot(String name, String description, boolean includeMemory) {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Creating snapshot");

            jobHandle = lib.getInstance().VixVM_CreateSnapshot(handle, name, description, includeMemory ? Vix.Snapshot.INCLUDE_MEMORY : 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Created snapshot OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void removeSnapshot() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Removing snapshot");

            IntByReference snapshotHandle = new IntByReference();
            lib.checkError(lib.getInstance().VixVM_GetRootSnapshot(handle, 0, snapshotHandle));

            jobHandle = lib.getInstance().VixVM_RemoveSnapshot(handle, snapshotHandle.getValue(), 0, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Removed snapshot OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

    public void revertToSnapshot() {
        if (handle == 0 || lib == null) {
            throw new VMwareRuntimeException("Not connected.");
        }

        int jobHandle = 0;
        try {
            LOGGER.info("Reverting to snapshot");

            IntByReference snapshotHandle = new IntByReference();
            lib.checkError(lib.getInstance().VixVM_GetRootSnapshot(handle, 0, snapshotHandle));

            jobHandle = lib.getInstance().VixVM_RevertToSnapshot(handle, snapshotHandle.getValue(), 0,
                    Vix.Handle.INVALID, null, null);

            if (jobHandle == 0) {
                throw new VMwareRuntimeException("Unknown error");
            }

            LOGGER.fine("Waiting...");

            lib.waitForJob(jobHandle);

            LOGGER.info("Revert to snapshot OK");
        } finally {
            lib.getInstance().Vix_ReleaseHandle(jobHandle);
        }
    }

}
