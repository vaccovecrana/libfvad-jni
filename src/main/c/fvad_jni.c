#include <jni.h>
#include "fvad.h"

JNIEXPORT jlong JNICALL Java_io_vacco_fvad_FvContext_fvadNew(JNIEnv *env, jobject obj) {
    Fvad *vad = fvad_new();
    return (jlong) vad;
}

JNIEXPORT void JNICALL Java_io_vacco_fvad_FvContext_fvadFree(JNIEnv *env, jobject obj, jlong vadPtr) {
    Fvad *vad = (Fvad *) vadPtr;
    fvad_free(vad);
}

JNIEXPORT void JNICALL Java_io_vacco_fvad_FvContext_fvadReset(JNIEnv *env, jobject obj, jlong vadPtr) {
    Fvad *vad = (Fvad *) vadPtr;
    fvad_reset(vad);
}

JNIEXPORT jint JNICALL Java_io_vacco_fvad_FvContext_fvadSetMode(JNIEnv *env, jobject obj, jlong vadPtr, jint mode) {
    Fvad *vad = (Fvad *) vadPtr;
    return (jint) fvad_set_mode(vad, mode);
}

JNIEXPORT jint JNICALL Java_io_vacco_fvad_FvContext_fvadSetSampleRate(JNIEnv *env, jobject obj, jlong vadPtr, jint sampleRate) {
    Fvad *vad = (Fvad *) vadPtr;
    return (jint) fvad_set_sample_rate(vad, sampleRate);
}

JNIEXPORT jint JNICALL Java_io_vacco_fvad_FvContext_fvadProcess(JNIEnv *env, jobject obj, jlong vadPtr, jshortArray frame, jlong length) {
    Fvad *vad = (Fvad *) vadPtr;
    jshort *framePtr = (*env)->GetShortArrayElements(env, frame, NULL);
    jint result = fvad_process(vad, (const int16_t *) framePtr, (size_t) length);
    (*env)->ReleaseShortArrayElements(env, frame, framePtr, 0);
    return result;
}
