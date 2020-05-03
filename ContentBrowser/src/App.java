import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;

public class App {

  // main
  public static void main(String[] args) {
    new App();
  }

  public App() {

    String rootDirectory = Constants.ROOT_DIR;
    String testdataDirectory = Constants.TESTDATA_DIR;
    System.out.println("[App] Root Directory: " + rootDirectory);
    System.out.println("[App] TestData Directory: " + testdataDirectory);

    // Display View
    DisplayView displayView = new DisplayView();
    displayView.initDialogView();
    // should have been on EDT thread, but here we need to use dialog label in Parser)

    // SwingUtilities.invokeLater(new Runnable() {
    //   @Override
    //   public void run() {
    //     displayView = new DisplayView();
      // }
    // });

    // Parser
    Parser parser = new Parser(rootDirectory, testdataDirectory);
    parser.setDialogLabel(displayView.getDialogLabel());
    BufferedImage synopsisImg = parser.loadSynopsis();
    MetaData metaData = parser.loadMetafile();
    int numVideo = metaData.getNumVideo();
    List<Clip> clipList = parser.loadAudio(numVideo);
    List<List<BufferedImage>> frameList = parser.loadFrames(numVideo);
    List<BufferedImage> imageList = parser.loadImages(metaData.getImageFileNameList(), numVideo);

    displayView.dismissDialog();
    displayView.initDisplayView(numVideo);

    // Model
    Model model = new Model(frameList, imageList, metaData, synopsisImg);

    // VideoPlayer & Controller
    VideoPlayer player = new VideoPlayer();
    BrowserController controller = new BrowserController(model, displayView);

    player.setController(controller);
    player.setDataSource(frameList, clipList);

    controller.setPlayer(player);
    controller.showSynopsisImage();
    controller.showSynopsisTypeText();

    // display view - listeners
    displayView.initListener(controller);

    System.out.println("[App] Initialization Finished.");
  }

}
