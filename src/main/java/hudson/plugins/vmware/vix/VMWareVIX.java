package hudson.plugins.vmware.vix;

public class VMWareVIX {
    public native static int vixHostConnect(String hostName, String userName, String password, int portNumber);

    public native static void vixHostDisconnect(int handle);

    public native static void vixHostRegisterVM(int hostHandle, String configFile);

    public native static void vixHostUnregisterVM(int hostHandle, String configFile);

    public native static int vixVMOpen(int hostHandle, String configFile);

    public native static void vixVMPowerOn(int vmHandle);

    public native static void vixVMPowerOff(int vmHandle);

    public native static void vixVMSuspend(int vmHandle);

    public native static void vixVMReset(int vmHandle);

    public native static void vixVMWaitForToolsInGuest(int vmHandle);

    public native static int vixGetProperties(int vmHandle);

    public native static String vixGetErrorText(int err, String locale);

    public native static void vixVMLoginGuest(int vmHandle, String userName, String password);
}