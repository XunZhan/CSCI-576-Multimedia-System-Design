import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

enum PlayerState {
  STOP, PAUSE, PLAYING
}


public class VideoPlayer {

  private List<List<BufferedImage>> frameList;
  private List<Clip> audioClipList;

  private List<Integer> numFrameList;
  private List<Long> clipLengthList;

  private int currentVideoID = 1;  // 1-based
  private int currentFrame = 0;
  private long currentClipLoc = 0;

  public PlayerState state = PlayerState.STOP;

  private BrowserController controller;

  // private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  // constructor
  public VideoPlayer() {
  }

  public void setDataSource(List<List<BufferedImage>> list, List<Clip> clip) {
    frameList = list;
    audioClipList = clip;

    numFrameList = new ArrayList<>();
    clipLengthList = new ArrayList<>();
    for (int i = 0; i < list.size(); ++i) {
      numFrameList.add(frameList.get(i).size());
      clipLengthList.add(audioClipList.get(i).getMicrosecondLength());
    }

    // update frame label
    updateFrameRelatedViews();
  }

  public void setController(BrowserController controller) {
    this.controller = controller;
  }

  // control methods
  // ---------------
  public void play() {

    System.out.println("[VideoPlayer] Playing...");

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // reset the currentFrame to 0 (it happens when it automatically reaches the end)
    if (currentFrame >= numFrameList.get(currentVideoID - 1)) {
      System.out.println("[VideoPlayer] Reset current frame to 0");
      currentFrame = 0;
    }

    state = PlayerState.PLAYING;
    playAudio();

    scheduler.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {

        if (state == PlayerState.STOP) {
          // STOP
          stopAudio();
          currentFrame = 0;
          updateFrameRelatedViews();
          scheduler.shutdownNow();
        } else if (state == PlayerState.PAUSE) {
          // PAUSE
          pauseAudio();
          updateFrameRelatedViews();
          scheduler.shutdownNow();
        } else {
          // PLAYING
          if (currentFrame < numFrameList.get(currentVideoID - 1)) {
            updateFrameRelatedViews();
            ++currentFrame;
          } else {
            // end of the video
            state = PlayerState.STOP;
            stopAudio();
            controller.playerStopNotification();
            scheduler.shutdownNow();
          }
        }
      }
    }, 0, Constants.FPS_NANOSECOND_INTERVAL, TimeUnit.NANOSECONDS);
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
  public void setCurrentFrame(int videoID, int currentFrame) {
    System.out.println("[VideoPlayer] CurrentFrame Updated (From 1): " + (currentFrame + 1) + "  in video " + videoID);
    this.currentVideoID = videoID;
    this.currentFrame = currentFrame;
    pauseAudio();
    updateFrameRelatedViews();

    if (state == PlayerState.STOP || state == PlayerState.PAUSE) {
      if (currentFrame == 0) {
        state = PlayerState.STOP;
      } else if (currentFrame == numFrameList.get(videoID - 1) - 1) {
        state = PlayerState.STOP;
        stopAudio();
        this.currentFrame = numFrameList.get(videoID - 1);
      } else {
        state = PlayerState.PAUSE;
      }
    } else {
      playAudio();
    }
  }

  private void updateFrameRelatedViews() {
    controller.setupProgressBarRange(numFrameList.get(currentVideoID - 1));
    controller.updateFrameInView(currentFrame);
    controller.updateFrameLabelValues(currentFrame, numFrameList.get(currentVideoID - 1));
    controller.updateProgressBarValue(currentFrame);
  }

  // audio methods
  // -------------
  private void playAudio() {
    Clip audioClip = audioClipList.get(currentVideoID - 1);
    if (audioClip != null) {
      audioClip.setMicrosecondPosition(currentClipLoc);
      audioClip.start();
    }
  }

  private void pauseAudio() {
    Clip audioClip = audioClipList.get(currentVideoID - 1);
    if (audioClip != null) {
      // currentClipLoc = audioClip.getMicrosecondPosition();
      // correction
      long microSecondInterval = Constants.FPS_MILLISECOND_PART * 1000 + Constants.FPS_NANOSECOND_PART / 1000;
      currentClipLoc = microSecondInterval * (currentFrame);
      // System.out.println("Audio Paused at " + currentClipLoc);

      audioClip.stop();
    }
  }

  private void stopAudio() {
    Clip audioClip = audioClipList.get(currentVideoID - 1);
    if (audioClip != null) {
      currentClipLoc = 0;
      audioClip.stop();
    }
  }

  public void setVolume(int level) {
    for (Clip audioClip : audioClipList) {
      if (audioClip != null) {
        FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10((float) level / (float) 100));
      }
    }
  }


  // getter
  // ------
  public int getNumFrame() {
    return numFrameList.get(currentVideoID - 1);
  }

  public int getCurrentVideoID() {
    return currentVideoID;
  }
}
