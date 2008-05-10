package hudson.plugins.vmware;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since 28-Sep-2007 10:16:31
 */
public class VMwareRuntimeException extends RuntimeException {
    public VMwareRuntimeException() {
        super();
    }

    public VMwareRuntimeException(String message) {
        super(message);
    }

    public VMwareRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public VMwareRuntimeException(Throwable cause) {
        super(cause);    
    }
}
