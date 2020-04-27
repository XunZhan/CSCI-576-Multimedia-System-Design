import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;

enum PlayerState {
  PAUSE, PLAYING
}

public class VideoModel {

  public List<BufferedImage> frameList;
  public Clip audioClip;

  public PlayerState state = PlayerState.PAUSE;
  public int currentFrame = 0;
  public long currentClipLoc = 0;

  // constructor
  public VideoModel(List<BufferedImage> list, Clip clip) {
    frameList = list;
    audioClip = clip;
  }




  // public void play() {
  //   play(currentFrame);
  // }
  //
  // private void play(int frameIndex) {
  //   System.out.println("play start");
  //
  //   long nanoError = 0;
  //
  //   for (int i = frameIndex; i < frameList.size(); ++i) {
  //     System.out.println(i);
  //     long start = System.nanoTime();
  //     this.displayView.showImg(frameList.get(i));
  //     long end = System.nanoTime();
  //     nanoError += end - start;
  //     long milli = MILLISECOND_INTERVAL;
  //     if (nanoError > 10_000_000) {
  //       nanoError = 0;
  //       milli = MILLISECOND_INTERVAL - 10;
  //     }
  //     try {
  //       Thread.sleep(milli, NANOSECOND_INTERVAL);
  //     } catch (InterruptedException e) {
  //     }
  //   }
  //
  //   System.out.println("play end");
  // }

}
