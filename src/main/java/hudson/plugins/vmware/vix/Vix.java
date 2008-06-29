package hudson.plugins.vmware.vix;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public interface Vix extends Library {

    public static class Handle {
        public static final int INVALID = 0;
    }

    /**
     * These are the types of handles.
     */
    public static class HandleType {
        public static final int NONE = 0;
        public static final int HOST = 2;
        public static final int VM = 3;
        public static final int NETWORK = 5;
        public static final int JOB = 6;
        public static final int SNAPSHOT = 7;
        public static final int METADATA_CONTAINER = 11;
    }

    /**
     * The error codes are returned by all public VIX routines.
     */
    public static class Error {
        public static final int OK = 0;

        /* General errors */
        public static final int E_FAIL = 1;
        public static final int E_OUT_OF_MEMORY = 2;
        public static final int E_INVALID_ARG = 3;
        public static final int E_FILE_NOT_FOUND = 4;
        public static final int E_OBJECT_IS_BUSY = 5;
        public static final int E_NOT_SUPPORTED = 6;
        public static final int E_FILE_ERROR = 7;
        public static final int E_DISK_FULL = 8;
        public static final int E_INCORRECT_FILE_TYPE = 9;
        public static final int E_CANCELLED = 10;
        public static final int E_FILE_READ_ONLY = 11;
        public static final int E_FILE_ALREADY_EXISTS = 12;
        public static final int E_FILE_ACCESS_ERROR = 13;
        public static final int E_REQUIRES_LARGE_FILES = 14;
        public static final int E_FILE_ALREADY_LOCKED = 15;
        public static final int E_NOT_SUPPORTED_ON_REMOTE_OBJECT = 20;
        public static final int E_FILE_TOO_BIG = 21;
        public static final int E_FILE_NAME_INVALID = 22;
        public static final int E_ALREADY_EXISTS = 23;

        /* Handle Errors */
        public static final int E_INVALID_HANDLE = 1000;
        public static final int E_NOT_SUPPORTED_ON_HANDLE_TYPE = 1001;
        public static final int E_TOO_MANY_HANDLES = 1002;

        /* XML errors */
        public static final int E_NOT_FOUND = 2000;
        public static final int E_TYPE_MISMATCH = 2001;
        public static final int E_INVALID_XML = 2002;

        /* VM Control Errors */
        public static final int E_TIMEOUT_WAITING_FOR_TOOLS = 3000;
        public static final int E_UNRECOGNIZED_COMMAND = 3001;
        public static final int E_OP_NOT_SUPPORTED_ON_GUEST = 3003;
        public static final int E_PROGRAM_NOT_STARTED = 3004;
        public static final int E_CANNOT_START_READ_ONLY_VM = 3005;
        public static final int E_VM_NOT_RUNNING = 3006;
        public static final int E_VM_IS_RUNNING = 3007;
        public static final int E_CANNOT_CONNECT_TO_VM = 3008;
        public static final int E_POWEROP_SCRIPTS_NOT_AVAILABLE = 3009;
        public static final int E_NO_GUEST_OS_INSTALLED = 3010;
        public static final int E_VM_INSUFFICIENT_HOST_MEMORY = 3011;
        public static final int E_SUSPEND_ERROR = 3012;
        public static final int E_VM_NOT_ENOUGH_CPUS = 3013;
        public static final int E_HOST_USER_PERMISSIONS = 3014;
        public static final int E_GUEST_USER_PERMISSIONS = 3015;
        public static final int E_TOOLS_NOT_RUNNING = 3016;
        public static final int E_GUEST_OPERATIONS_PROHIBITED = 3017;
        public static final int E_ANON_GUEST_OPERATIONS_PROHIBITED = 3018;
        public static final int E_ROOT_GUEST_OPERATIONS_PROHIBITED = 3019;
        public static final int E_MISSING_ANON_GUEST_ACCOUNT = 3023;
        public static final int E_CANNOT_AUTHENTICATE_WITH_GUEST = 3024;
        public static final int E_UNRECOGNIZED_COMMAND_IN_GUEST = 3025;
        public static final int E_CONSOLE_GUEST_OPERATIONS_PROHIBITED = 3026;
        public static final int E_MUST_BE_CONSOLE_USER = 3027;

        /* VM Errors */
        public static final int E_VM_NOT_FOUND = 4000;
        public static final int E_NOT_SUPPORTED_FOR_VM_VERSION = 4001;
        public static final int E_CANNOT_READ_VM_CONFIG = 4002;
        public static final int E_TEMPLATE_VM = 4003;
        public static final int E_VM_ALREADY_LOADED = 4004;
        public static final int E_VM_ALREADY_UP_TO_DATE = 4006;

        /* Property Errors */
        public static final int E_UNRECOGNIZED_PROPERTY = 6000;
        public static final int E_INVALID_PROPERTY_VALUE = 6001;
        public static final int E_READ_ONLY_PROPERTY = 6002;
        public static final int E_MISSING_REQUIRED_PROPERTY = 6003;

        /* Completion Errors */
        public static final int E_BAD_VM_INDEX = 8000;

        /* Snapshot errors */
        public static final int E_SNAPSHOT_INVAL = 13000;
        public static final int E_SNAPSHOT_DUMPER = 13001;
        public static final int E_SNAPSHOT_DISKLIB = 13002;
        public static final int E_SNAPSHOT_NOTFOUND = 13003;
        public static final int E_SNAPSHOT_EXISTS = 13004;
        public static final int E_SNAPSHOT_VERSION = 13005;
        public static final int E_SNAPSHOT_NOPERM = 13006;
        public static final int E_SNAPSHOT_CONFIG = 13007;
        public static final int E_SNAPSHOT_NOCHANGE = 13008;
        public static final int E_SNAPSHOT_CHECKPOINT = 13009;
        public static final int E_SNAPSHOT_LOCKED = 13010;
        public static final int E_SNAPSHOT_INCONSISTENT = 13011;
        public static final int E_SNAPSHOT_NAMETOOLONG = 13012;
        public static final int E_SNAPSHOT_VIXFILE = 13013;
        public static final int E_SNAPSHOT_DISKLOCKED = 13014;
        public static final int E_SNAPSHOT_DUPLICATEDDISK = 13015;
        public static final int E_SNAPSHOT_INDEPENDENTDISK = 13016;
        public static final int E_SNAPSHOT_NONUNIQUE_NAME = 13017;

        /* Guest Errors */
        public static final int E_NOT_A_FILE = 20001;
        public static final int E_NOT_A_DIRECTORY = 20002;
        public static final int E_NO_SUCH_PROCESS = 20003;
        public static final int E_FILE_NAME_TOO_LONG = 20004;
    }

    /**
     * Returns a human-readable string that describes the error.
     *
     * @param err    A Vix error code returned by any other Vix function.
     * @param locale Must be <code>null</code>.
     * @return A human-readable string that describes the error.
     */
    String Vix_GetErrorText(int err, String locale);

    /*
    *-----------------------------------------------------------------------------
    *
    * VIX Handles --
    *
    * These are common functions that apply to handles of several types.
    *-----------------------------------------------------------------------------
    */

    /**
     * VIX Property Type
     */
    public static final class PropertyType {
        public static final int ANY = 0;
        public static final int INTEGER = 1;
        public static final int STRING = 2;
        public static final int BOOL = 3;
        public static final int HANDLE = 4;
        public static final int INT64 = 5;
        public static final int BLOB = 6;
    }

    /**
     * VIX Property ID's
     */
    public static final class Property {
        public static final int NONE = 0;

        /* Properties used by several handle types. */
        public static final int META_DATA_CONTAINER = 2;

        /* VIX_HANDLETYPE_HOST properties */
        public static final int HOST_HOSTTYPE = 50;
        public static final int HOST_API_VERSION = 51;

        /* VIX_HANDLETYPE_VM properties */
        public static final int VM_NUM_VCPUS = 101;
        public static final int VM_VMX_PATHNAME = 103;
        public static final int VM_VMTEAM_PATHNAME = 105;
        public static final int VM_MEMORY_SIZE = 106;
        public static final int VM_READ_ONLY = 107;
        public static final int VM_IN_VMTEAM = 128;
        public static final int VM_POWER_STATE = 129;
        public static final int VM_TOOLS_STATE = 152;
        public static final int VM_IS_RUNNING = 196;
        public static final int VM_SUPPORTED_FEATURES = 197;

        /* Result properties; these are returned by various procedures */
        public static final int JOB_RESULT_ERROR_CODE = 3000;
        public static final int JOB_RESULT_VM_IN_GROUP = 3001;
        public static final int JOB_RESULT_USER_MESSAGE = 3002;
        public static final int JOB_RESULT_EXIT_CODE = 3004;
        public static final int JOB_RESULT_COMMAND_OUTPUT = 3005;
        public static final int JOB_RESULT_HANDLE = 3010;
        public static final int JOB_RESULT_GUEST_OBJECT_EXISTS = 3011;
        public static final int JOB_RESULT_GUEST_PROGRAM_ELAPSED_TIME = 3017;
        public static final int JOB_RESULT_GUEST_PROGRAM_EXIT_CODE = 3018;
        public static final int JOB_RESULT_ITEM_NAME = 3035;
        public static final int JOB_RESULT_FOUND_ITEM_DESCRIPTION = 3036;
        public static final int JOB_RESULT_SHARED_FOLDER_COUNT = 3046;
        public static final int JOB_RESULT_SHARED_FOLDER_HOST = 3048;
        public static final int JOB_RESULT_SHARED_FOLDER_FLAGS = 3049;
        public static final int JOB_RESULT_PROCESS_ID = 3051;
        public static final int JOB_RESULT_PROCESS_OWNER = 3052;
        public static final int JOB_RESULT_PROCESS_COMMAND = 3053;
        public static final int JOB_RESULT_FILE_FLAGS = 3054;
        public static final int JOB_RESULT_PROCESS_START_TIME = 3055;
        public static final int JOB_RESULT_VM_VARIABLE_STRING = 3056;
        public static final int JOB_RESULT_PROCESS_BEING_DEBUGGED = 3057;

        /* Event properties; these are sent in the moreEventInfo for some events. */
        public static final int FOUND_ITEM_LOCATION = 4010;

        /* VIX_HANDLETYPE_SNAPSHOT properties */
        public static final int SNAPSHOT_DISPLAYNAME = 4200;
        public static final int SNAPSHOT_DESCRIPTION = 4201;
        public static final int SNAPSHOT_POWERSTATE = 4205;

    }

    /**
     * These are events that may be signalled by calling a procedure
     * of type VixEventProc.
     */
    public static final class EventType {
        public static final int JOB_COMPLETED = 2;
        public static final int JOB_PROGRESS = 3;
        public static final int FIND_ITEM = 8;
    }

    /**
     * These are the property flags for each file.
     */

    public static final class FileAttributes {
        public static final int VIX_FILE_ATTRIBUTES_DIRECTORY = 0x0001;
        public static final int VIX_FILE_ATTRIBUTES_SYMLINK = 0x0002;
    }

    /**
     * Procedures of this type are called when an event happens on a handle.
     */
    public static interface VixEventProc extends Callback {
        void callback(int handle, int eventType, int moreEventInfo, Pointer clientData);
    }

    /*
     * Handle Property functions
     */

    /**
     * This function decrements the reference count for a handle and destroys the handle when there are no references.
     *
     * @param handle Any handle returned by a Vix function.
     */
    void Vix_ReleaseHandle(int handle);

    /**
     * Given a handle, this returns the handle type.
     *
     * @param handle Any handle returned by a Vix function.
     * @return one of the constants from {@link HandleType}
     */
    int Vix_GetHandleType(int handle);

    /**
     * This function allows you to get one or more properties from a handle.
     * <ul>
     * <li>This function allows you to get one or more properties from a handle. You may use this function on any
     * type of handle, but only specific properties are defined for each handle.</li>
     * <li>This procedure accepts a variable number of parameters, so you can retrieve any number of properties with
     * a single call. The parameters must be in a series of pairs of property IDs and result pointers. Each result
     * pointer will accept the value of the property identified by the property ID in the previous parameter. The
     * type of the pointer depends on the type of the property. You end the variable list of parameters with a single
     * ID value of {@link Vix.Property.NONE}.</li>
     * <li>When Vix_GetProperties() returns an error, the values of the output parameters are indeterminate.</li>
     * <li>If you retrieve a string property, the Programming API allocates space for that string. You are responsible
     * for calling Vix_FreeBuffer() to free the string.</li>
     * <li>The value of {@link Vix.Property.VM_TOOLS_STATE} is valid only after calling
     * {@link VixVM_WaitForToolsInGuest}.</li>
     * </ul>
     *
     * @param handle          Any handle returned by a Vix function.
     * @param firstPropertyID A property ID.
     * @return This function returns VIX_OK if it succeeded, otherwise the return value indicates an error.
     */
    int Vix_GetProperties(int handle, Object... firstPropertyID);

    /**
     * Given a property ID, this function returns the type of that property.
     *
     * @param handle       Any handle returned by a VIX function.
     * @param propertyID   A property ID. See below for valid values.
     * @param propertyType The type of the data stored by the property.
     * @return This function returns VIX_OK if it succeeded.
     */
    int Vix_GetPropertyType(int handle, int propertyID, IntByReference propertyType);

    /**
     * When Vix_GetProperties() or Vix_JobWait() returns a string property, it allocates a buffer for the string.
     * Client applications are responsible for calling Vix_FreeBuffer() to free the string buffer when no longer
     * needed. If you pass a null pointer to Vix_FreeBuffer(), the function returns immediately.
     *
     * @param buffer A string pointer returned by a call to Vix_GetProperties() or Vix_JobWait().
     */
    void Vix_FreeBuffer(Pointer buffer);

    /*
     *-----------------------------------------------------------------------------
     *
     * VIX Host --
     *
     *-----------------------------------------------------------------------------
     */
    public static final class HostOption {
        public static final int NONE = 0;
        public static final int USE_EVENT_PUMP = 0x0008;
    }

    public static final class ServiceProvider {
        public static final int DEFAULT = 1;
        public static final int VMWARE_SERVER = 2;
        public static final int VMWARE_WORKSTATION = 3;
    }

    int VixHost_Connect(int apiVersion,
                        int hostType,
                        String hostName,
                        int hostPort,
                        String userName,
                        String password,
                        int options,
                        int propertyListHandle,
                        VixEventProc callbackProc,
                        Pointer clientData);

    void VixHost_Disconnect(int hostHandle);

    /*
    * VM Registration
    */

    int VixHost_RegisterVM(int hostHandle,
                           String vmxFilePath,
                           VixEventProc callbackProc,
                           Pointer clientData);

    int VixHost_UnregisterVM(int hostHandle,
                             String vmxFilePath,
                             VixEventProc callbackProc,
                             Pointer clientData);

    /**
     * VM Search
     */
    public static final class Find {
        public static final int RUNNING_VMS = 1;
        public static final int REGISTERED_VMS = 4;
    }

    int VixHost_FindItems(int hostHandle,
                          int searchType,
                          int searchCriteria,
                          int timeout,
                          VixEventProc callbackProc,
                          Pointer clientData);

    /**
     * Event pump
     */
    public static final class PumpEventsOption {
        public static final int NONE = 0;
    }

    void Vix_PumpEvents(int hostHandle, int options);

/*
 *-----------------------------------------------------------------------------
 *
 * VIX VM --
 *
 * This describes the persistent configuration state of a single VM. The
 * VM may or may not be running.
 *
 *-----------------------------------------------------------------------------
 */

    int VixVM_Open(int hostHandle,
                   String vmxFilePathName,
                   VixEventProc callbackProc,
                   Pointer clientData);

    public static final class VMPPowerOp {
        public static final int NORMAL = 0;
        public static final int SUPPRESS_SNAPSHOT_POWERON = 0x0080;
        public static final int LAUNCH_GUI = 0x0200;
    }

    /*
     * Power operations
     */

    int VixVM_PowerOn(int vmHandle,
                      int powerOnOptions,
                      int propertyListHandle,
                      VixEventProc callbackProc,
                      Pointer clientData);

    int VixVM_PowerOff(int vmHandle,
                       int powerOffOptions,
                       VixEventProc callbackProc,
                       Pointer clientData);

    int VixVM_Reset(int vmHandle,
                    int powerOnOptions,
                    VixEventProc callbackProc,
                    Pointer clientData);

    int VixVM_Suspend(int vmHandle,
                      int powerOffOptions,
                      VixEventProc callbackProc,
                      Pointer clientData);

    public static final class VMDeleteOptions {
        public static final int NONE = 0;
        public static final int DELETE_DISK_FILES = 0x0002;
    }

    int VixVM_Delete(int vmHandle,
                     int deleteOptions,
                     VixEventProc callbackProc,
                     Pointer clientData);

    /**
     * This is the state of an individual VM.
     */
    public static final class PowerState {
        public static final int POWERING_OFF = 0x0001;
        public static final int POWERED_OFF = 0x0002;
        public static final int POWERING_ON = 0x0004;
        public static final int POWERED_ON = 0x0008;
        public static final int SUSPENDING = 0x0010;
        public static final int SUSPENDED = 0x0020;
        public static final int TOOLS_RUNNING = 0x0040;
        public static final int RESETTING = 0x0080;
        public static final int BLOCKED_ON_MSG = 0x0100;
    }

    public static final class ToolsState {
        public static final int UNKNOWN = 0x0001;
        public static final int RUNNING = 0x0002;
        public static final int NOT_INSTALLED = 0x0004;
    }

    /**
     * These flags describe optional functions supported by different
     * types of VM.
     */
    public static final class VMSupport {
        public static final int SHARED_FOLDERS = 0x0001;
        public static final int MULTIPLE_SNAPSHOTS = 0x0002;
        public static final int TOOLS_INSTALL = 0x0004;
        public static final int HARDWARE_UPGRADE = 0x0008;
    }

    /**
     * These are special names for an anonymous user and the system administrator.
     * The password is ignored if you specify these.
     */
    public static final class UserName {
        public static final String ANONYMOUS = "__VMware_Vix_Guest_User_Anonymous__";
        public static final String ADMINISTRATOR = "__VMware_Vix_Guest_User_Admin__";
        public static final String CONSOLE = "__VMware_Vix_Guest_Console_User__";
    }

    /*
    * Guest operations
    */

    int VixVM_WaitForToolsInGuest(int vmHandle,
                                  int timeoutInSeconds,
                                  VixEventProc callbackProc,
                                  Pointer clientData);

    int VixVM_LoginInGuest(int vmHandle,
                           String userName,
                           String password,
                           int options,
                           VixEventProc callbackProc,
                           Pointer clientData);

    int VixVM_LogoutFromGuest(int vmHandle,
                              VixEventProc callbackProc,
                              Pointer clientData);

    /**
     * Guest Process functions
     */
    public static final class RunProgram {
        public static final int RETURN_IMMEDIATELY = 0x0001;
        public static final int ACTIVATE_WINDOW = 0x0002;
    }

    int VixVM_RunProgramInGuest(int vmHandle,
                                String guestProgramName,
                                String commandLineArgs,
                                int options,
                                int propertyListHandle,
                                VixEventProc callbackProc,
                                Pointer clientData);

    int VixVM_ListProcessesInGuest(int vmHandle,
                                   int options,
                                   VixEventProc callbackProc,
                                   Pointer clientData);

    int VixVM_KillProcessInGuest(int vmHandle,
                                 long pid,
                                 int options,
                                 VixEventProc callbackProc,
                                 Pointer clientData);

    int VixVM_RunScriptInGuest(int vmHandle,
                               String interpreter,
                               String scriptText,
                               int options,
                               int propertyListHandle,
                               VixEventProc callbackProc,
                               Pointer clientData);

    int VixVM_OpenUrlInGuest(int vmHandle,
                             String url,
                             int windowState,
                             int propertyListHandle,
                             VixEventProc callbackProc,
                             Pointer clientData);

    /*
     * Snapshot functions that operate on a VM
     */

    int VixVM_GetNumRootSnapshots(int vmHandle, IntByReference result);

    int VixVM_GetRootSnapshot(int vmHandle, int index, IntByReference snapshotHandle);

    int VixVM_GetCurrentSnapshot(int vmHandle,
                                 IntByReference snapshotHandle);

    int VixVM_GetNamedSnapshot(int vmHandle,
                               String name,
                               IntByReference snapshotHandle);

    public static final class Snapshot {
        /**
         * Remove option
         */
        public static final int REMOVE_CHILDREN = 0x0001;
        /**
         * Create option
         */
        public static final int INCLUDE_MEMORY = 0x0002;
    }

    int VixVM_RemoveSnapshot(int vmHandle,
                             int snapshotHandle,
                             int options,
                             VixEventProc callbackProc,
                             Pointer clientData);

    int VixVM_RevertToSnapshot(int vmHandle,
                               int snapshotHandle,
                               int options,
                               int propertyListHandle,
                               VixEventProc callbackProc,
                               Pointer clientData);

    int VixVM_CreateSnapshot(int vmHandle,
                             String name,
                             String description,
                             int options,
                             int propertyListHandle,
                             VixEventProc callbackProc,
                             Pointer clientData);

    /*
    *-----------------------------------------------------------------------------
    *
    * VIX Job --
    *
    *-----------------------------------------------------------------------------
    */

    /*
    * Synchronization functions
    * (used to detect when an asynch operation completes).
    */

    int VixJob_Wait(int jobHandle, Object... firstPropertyID);

    int VixJob_CheckCompletion(int jobHandle, IntByReference complete);

    /*
    * Accessor functions
    * (used to get results of a completed asynch operation).
    */

    int VixJob_GetError(int jobHandle);

    int VixJob_GetNumProperties(int jobHandle,
                                int resultPropertyID);

    int VixJob_GetNthProperties(int jobHandle,
                                int index,
                                Object... propertyID);


}