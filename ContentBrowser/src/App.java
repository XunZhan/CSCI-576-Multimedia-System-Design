import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

public class App {

  // main
  public static void main(String[] args) {
    new App();
  }

  private DisplayView displayView;
  private Model model;

  public App() {

    String directory = "./";  // path to the folder
    System.out.println("[App] Directory: " + directory);

    // Display View
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        displayView = new DisplayView();
      }
    });

    // VideoParser
    VideoParser vp = new VideoParser(directory + Constants.VIDEO_DIR);
    List<BufferedImage> frameList = vp.parse();

    // Audio File
    File audioFile = new File(directory + Constants.AUDIO_FILE_PATH);
    Clip clip = null;
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (Exception e) {
      System.out.println("[App] No Audio.");
      e.printStackTrace();
    }

    // Model
    model = new Model(frameList);

    // VideoPlayer & Controller
    VideoPlayer player = new VideoPlayer();
    BrowserController controller = new BrowserController(model, displayView);

    player.setController(controller);
    player.setDataSource(frameList, clip);

    controller.setPlayer(player);
    // controller.init();

    displayView.initListener(controller);

    System.out.println("[App] Initialization Finished.");
  }

}
