import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

enum PlayerState {
  STOP, PAUSE, PLAYING
}


public class VideoPlayer {

  private List<BufferedImage> frameList;
  private Clip audioClip;

  private long numFrame;
  private long clipLength;

  private long currentFrame = 0;
  private long currentClipLoc = 0;

  public PlayerState state = PlayerState.STOP;

  private BrowserController controller;

  // constructor
  public VideoPlayer() {

  }

  public void setDataSource(List<BufferedImage> list, Clip clip) {
    frameList = list;
    audioClip = clip;

    numFrame = list.size();
    clipLength = clip.getMicrosecondLength();

    // update frame label
    controller.setupProgressBarRange((int) numFrame);
    updateFrameRelatedViews();
  }

  public void setController(BrowserController controller) {
    this.controller = controller;
  }


  // control methods
  // ---------------
  public void play() {
    System.out.println("[VideoPlayer] Playing...");

    // reset the currentFrame to 0 (it happens when it automatically reaches the end)
    if (currentFrame >= numFrame) {
      currentFrame = 0;
    }

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

          if (state == PlayerState.STOP) {
            // test if stopped
            stopAudio();
            currentFrame = 0;
            updateFrameRelatedViews();
            return;
          } else if (state == PlayerState.PAUSE) {
            // test if paused
            pauseAudio();
            updateFrameRelatedViews();
            return;
          } else {
            // continue
            updateFrameRelatedViews();
          }

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
        state = PlayerState.STOP;
        stopAudio();
        controller.playerStopNotification();
      }
    }, "VideoThread");

    thread.start();
  }

  public void pause() {
    System.out.println("[VideoPlayer] Pause!");

    state = PlayerState.PAUSE;
  }

  public void stop() {
    System.out.println("[VideoPlayer] Stop!");
    state = PlayerState.STOP;
    // if the current state is paused or stopped, we need to do it by ourselves
    if (state == PlayerState.PAUSE || state == PlayerState.STOP) {
      currentFrame = 0;
      stopAudio();
      updateFrameRelatedViews();
    }
  }

  // frame update methods
  // --------------------
  public void setCurrentFrame(long currentFrame) {
    System.out.println("[VideoPlayer] CurrentFrame Updated (From 1): " + (currentFrame + 1));
    this.currentFrame = currentFrame;
    pauseAudio();
    updateFrameRelatedViews();

    if (state == PlayerState.STOP || state == PlayerState.PAUSE) {
      if (currentFrame == 0) {
        state = PlayerState.STOP;
      } else if (currentFrame == numFrame - 1) {
        state = PlayerState.STOP;
        stopAudio();
        this.currentFrame = numFrame;
      } else {
        state = PlayerState.PAUSE;
      }
    } else {
      playAudio();
    }
  }

  private void updateFrameRelatedViews() {
    controller.updateFrameInView((int) currentFrame);
    controller.updateFrameLabelValues((int) currentFrame, (int) numFrame);
    controller.updateProgressBarValue((int) currentFrame);
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
      // currentClipLoc = audioClip.getMicrosecondPosition();
      // correction
      long microSecondInterval = Constants.MILLISECOND_INTERVAL * 1000 + Constants.NANOSECOND_INTERVAL / 1000;
      currentClipLoc = microSecondInterval * currentFrame;

      audioClip.stop();
    }
  }

  private void stopAudio() {
    if (audioClip != null) {
      currentClipLoc = 0;
      audioClip.stop();
    }
  }

  public void setVolume(int level) {
    if (audioClip != null) {
      FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
      gainControl.setValue(20f * (float) Math.log10((float) level / (float) 100));
    }
  }


  // getter
  // ------
  public long getNumFrame() {
    return numFrame;
  }
}
