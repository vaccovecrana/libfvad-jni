package io.vacco.fvad;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FvReSampler {

  private static short getSafeSample(short[] pcmData, int index) {
    if (index < 0 || index >= pcmData.length) {
      return 0;
    }
    return pcmData[index];
  }

  private static short cubicInterpolate(short y0, short y1, short y2, short y3, double t) {
    double a0 = y3 - y2 - y0 + y1;
    double a1 = y0 - y1 - a0;
    double a2 = y2 - y0;
    double a3 = y1;
    return (short) (a0 * t * t * t + a1 * t * t + a2 * t + a3);
  }

  private static short[] cubicResamplePcmData(short[] pcmData, int sourceSampleRate, int targetSampleRate) {
    var resampleRatio = (double) targetSampleRate / sourceSampleRate;
    var newNumSamples = (int) (pcmData.length * resampleRatio);
    var resampledData = new short[newNumSamples];

    for (int i = 0; i < newNumSamples; i++) {
      var srcIndex = i / resampleRatio;
      int indexFloor = (int) Math.floor(srcIndex);
      var t = srcIndex - indexFloor;
      // Get four neighboring points for cubic interpolation
      short y0 = getSafeSample(pcmData, indexFloor - 1);
      short y1 = getSafeSample(pcmData, indexFloor);
      short y2 = getSafeSample(pcmData, indexFloor + 1);
      short y3 = getSafeSample(pcmData, indexFloor + 2);
      resampledData[i] = cubicInterpolate(y0, y1, y2, y3, t);
    }

    return resampledData;
  }

  public static short[][] splitIntoFrames(short[] pcmData, int frameSize) {
    int numFrames = pcmData.length / frameSize;
    short[][] frames = new short[numFrames][frameSize];
    for (int i = 0; i < numFrames; i++) {
      System.arraycopy(pcmData, i * frameSize, frames[i], 0, frameSize);
    }
    return frames;
  }

  public static short[] readSamples(AudioInputStream audioStream, int targetSampleRate) throws IOException {
    var format = audioStream.getFormat();
    int frameSize = format.getFrameSize();
    int bytesPerSample = format.getSampleSizeInBits() / 8;

    if (frameSize != 2 || bytesPerSample != 2) {
      throw new IllegalArgumentException(String.format(
        "This function only supports 16-bit PCM audio, provided: [%d, %d]",
        frameSize, bytesPerSample
      ));
    }

    var isBigEndian = format.isBigEndian();
    var bos = new ByteArrayOutputStream();
    audioStream.transferTo(bos);
    bos.flush();
    bos.close();
    audioStream.close();

    var buffer = bos.toByteArray();
    int numSamples = buffer.length / 2; // 2 bytes per sample
    var audioSamples = new short[numSamples];
    if (isBigEndian) {
      for (int i = 0; i < numSamples; i++) {
        audioSamples[i] = (short) (((buffer[i * 2] & 0xFF) << 8) | (buffer[i * 2 + 1] & 0xFF));
      }
    } else {
      for (int i = 0; i < numSamples; i++) {
        audioSamples[i] = (short) (((buffer[i * 2 + 1] & 0xFF) << 8) | (buffer[i * 2] & 0xFF));
      }
    }

    var sourceSr = (int) audioStream.getFormat().getSampleRate();
    if (sourceSr == targetSampleRate) {
      return audioSamples;
    }
    return cubicResamplePcmData(audioSamples, sourceSr, targetSampleRate);
  }

}
