package hudson.plugins.vmware.vix;

import com.sun.jna.ptr.IntByReference;
import hudson.plugins.vmware.VMwareRuntimeException;

import java.util.logging.Logger;

/**
 * Base class for Vix objects.
 *
 * @author Stephen Connolly
 * @since 29-Jun-2008 22:43:45
 */
public class VixObject {
    private static final Logger LOGGER = Logger.getLogger(VixObject.class.getName());
    private final Vix library;

    protected VixObject(Vix library) {
        this.library = library;
    }

    public Vix getLibrary() {
        return library;
    }

    protected void waitForJob(int jobHandle) {
        checkError(library.VixJob_Wait(jobHandle, Vix.Property.NONE));
    }

    protected int waitForJobAndGetJobResultHandle(int jobHandle) {
        IntByReference result = new IntByReference();
        checkError(library.VixJob_Wait(jobHandle, Vix.Property.JOB_RESULT_HANDLE, result, Vix.Property.NONE));
        return result.getValue();
    }

    protected void checkError(int err) {
        if (err != Vix.Error.OK) {
            final String errorMessage = library.Vix_GetErrorText(err, null);
            LOGGER.warning(errorMessage);
            throw new VMwareRuntimeException(errorMessage);
        }
    }

}
