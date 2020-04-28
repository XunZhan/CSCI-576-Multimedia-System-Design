import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
  public void mouseClicked(MouseEvent e) {

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
        view.showImg(model.frameList.get(currentFrame));
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
        // if (currentValue == 1) {
        //   view.setProgressBarValue(0);
        // } else {
        //   float fraction = (float) currentValue / (float) totalNumFrame;  // 400 / 400
        //   view.setProgressBarValue((int) (fraction * 100.0f));
        // }
        // if (currentFrame >= total - 1)
        // float fraction = (float) currentFrame / (float) total;
        // value = fraction * 100;
        // view.setProgressBarValue(value);
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
  public void mousePressed(MouseEvent e) {

  }
}
