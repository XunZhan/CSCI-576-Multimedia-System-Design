import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BrowserController implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {

  private DisplayView view;
  private Model model;
  private VideoPlayer player;


  // constructor
  public BrowserController(Model model, DisplayView view) {
    this.model = model;
    this.view = view;
  }

  public void setPlayer(VideoPlayer player) {
    this.player = player;
  }

  // Synopsis
  // --------
  public void showSynopsisImage() {
    view.setSynopsisImage(model.synopsisImage);
  }

  public void showSynopsisTypeText() {
    view.setSynopsisLabelMetadata(model.metaData);
  }


  // listener
  // --------
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("PlayButton")) {

      int currentSelectedIndex = view.getSynopsisLabelCurrentSelectedIndex();

      // deselect the rectangle when the current selection is an image
      if (currentSelectedIndex != -1) {
        if (model.metaData.getItemList().get(currentSelectedIndex).getType() == ItemType.IMAGE) {
          view.setSynopsisLabelCurrentSelectedIndex(-1);
        }
      }

      if (player.state == PlayerState.STOP || player.state == player.state.PAUSE) {
        player.play();
        view.setPlayButtonState(1);
        if (player.state == PlayerState.STOP) {
          // update selected rect
          view.setSynopsisLabelCurrentSelectedIndex(-1);
        }
      } else {
        player.pause();
        view.setPlayButtonState(0);
      }
    } else if (e.getActionCommand().equals("StopButton")) {
      player.stop();
      view.setPlayButtonState(0);
      // update selected rect
      view.setSynopsisLabelCurrentSelectedIndex(-1);
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JSlider slider = (JSlider) e.getSource();
    player.setVolume(slider.getValue());
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getSource().getClass() == JButton.class) {
      // VIDEO BUTTON
      JButton btn = (JButton) e.getSource();
      String command = btn.getActionCommand();
      if (command.startsWith("VideoButton")) {
        int videoID = Integer.parseInt(command.split("VideoButton")[1]);
        // only when ID is different
        if (videoID != player.getCurrentVideoID()) {
          System.out.println("[BrowserController] Changed VideoID!");
          player.stop();
          view.setPlayButtonState(0);
          view.setVideoButtonSelected(videoID);
          player.setCurrentFrame(videoID, 0);
        }
      }

    } else if (e.getSource().getClass() == JProgressBar.class) {
      // PROGRESS BAR
      JProgressBar progressBar = (JProgressBar) e.getSource();
      float percentage = (float) e.getX() / (float) progressBar.getWidth();
      int newFrame = (int) (percentage * (float) player.getNumFrame());
      // it has been tested that it won't exceed the maximum (just in case)
      newFrame = Math.max(newFrame, 0);
      newFrame = Math.min(newFrame, (int) player.getNumFrame() - 1);
      player.setCurrentFrame(player.getCurrentVideoID(), newFrame);
      // update selected rect
      view.setSynopsisLabelCurrentSelectedIndex(-1);

    } else {
      // SYNOPSIS IMAGE
      JLabel label = (JLabel) e.getSource();
      if (e.getX() < 0 && e.getX() > label.getWidth())
        return;
      int index = e.getX() / model.metaData.getSynopsisSpan();
      Item item = model.metaData.getItemList().get(index);

      // stop
      if (player.state == PlayerState.PLAYING) {
        player.stop();
        view.setPlayButtonState(0);
      }

      if (item.getType() == ItemType.FRAME) {

        FrameItem fItem = (FrameItem) item;
        if (SwingUtilities.isRightMouseButton(e)) {
          // RIGHT Pressed
          view.setVideoButtonSelected(fItem.getVideoID());
          player.setCurrentFrame(fItem.getVideoID(), item.getIndex());
        } else if (SwingUtilities.isLeftMouseButton(e)) {
          // LEFT Pressed
          view.setVideoButtonSelected(fItem.getVideoID());
          System.out.println("[BrowserController] Left Clicked --> Shot Starting Frame");
          player.setCurrentFrame(fItem.getVideoID(), fItem.getShotStart());
        }

      } else {  // Image
        if (player.state == PlayerState.PLAYING || player.state == PlayerState.PAUSE) {
          player.stop();
          view.setPlayButtonState(0);
        }
        showImageInView(item.getIndex());
      }
      // update selected rect
      view.setSynopsisLabelCurrentSelectedIndex(index);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (player.state == PlayerState.PLAYING) {
      return;
    }

    if (e.getSource().getClass() == JProgressBar.class) {
      // PROGRESS BAR
      JProgressBar progressBar = (JProgressBar) e.getSource();
      float percentage = (float) e.getX() / (float) progressBar.getWidth();
      int newFrame = (int) (percentage * (float) player.getNumFrame());
      // it has been tested that it won't exceed the maximum (just in case)
      newFrame = Math.max(newFrame, 0);
      newFrame = Math.min(newFrame, player.getNumFrame() - 1);
      player.setCurrentFrame(player.getCurrentVideoID(), newFrame);
      view.setSynopsisLabelCurrentSelectedIndex(-1);

    } else if (e.getSource().getClass() == DisplayView.SynopsisLabel.class) {
      // SYNOPSIS IMAGE
      DisplayView.SynopsisLabel label = (DisplayView.SynopsisLabel) e.getSource();
      if (e.getX() < 0 && e.getX() > label.getWidth())
        return;
      int index = e.getX() / model.metaData.getSynopsisSpan();
      // check if leftClicked
      if (SwingUtilities.isLeftMouseButton(e) == false) {
        return;
      }
      // check if it is within the current selected rectangle
      if (index != label.getCurrentSelectedIndex()) {
        return;
      }
      Item item = model.metaData.getItemList().get(index);
      // only activated for FRAMES
      if (item.getType() == ItemType.FRAME) {
        FrameItem fItem = (FrameItem) item;
        int shotStartIndex = fItem.getShotStart();  // ex. 300
        int shotEndIndex = fItem.getShotEnd();  // ex. 360
        int numShotFrame = shotEndIndex - shotStartIndex;  // without -1 is acceptable
        int offsetX = e.getX() - model.metaData.getSynopsisSpan() * index;
        float percent = (float) offsetX / (float) model.metaData.getSynopsisSpan();
        int selectedFrame = shotStartIndex + (int) (percent * numShotFrame);
        // double-check
        selectedFrame = Math.max(shotStartIndex, selectedFrame);
        selectedFrame = Math.min(shotEndIndex, selectedFrame);
        player.setCurrentFrame(fItem.getVideoID(), selectedFrame);
      }
    }
  }

  // stop button
  // -----------
  public void playerStopNotification() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        view.setPlayButtonState(0);
      }
    });
  }


  // frame
  // -----
  public void updateFrameInView(int currentFrame) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (model.frameList != null && model.frameList.size() > 0) {
          view.showImg(model.frameList.get(player.getCurrentVideoID() - 1).get(currentFrame));
        }
      }
    });
  }

  public void updateFrameLabelValues(int currentFrame, int totalNumFrame) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        // convert from index to number (add 1)
        view.setFrameLabelValues(currentFrame + 1, totalNumFrame);
      }
    });
  }

  public void setupProgressBarRange(int totalNumFrame) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        view.setProgressBarRange(1, totalNumFrame);
      }
    });
  }

  public void updateProgressBarValue(int currentFrame) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int currentValue = currentFrame + 1;
        view.setProgressBarValue(currentValue);
      }
    });
  }


  // sound
  // -----
  public void updateSoundSliderValue(int level) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        view.setSoundSliderValue(level);
      }
    });
  }


  // image
  // -----
  public void showImageInView(int imgIndex) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (model.imageList != null && model.imageList.size() > 0) {
          System.out.println("[BrowserController] Showing Image at Index: " + imgIndex);
          view.showImg(model.imageList.get(imgIndex));
        }
      }
    });
  }


  // no use
  // ------
  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

  @Override
  public void mouseMoved(MouseEvent e) {

  }
}
