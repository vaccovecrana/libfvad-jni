package io.vacco.fvad;

import javax.sound.sampled.AudioInputStream;

public class FvContext implements AutoCloseable {

  public static int DefaultSampleRate = 16000;
  public static int DefaultVadMode = 3;
  public static int DefaultVadFrameMs = 10;

  static {
    FvNative.loadLibraryFromJar("/io/vacco/fvad/libfvad.so");
  }

  private final long ptr;

  public native long  fvadNew();
  public native void  fvadFree(long vadPtr);
  public native void  fvadReset(long vadPtr);
  public native int   fvadSetMode(long vadPtr, int mode);
  public native int   fvadSetSampleRate(long vadPtr, int sampleRate);
  public native int   fvadProcess(long vadPtr, short[] frame, long length);

  @SuppressWarnings("this-escape")
  public FvContext() {
    this.ptr = fvadNew();
  }

  private int vadBufferSizeOf(int sampleRate, int frameDurationMs) {
    if (frameDurationMs != DefaultVadFrameMs && frameDurationMs != 20 && frameDurationMs != 30) {
      throw new IllegalArgumentException("Invalid frame duration: must be 10, 20, or 30 ms.");
    }
    return (sampleRate / 1000) * frameDurationMs;
  }

  public FvResult process(AudioInputStream audioInputStream) {
    try {
      fvadReset(ptr);
      var format = audioInputStream.getFormat();
      if (format.getChannels() != 1) {
        throw new IllegalArgumentException("Invalid audio channel count: " + format.getChannels());
      }
      fvadSetSampleRate(ptr, DefaultSampleRate);
      fvadSetMode(ptr, DefaultVadMode);

      var vadBufferSize = vadBufferSizeOf(DefaultSampleRate, DefaultVadFrameMs);
      var samples = FvReSampler.readSamples(audioInputStream, DefaultSampleRate);
      var result = new FvResult();
      var frames = FvReSampler.splitIntoFrames(samples, vadBufferSize);
      int samplesRead = 0;
      for (var frame : frames) {
        var vadResult = fvadProcess(ptr, frame, vadBufferSize);
        if (vadResult == 1) {
          result.voiceCount++;
        }
        samplesRead++;
      }

      result.frameCount = samplesRead;
      return result.updateSpeechProb();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to process VAD detection", e);
    }
  }

  @Override public void close() {
    this.fvadFree(this.ptr);
  }

}