package io.vacco.fvad;

import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import javax.sound.sampled.*;
import java.util.Objects;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class FvContextTest {

  public static final String[] wavClips = new String[] {
    "/d81cfb74.wav", "/5574b93d.wav", "/jfk.wav"
  };

  static {
    it("Detects voice frames in audio streams", () -> {
      try (var vc = new FvContext()) {
        for (var audio : wavClips) {
          var url = Objects.requireNonNull(FvContextTest.class.getResource(audio));
          var audioInputStream = AudioSystem.getAudioInputStream(url.openStream());
          var result = vc.process(audioInputStream);
          System.out.println(">> " + url);
          System.out.println(result);
        }
      }
    });
  }
}
