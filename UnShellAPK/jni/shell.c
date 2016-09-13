#include "com_demo_jnitool_JNITool.h"

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
		pt = ptrace(PTRACE_TRACEME, 0, 0, 0); //�ӽ��̷�����
		while(1)
		{
			fd = fopen(filename,"r");
			while(fgets(line,MAX,fd))//���ļ���fd�ж�ȡһ�е�line��������ȡMAX-1���ַ�
			{
				if(strncmp(line,"TracerPid",9) == 0)
				{
					int TracerPid = atoi(&line[10]); //�ַ�ת����
					LOGI("########## TracerPid = %d,%s", TracerPid,line); //��ӡTracerPid:0/��0
					fclose(fd);
					if(TracerPid != 0)
					{
						LOGI("########## here");
						int ret = kill( pid,SIGKILL);// �ɹ�ִ��ʱ������0��ʧ�ܷ���-1
						LOGI("########## kill = %d", ret);
						return;
					}
					break;
				}
			}
			sleep(CHECK_TIME); //���ʱ����Ϊ10s
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
		while(fgets(line,MAX,fd))//���ļ���fd�ж�ȡһ�е�line��������ȡMAX-1���ַ�
		{
			if(strncmp(line,"TracerPid",9) == 0)
			{
				int TracerPid = atoi(&line[10]); //�ַ�ת����
				//LOGI("########## TracerPid = %d,%s", TracerPid,line); //��ӡTracerPid:0/��0
				fclose(fd);
				if(TracerPid != 0)
				{
					//LOGI("########## here");
					//int ret = kill( pid,SIGKILL);// �ɹ�ִ��ʱ������0��ʧ�ܷ���-1
					LOGI("checkstatus() result=1 !");
					return 1;
				}
				LOGI("checkstatus() result=0 !");
				return 0;
			}
		}
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	//anti_debug();
	JNIEnv* env;
	if(JNI_OK != (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6)){ //����ָ���汾��JNI
			return -1;
	}
	LOGI("JNI_OnLoad()");
	return JNI_VERSION_1_6;
}

JNIEXPORT jstring JNICALL Java_com_demo_jnitool_JNITool_checkey
  (JNIEnv * env, jclass jcls, jstring key){
	int isdebugged;
	const char * chs = "FalseKey!NativeMethod";

	isdebugged = checkstatus();
	if(isdebugged==0){
		return key;
	}else{
		return (*env)->NewStringUTF(env, chs);
	}



}


JNIEXPORT jbyteArray JNICALL Java_com_demo_jnitool_JNITool_decrypt
  (JNIEnv * env, jclass jcls, jstring key, jbyteArray encrypted){

	jclass clazz=(*env)->FindClass(env,"com/demo/jnitool/RC4");
	//     jmethodID   (*GetStaticMethodID)(JNIEnv*, jclass, const char*, const char*);
	jmethodID  methodid=(*env)->GetStaticMethodID(env,clazz,"RC4Base","([BLjava/lang/String;)[B");
	//void        (*CallStaticVoidMethod)(JNIEnv*, jclass, jmethodID, ...);
	jbyteArray result = (*env)->CallStaticObjectMethod(env, clazz, methodid, encrypted, key);

	return result;

}

JNIEXPORT jbyteArray JNICALL Java_com_demo_jnitool_JNITool_unShell
  (JNIEnv * env, jclass clazz, jbyteArray bytes){

	jsize len = (*env)->GetArrayLength(env, bytes);
	jbyte* pbyte = (jbyte*)malloc(len * sizeof(jbyte));
	(*env)->GetByteArrayRegion(env, bytes,0,len,pbyte);

	int i=0;
	for(;i<len;i++){
		pbyte[i] = (jbyte)(0xFF ^ pbyte[i]);//�൱�����ֽ�ȡ��
	}
	(*env)->SetByteArrayRegion(env,bytes, 0, len, pbyte);

	free(pbyte);
	return bytes;

}
