import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BrowserController implements ActionListener {

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

  public void init() {
    updateFrameInView(0);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand() == "PlayButton") {
      System.out.println("[BrowserController] PlayButton Clicked");

      if (player.state == PlayerState.PAUSE) {

        player.play();

        view.playButton.setText("Pause");

      } else {  // PLAYING

        player.pause();

        view.playButton.setText("Play");

      }

    } else if (e.getActionCommand() == "StopButton") {
      System.out.println("[BrowserController] StopButton Clicked");

      player.stop();
      view.playButton.setText("Play");
    }
  }

  public void updateFrameInView(int currentFrame) {
    view.showImg(model.frameList.get(currentFrame));
  }

  public void playerStopNotification() {
    view.playButton.setText("Play");
  }
}
