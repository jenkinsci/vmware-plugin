// VMwareVIXJNI.cpp : Defines the entry point for the DLL application.
//

#include "stdafx.h"
#include "hudson_plugins_vmware_vix_VMWareVIX.h"
#include <vix.h>

#ifdef _MANAGED
#pragma managed(push, off)
#endif

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
    return TRUE;
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixHostConnect
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixHostConnect
(JNIEnv *env, jclass cl, jstring hostName, jstring userName, jstring password, jint portNumber) {
	VixHandle hostHandle = VIX_INVALID_HANDLE;

	VixHandle jobHandle = VIX_INVALID_HANDLE;

	VixError err = VIX_OK;

	const char *cHostName = env->GetStringUTFChars(hostName, 0);

	const char *cUserName = env->GetStringUTFChars(userName, 0);

	const char *cPassword = env->GetStringUTFChars(password, 0);

	jobHandle = VixHost_Connect(
		1, 
		VIX_SERVICEPROVIDER_VMWARE_SERVER,
		cHostName, // *hostName,
		portNumber, // hostPort,
		cUserName, // *userName,
		cPassword, // *password,
		0, // options,
		VIX_INVALID_HANDLE, // propertyListHandle,
		NULL, // *callbackProc,
		NULL);

	err = VixJob_Wait(
		jobHandle,
		VIX_PROPERTY_JOB_RESULT_HANDLE,
		&hostHandle,
		VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);

	if(VIX_OK != err){
		hostHandle = 0;
	}
	return hostHandle;
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixHostDisconnect
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixHostDisconnect
(JNIEnv *env, jclass cl, jint hostHandle) {
	VixHost_Disconnect(hostHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixHostRegisterVM
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixHostRegisterVM
(JNIEnv *env, jclass cl, jint hostHandle, jstring configFile) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	const char *cConfigFile = env->GetStringUTFChars(configFile, 0);

	jobHandle = VixHost_RegisterVM(
		hostHandle,
		cConfigFile,
		NULL,
		NULL);

	err = VixJob_Wait(
		jobHandle,
		VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixHostUnregisterVM
 * Signature: (ILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixHostUnregisterVM
(JNIEnv *env, jclass cl, jint hostHandle, jstring configFile) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	const char *cConfigFile = env->GetStringUTFChars(configFile, 0);

	jobHandle = VixHost_UnregisterVM(
		hostHandle,
		cConfigFile,
		NULL,
		NULL);

	err = VixJob_Wait(
		jobHandle,
		VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMOpen
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMOpen
(JNIEnv* env, jclass cl, jint hostHandle, jstring configFile){
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixHandle vmHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	const char *cConfigFile = env->GetStringUTFChars(configFile, 0);
	
	jobHandle = VixVM_Open(
		hostHandle,
		cConfigFile,
		NULL,
		NULL);
	
	err = VixJob_Wait(
		jobHandle,
		VIX_PROPERTY_JOB_RESULT_HANDLE,
		&vmHandle,
		VIX_PROPERTY_NONE);
	
	Vix_ReleaseHandle(jobHandle);
	
	if(VIX_OK != err){
		vmHandle = 0;
	}
	return vmHandle;
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMPowerOn
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMPowerOn
(JNIEnv *env, jclass cl, jint vmHandle) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	jobHandle = VixVM_PowerOn(vmHandle,VIX_HOSTOPTION_USE_EVENT_PUMP,VIX_INVALID_HANDLE,NULL,NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMPowerOff
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMPowerOff
(JNIEnv *env, jclass cl, jint vmHandle) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	jobHandle = VixVM_PowerOff(
		vmHandle,
		VIX_VMPOWEROP_NORMAL,
		NULL, // *callbackProc,
		NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMSuspend
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMSuspend
(JNIEnv *env, jclass cl, jint vmHandle) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;

	jobHandle = VixVM_Suspend(
		vmHandle,
		VIX_VMPOWEROP_NORMAL,
		NULL, // *callbackProc,
		NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMReset
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMReset
(JNIEnv *env, jclass cl, jint vmHandle) {
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;

	jobHandle = VixVM_Reset(
		vmHandle,
		VIX_VMPOWEROP_NORMAL,
		NULL, // *callbackProc,
		NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMWaitForToolsInGuest
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMWaitForToolsInGuest
(JNIEnv *env, jclass cl, jint vmHandle){
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;

	jobHandle = VixVM_WaitForToolsInGuest(
		vmHandle,
		300,
		NULL,
		NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixGetProperties
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixGetProperties
(JNIEnv *env, jclass cl, jint vmHandle){
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	VixHandle vmState = VIX_INVALID_HANDLE;

	jobHandle = Vix_GetProperties(
		vmHandle,
		VIX_PROPERTY_JOB_RESULT_HANDLE,
		&vmState,
		VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);

	return vmState;
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixGetErrorText
 * Signature: (ILjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixGetErrorText
(JNIEnv *env, jclass cl, jint err, jstring locale){
	const char * cLocale = env->GetStringUTFChars(locale, 0);
	jstring errText = env->NewStringUTF(Vix_GetErrorText(err, cLocale));
	return errText;
}

/*
 * Class:     hudson_plugins_vmware_vix_VMWareVIX
 * Method:    vixVMLoginGuest
 * Signature: (ILjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_hudson_plugins_vmware_vix_VMWareVIX_vixVMLoginGuest
(JNIEnv *env, jclass cl, jint vmHandle, jstring userName, jstring password){
	VixHandle jobHandle = VIX_INVALID_HANDLE;
	VixError err = VIX_OK;
	const char *cUserName = env->GetStringUTFChars(userName, 0);
	const char *cPassword = env->GetStringUTFChars(password, 0);

	jobHandle = VixVM_LoginInGuest(vmHandle, cUserName, cPassword, 0, NULL, NULL);

	err = VixJob_Wait(jobHandle, VIX_PROPERTY_NONE);

	Vix_ReleaseHandle(jobHandle);
}

#ifdef _MANAGED
#pragma managed(pop)
#endif

