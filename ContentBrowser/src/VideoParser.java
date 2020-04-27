import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VideoParser {
  private String directory;
  private ImageReader imageReader;

  public VideoParser(String dir) {
    directory = dir;
    imageReader = new ImageReader();
  }

  public List<BufferedImage> parse() {
    // check directory
    File dirFile = new File(directory);
    if (dirFile.exists() == false) {
      System.out.println("[VideoParser] The directory is invalid.");
      throw new IllegalArgumentException();
    }

    // Frames
    // ------
    System.out.println("[VideoParser] Parsing Frames ...");
    List<BufferedImage> videoImages = new ArrayList<>();
    int frameIndex = 1;

    DecimalFormat f = new DecimalFormat("0000");
    while (true) {
      String fileName = directory + "/" + Constants.FRAME_FILE_PREFIX + f.format(frameIndex) + ".rgb";
      File imgFile = new File(fileName);
      if (imgFile.exists() == false) {
        System.out.printf("[VideoParser] Frame Parsing Completed (%d frames).\n", frameIndex - 1);
        break;
      }

      BufferedImage img = this.imageReader.read(fileName);

      videoImages.add(img);
      ++frameIndex;
    }

    return videoImages;
  }

}
