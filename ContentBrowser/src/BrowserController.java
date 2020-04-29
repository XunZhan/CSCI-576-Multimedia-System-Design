import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BrowserController implements ActionListener, ChangeListener, MouseListener {

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
    if (e.getActionCommand() == "PlayButton") {
      if (player.state == PlayerState.STOP || player.state == player.state.PAUSE) {
        player.play();
        view.setPlayButtonState(1);
      } else {
        player.pause();
        view.setPlayButtonState(0);
      }

    } else if (e.getActionCommand() == "StopButton") {
      player.stop();
      view.setPlayButtonState(0);
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JSlider slider = (JSlider) e.getSource();
    player.setVolume(slider.getValue());
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getSource().getClass() == JProgressBar.class) {
      // progressBar is clicked
      JProgressBar progressBar = (JProgressBar) e.getSource();
      // System.out.println(e.getX() + " / " + progressBar.getWidth());
      // 0 ~ 399
      // 0 ~ 550
      float percentage = (float) e.getX() / (float) progressBar.getWidth();
      int newFrame = (int) (percentage * (float) player.getNumFrame());
      // System.out.println(newFrame);
      // it has been tested that it won't exceed the maximum (just in case)
      newFrame = Math.min(newFrame, (int) player.getNumFrame());
      player.setCurrentFrame(newFrame);
    } else {
      // synopsis image is clicked
      // System.out.println("synopsis image clicked -> " + e.getX() + " / " + label.getWidth());
      int index = e.getX() / model.metaData.getSynopsisSpan();
      Item item = model.metaData.getItemList().get(index);
      if (item.getType() == ItemType.FRAME) {
        player.setCurrentFrame(item.getIndex());
      } else {  // Image
        player.stop();
        view.setPlayButtonState(0);
        showImageInView(item.getIndex());
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
          view.showImg(model.frameList.get(currentFrame));
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
}
