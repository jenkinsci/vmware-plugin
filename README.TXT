Warning: may bring down the JVM, use at your own risk

Changes:
* Switched from JNI based native DLL to JNA (https://jna.dev.java.net) should work on Linux

Known issues:
* You need to use VMware VIX 1.0 (i.e. the version for use with VMware Server).  On Windows, if
  you have VIX 1.1 set the library path to
    C:\Program Files\VMware\VMware VIX\server-1\32bit
  I have not tested on Linux

* I have observed that either VIX or JNA can crash the JVM.  This seems to occur when the VMware VM is started and
  stopped before it can complete it's BIOS boot sequence.  I have put a hard delay of 10 seconds between power-on
  and power-off to try and prevent this issue (frankly I think this is an issue with VIX)

  The more people that can test this on their systems, and get some confidence that I have removed the crash, the better

ToDo:
* Move VMware Host config to the global page
* Allow control of multiple VMware machines from a single build