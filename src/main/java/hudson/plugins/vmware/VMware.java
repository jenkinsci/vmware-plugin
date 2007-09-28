/*
 * Copyright (c) 2007 Avaya Inc.
 *
 * All rights reserved.
 */

package hudson.plugins.vmware;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import hudson.plugins.vmware.vix.Vix;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since 28-Sep-2007 09:59:20
 */
public class VMware {
    private static final Logger LOGGER = Logger.getLogger(VMware.class.getName());
    private final Vix instance;

    public VMware() {
        instance = (Vix) Native.loadLibrary("vix", Vix.class);
    }

    public VMware(String pathToLib) {
        final String oldProp = System.getProperty("jna.library.path");
        try {
            System.setProperty("jna.library.path", pathToLib);
            instance = (Vix) Native.loadLibrary("vix", Vix.class);
        } finally {
            if (oldProp != null) {
                System.setProperty("jna.library.path", oldProp);
            }
        }
    }

    public Host connect(Host.HostType hostType, String hostName, int hostPort, String userName, String password) {
        return new Host(this, hostType, hostName, hostPort, userName, password);
    }

    Vix getInstance() {
        return instance;
    }

    int waitForJobAndGetJobResultHandle(int jobHandle) {
        IntByReference result = new IntByReference();
        checkError(instance.VixJob_Wait(jobHandle, Vix.Property.JOB_RESULT_HANDLE, result, Vix.Property.NONE));
        return result.getValue();
    }

    void waitForJob(int jobHandle) {
        checkError(instance.VixJob_Wait(jobHandle, Vix.Property.NONE));
    }

    void checkError(int err) {
        if (err != Vix.Error.OK) {
            final String errorMessage = instance.Vix_GetErrorText(err, null);
            LOGGER.warning(errorMessage);
            throw new VMwareRuntimeException(errorMessage);
        }
    }

}
