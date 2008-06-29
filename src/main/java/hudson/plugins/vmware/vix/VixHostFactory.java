package hudson.plugins.vmware.vix;

import hudson.plugins.vmware.Host;
import hudson.plugins.vmware.vix.VixHostConfig;
import hudson.plugins.vmware.VMwareRuntimeException;
import hudson.plugins.vmware.PluginImpl;
import com.sun.jna.ptr.IntByReference;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 21:59:15
 */
public class VixHostFactory {

    Vix instance;

    private VMware(String pathToLib) {
        instance = PluginImpl.getVixInstance(pathToLib);
    }



    public Host connect(VixHostConfig config) {
        return new VixHost(instance,
                config.getHostType(),
                config.getHostName(),
                config.getPortNumber(),
                config.getUsername(),
                config.getPassword());
    }

    void waitForJob(int jobHandle) {
        checkError(instance.VixJob_Wait(jobHandle, Vix.Property.NONE));
    }

    int waitForJobAndGetJobResultHandle(int jobHandle) {
        IntByReference result = new IntByReference();
        checkError(instance.VixJob_Wait(jobHandle, Vix.Property.JOB_RESULT_HANDLE, result, Vix.Property.NONE));
        return result.getValue();
    }

    void checkError(int err) {
        if (err != Vix.Error.OK) {
            final String errorMessage = instance.Vix_GetErrorText(err, null);
            LOGGER.warning(errorMessage);
            throw new VMwareRuntimeException(errorMessage);
        }
    }
}
