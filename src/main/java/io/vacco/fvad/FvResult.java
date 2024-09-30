package io.vacco.fvad;

public class FvResult {

  public int    frameCount = 0;
  public int    voiceCount = 0;
  public double speechProb = 0;

  public FvResult updateSpeechProb() {
    speechProb = ((double) voiceCount) / frameCount;
    return this;
  }

  @Override public String toString() {
    return String.format(
      "fc: %d, vc: %d, sProb: %.2f",
      frameCount, voiceCount, speechProb
    );
  }

}
