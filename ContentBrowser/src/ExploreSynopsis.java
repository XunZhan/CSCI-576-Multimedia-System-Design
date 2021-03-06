import java.awt.image.BufferedImage;
import java.util.List;

import javax.sound.sampled.Clip;

public class ExploreSynopsis {

  // main
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("[Explore Synopsis] Invalid Argument.");
      return;
    }
    new ExploreSynopsis(args);
  }

  public ExploreSynopsis(String[] args) {

    String rootDirectory = Constants.ROOT_DIR;
    String testdataDirectory = Constants.TESTDATA_DIR;
    System.out.println("[Explore Synopsis] Root Directory: " + rootDirectory);
    System.out.println("[Explore Synopsis] TestData Directory: " + testdataDirectory);

    // Display View
    DisplayView displayView = new DisplayView();
    displayView.initDialogView();
    // should have been on EDT thread, but here we need to use dialog label in Parser)

    // Parser
    Parser parser = new Parser(rootDirectory, testdataDirectory);
    parser.setDialogLabel(displayView.getDialogLabel());
    MetaData metaData = parser.loadMetafile();
    BufferedImage synopsisImg = parser.loadSynopsis(args[0], metaData.getSynopsisOriginalWidth(), metaData.getSynopsisOriginalHeight());
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

    System.out.println("[Explore Synopsis] Initialization Finished.");
  }

}
