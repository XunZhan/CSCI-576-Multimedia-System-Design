import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;

public class Parser {
  private String directory;
  private ImageReader imageReader;

  private JLabel dialogLabel;
  private boolean isAudioLoaded;
  private boolean isSynopsisLoaded;
  private boolean isMetafileLoaded;

  private int numFrame = 0;  // bad design --> but keep it for dialog
  private int numImage = 0;

  // constructor
  // -----------
  public Parser(String dir) {
    directory = dir;
    imageReader = new ImageReader();
  }

  public void setDialogLabel(JLabel label) {
    dialogLabel = label;
  }

  // loadSynopsis
  // ------------
  public BufferedImage loadSynopsis() {
    System.out.print("[Parser] Loading Synopsis ...... ");
    // BufferedImage img = imageReader.read(directory + Constants.SYNOPSIS_FILE);
    try {
      BufferedImage img = ImageIO.read(new File(directory + Constants.SYNOPSIS_FILE));

      // re-scaled
      float ratio = (float) img.getHeight() / (float) Constants.SYNOPSIS_HEIGHT;
      Image ig = img.getScaledInstance((int) (img.getWidth() / ratio), Constants.SYNOPSIS_HEIGHT, Image.SCALE_SMOOTH);

      BufferedImage scaledImg = new BufferedImage(ig.getWidth(null), ig.getHeight(null), BufferedImage.TYPE_INT_ARGB);

      Graphics2D graphics2D = scaledImg.createGraphics();
      graphics2D.drawImage(ig, 0, 0, null);
      graphics2D.dispose();

      System.out.printf("Completed (1 synopsis).\n");
      isSynopsisLoaded = true;
      return scaledImg;
    } catch (IOException e) {
      System.out.printf("Completed (0 synopsis).\n");
      System.out.println("[Parser] Exception in Loading Synopsis Image.");
      return null;
    }
  }

  // loadMetafile
  // ------------
  public MetaData loadMetafile() {
    System.out.print("[Parser] Loading Metafile ...... ");
    String fileName = Constants.META_FILE;
    List<Item> itemList = new ArrayList<>();
    List<String> imageFileNameList = new ArrayList();
    int imgWidth = 0;
    int imgHeight = 0;
    int imgSpan = 0;

    try (FileReader reader = new FileReader(directory + fileName);
         BufferedReader br = new BufferedReader(reader)
    ) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] result = line.split(" ");
        String type = result[0];
        if ("size".equals(type)) {
          // line 0
          imgWidth = Integer.parseInt(result[1]);
          imgHeight = Integer.parseInt(result[2]);
        } else if ("span".equals(type)) {
          // line 1
          imgSpan = Integer.parseInt(result[1]);
        } else if ("frame".equals(type)) {
          // frame
          int frameNumber = Integer.parseInt(result[1]);
          int shotStartNumber = Integer.parseInt(result[2]);
          int shotEndNumber = Integer.parseInt(result[3]);
          int index = frameNumber - 1;
          int startIndex = shotStartNumber - 1;
          int endIndex = shotEndNumber - 1;
          itemList.add(new FrameItem(index, startIndex, endIndex));
        } else {
          // image
          int index = Integer.parseInt(result[1]);  // it is an index that starts from 0
          String imageFileName = result[2];
          itemList.add(new ImageItem(index));
          imageFileNameList.add(imageFileName);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("[Parser] Exception in Loading Metafile.");
    }

    System.out.printf("Completed (1 metafile).\n");
    isMetafileLoaded = true;

    // for dialog
    numImage = imageFileNameList.size();

    // re-scale
    float ratio = (float) imgHeight / (float) Constants.SYNOPSIS_HEIGHT;

    return new MetaData((int) (imgWidth / ratio), Constants.SYNOPSIS_HEIGHT, (int) (imgSpan / ratio), itemList, imageFileNameList);
  }

  // loadAudio
  // ---------
  public Clip loadAudio() {
    System.out.print("[Parser] Loading Audio ...... ");
    File audioFile = new File(directory + Constants.AUDIO_FILE_PATH);
    Clip clip = null;
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      System.out.printf("Completed (1 audio).\n");
      isAudioLoaded = true;
    } catch (Exception e) {
      System.out.printf("Completed (0 audio).\n");
      isAudioLoaded = false;
      return null;
    }
    return clip;
  }

  // loadFrames
  // ----------
  public List<BufferedImage> loadFrames() {
    // check directory
    File dirFile = new File(directory + Constants.VIDEO_DIR);
    if (dirFile.exists() == false) {
      System.out.println("[Parser] The video frame directory is invalid.");
      throw new IllegalArgumentException();
    }

    numFrame = dirFile.listFiles().length - 1;

    System.out.print("[Parser] Loading Frames ...... ");
    List<BufferedImage> videoImages = new ArrayList<>();
    int frameIndex = 1;

    DecimalFormat f = new DecimalFormat("0000");
    while (true) {
      String fileName = dirFile.getPath() + "/" + Constants.FRAME_FILE_PREFIX + f.format(frameIndex) + ".rgb";
      File imgFile = new File(fileName);
      if (imgFile.exists() == false) {
        System.out.printf("Completed (%d frames).\n", numFrame);
        break;
      }

      setDialogInfo(frameIndex, numFrame, 0, numImage);
      BufferedImage img = this.imageReader.read(fileName, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);

      videoImages.add(img);
      ++frameIndex;
    }

    return videoImages;
  }

  // loadIamges
  // ----------
  public List<BufferedImage> loadImages(List<String> fileNameList) {
    File dirFile = new File(directory + Constants.IMAGE_DIR);
    if (dirFile.exists() == false) {
      System.out.println("[Parser] The image directory is invalid.");
      throw new IllegalArgumentException();
    }

    System.out.print("[Parser] Loading Images ...... ");
    List<BufferedImage> imageList = new ArrayList<>();

    for (int i = 0; i < fileNameList.size(); ++i) {
      String fileName = dirFile.getPath() + "/" + fileNameList.get(i);
      File imgFile = new File(fileName);

      setDialogInfo(numFrame, numFrame, i + 1, numImage);

      BufferedImage img = this.imageReader.read(fileName, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
      imageList.add(img);
    }

    System.out.printf("Completed (%d images).\n", numImage);
    return imageList;
  }

  // setDialogInfo
  // -------------
  private void setDialogInfo(int currFrame, int totalNumFrame,
                             int currImage, int totalNumImage) {
    String audioStr = isAudioLoaded ? "Yes" : "No";
    String synopsisStr = isSynopsisLoaded ? "Yes" : "No";
    String metafileStr = isSynopsisLoaded ? "Yes" : "No";
    dialogLabel.setText(String.format("<html>[Synopsis]  &nbsp;%s<br>[Metafile]  &nbsp;%s<br>[Audio]  &nbsp;%s<br>[Frame]  &nbsp;%d / %d<br>[Image]  &nbsp;%d / %d<br></html>",
            synopsisStr, metafileStr, audioStr,
            currFrame, totalNumFrame,
            currImage, totalNumImage));
  }


}


