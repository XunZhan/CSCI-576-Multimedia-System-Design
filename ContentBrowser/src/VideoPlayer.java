import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;

enum PlayerState {
  PAUSE, PLAYING
}


public class VideoPlayer {

  private List<BufferedImage> frameList;
  private Clip audioClip;

  private long numFrame;
  private long clipLength;

  private long currentFrame = 0;
  private long currentClipLoc = 0;

  public PlayerState state = PlayerState.PAUSE;

  private BrowserController controller;

  // constructor
  public VideoPlayer() {

  }

  public void setDataSource(List<BufferedImage> list, Clip clip) {
    frameList = list;
    audioClip = clip;

    numFrame = list.size();
    clipLength = clip.getMicrosecondLength();
  }

  public void setController(BrowserController controller) {
    this.controller = controller;
  }

  public void play() {
    System.out.println("[VideoPlayer] Playing...");

    // create a new thread to play video
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        state = PlayerState.PLAYING;

        // audio
        playAudio();

        // frames
        long nanoError = 0;

        while (currentFrame < numFrame) {
          // for error calculation and correction
          long t_start = System.nanoTime();

          // test if paused
          if (state == PlayerState.PAUSE) {
            // save some stuff
            //
            return;  // stop the thread
          }

          // update UI
          controller.updateFrameInView((int)currentFrame);

          long t_end = System.nanoTime();

          nanoError += t_end - t_start;
          long milli = Constants.MILLISECOND_INTERVAL;
          if (nanoError > 10_000_000) {
            nanoError = 0;
            milli = Constants.MILLISECOND_INTERVAL - 10;
          }
          try {
            Thread.sleep(milli, Constants.NANOSECOND_INTERVAL);
          } catch (InterruptedException e) {

          }

          ++currentFrame;
        }

        // end of the video
        state = PlayerState.PAUSE;
        stopAudio();
        currentFrame = 0;
        controller.playerStopNotification();
      }
    }, "VideoThread");

    thread.start();
  }

  public void pause() {
    System.out.println("[VideoPlayer] Pause.");

    state = PlayerState.PAUSE;
    pauseAudio();
  }

  public void stop() {
    System.out.println("[VideoPlayer] Stop.");

    state = PlayerState.PAUSE;
    stopAudio();
    currentFrame = 0;
    controller.updateFrameInView(0);
  }

  // audio methods
  // -------------
  private void playAudio() {
    if (audioClip != null) {
      audioClip.setMicrosecondPosition(currentClipLoc);
      audioClip.start();
    }
  }

  private void pauseAudio() {
    if (audioClip != null) {
      currentClipLoc = audioClip.getMicrosecondPosition();
      audioClip.stop();
    }
  }

  private void stopAudio() {
    if (audioClip != null) {
      currentClipLoc = 0;
      audioClip.stop();
    }
  }


  // getter
  // ------
  public long getNumFrame() {
    return numFrame;
  }

  public long getClipLength() {
    return getClipLength();
  }

  public long getCurrentFrame() {
    return currentFrame;
  }
}
