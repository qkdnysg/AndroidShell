//#include "com_demo_jnitool_JNITool.h"
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <sys/ptrace.h>
#include <android/log.h>

#define MAX 128
#define CHECK_TIME 10
#define LOG_TAG "com.demo.jnitool"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifndef NELEM //计算结构元素个数
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

void  anti_debug() __attribute__((constructor));
void anti_debug()
{
	LOGI("Enter into anti_debug()...");
	int pid;
	FILE *fd;
	char filename[MAX];
	char line[MAX];
	pid = getpid();
	sprintf(filename,"/proc/%d/status",pid);//filename=/proc/pid/status
	if(fork()==0)
	{
		int pt;
		pt = ptrace(PTRACE_TRACEME, 0, 0, 0); //子进程反调试
		while(1)
		{
			fd = fopen(filename,"r");
			while(fgets(line,MAX,fd))//从文件流fd中读取一行到line，但最多读取MAX-1个字符
			{
				if(strncmp(line,"TracerPid",9) == 0)
				{
					int TracerPid = atoi(&line[10]); //字符转整型
					LOGI("***** TracerPid = %d", TracerPid); //打印TracerPid:0/非0
					fclose(fd);
					if(TracerPid != 0)
					{
						LOGI("***** Debugger is found here! Killing %d ...", pid);
						int ret = kill( pid,SIGKILL);// 成功执行时，返回0。失败返回-1
						LOGI("***** kill() = %d", ret);
						return;
					}
					break;
				}
			}
			sleep(CHECK_TIME); //检查时间间隔为10s
		}
	}
}

int checkstatus(){
	    LOGI("Enter into checkstatus()...");
		int pid;
		FILE *fd;
		char filename[MAX];
		char line[MAX];
		pid = getpid();
		sprintf(filename,"/proc/%d/status",pid);//filename=/proc/pid/status

		fd = fopen(filename,"r");
		while(fgets(line,MAX,fd))//从文件流fd中读取一行到line，但最多读取MAX-1个字符
		{
			if(strncmp(line,"TracerPid",9) == 0)
			{
				int TracerPid = atoi(&line[10]); //字符转整型
				fclose(fd);
				if(TracerPid != 0)
				{
					LOGI("checkstatus() result=1 !");
					return 1;
				}
				LOGI("checkstatus() result=0 !");
				return 0;
			}
		}
}

JNIEXPORT jbyteArray nativeDecrypt(JNIEnv * env, jclass jcls, jstring key, jbyteArray encrypted){

	jclass clazz=(*env)->FindClass(env,"com/demo/mUtils/RC4");
	//     jmethodID   (*GetStaticMethodID)(JNIEnv*, jclass, const char*, const char*);
	jmethodID  methodid=(*env)->GetStaticMethodID(env,clazz,"RC4Base","([BLjava/lang/String;)[B");
	//void        (*CallStaticVoidMethod)(JNIEnv*, jclass, jmethodID, ...);
	jbyteArray result = (*env)->CallStaticObjectMethod(env, clazz, methodid, encrypted, key);

	return result;

}

JNIEXPORT jboolean nativeisEmulator(JNIEnv * env, jclass jcls, jobject mContext){

	jclass clazz=(*env)->FindClass(env,"com/demo/mUtils/EmulatorCheckTool");
	//     jmethodID   (*GetStaticMethodID)(JNIEnv*, jclass, const char*, const char*);
	jmethodID  methodid=(*env)->GetStaticMethodID(env,clazz,"isEmulator","(Landroid/content/Context;)Ljava/lang/Boolean;");
	jboolean result = (*env)->CallStaticObjectMethod(env, clazz, methodid, mContext);

	return result;

}

JNIEXPORT jbyteArray JNICALL nativeUnShell(JNIEnv * env, jclass clazz, jbyteArray bytes){

	jsize len = (*env)->GetArrayLength(env, bytes);
	jbyte* pbyte = (jbyte*)malloc(len * sizeof(jbyte));
	(*env)->GetByteArrayRegion(env, bytes,0,len,pbyte);

	int i=0;
	for(;i<len;i++){
		pbyte[i] = (jbyte)(0xFF ^ pbyte[i]);//相当于逐字节取反
	}
	(*env)->SetByteArrayRegion(env,bytes, 0, len, pbyte);

	free(pbyte);
	return bytes;
}

JNIEXPORT jstring nativeCheckey(JNIEnv * env, jclass jcls, jstring key){
	int isdebugged;
	const char * chs = "FalseKey!NativeMethod";

	isdebugged = checkstatus();
	if(isdebugged==0){
		return key;
	}else{
		return (*env)->NewStringUTF(env, chs);
	}
}

static JNINativeMethod jniMethods[] = {
	{"decrypt", "(Ljava/lang/String;[B)[B", (void*)nativeDecrypt},
	{"unShell", "([B)[B", (void*)nativeUnShell},
	{"checkey", "(Ljava/lang/String;)Ljava/lang/String;", (void*)nativeCheckey},
	{"isEmulator", "(Landroid/content/Context;)Ljava/lang/Boolean;", (void*)nativeisEmulator}
};

JNIEnv* env;
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	//anti_debug();

	if(JNI_OK != (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6)){ //加载指定版本的JNI
			return -1;
	}
	LOGI("JNI_OnLoad()");
	jclass jni_class = (*env)->FindClass(env, "com/demo/jnitool/JNITool");
	//注册未声明本地方法
	if (JNI_OK == (*env)->RegisterNatives(env, jni_class, jniMethods, NELEM(jniMethods))){
			LOGI("RegisterNatives() OK!");
		} else {
			LOGE("RegisterNatives() FAILED!");
			return -1;
		}
	return JNI_VERSION_1_6;
}

void JNI_OnUnLoad(JavaVM* vm, void* reserved){
	LOGI("JNI_OnUnLoad()");
	jclass jni_class = (*env)->FindClass(env, "com/demo/jnitool/JNITool");
	(*env)->UnregisterNatives(env, jni_class);
	LOGI("UnregisterNatives()");
}


