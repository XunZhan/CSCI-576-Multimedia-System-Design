import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;

public class App {

  // main
  public static void main(String[] args) {
    new App();
  }

  public App() {

    String directory = "./";  // path to the folder
    System.out.println("[App] Directory: " + directory);

    // Display View
    DisplayView displayView = new DisplayView();
    // should have been on EDT thread, but here we need to use dialog label in Parser)

    // SwingUtilities.invokeLater(new Runnable() {
    //   @Override
    //   public void run() {
    //     displayView = new DisplayView();
      // }
    // });

    // Parser
    Parser parser = new Parser(directory);
    parser.setDialogLabel(displayView.getDialogLabel());
    BufferedImage synopsisImg = parser.loadSynopsis();
    MetaData metaData = parser.loadMetafile();
    Clip clip = parser.loadAudio();
    List<BufferedImage> frameList = parser.loadFrames();
    List<BufferedImage> imageList = parser.loadImages(metaData.getImageFileNameList());

    displayView.dismissDialog();

    // Model
    Model model = new Model(frameList);

    // VideoPlayer & Controller
    VideoPlayer player = new VideoPlayer();
    BrowserController controller = new BrowserController(model, displayView);

    player.setController(controller);
    player.setDataSource(frameList, clip);

    controller.setPlayer(player);

    displayView.initListener(controller);

    System.out.println("[App] Initialization Finished.");
  }

}
