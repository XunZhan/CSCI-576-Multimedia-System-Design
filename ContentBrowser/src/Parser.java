import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;

public class Parser {
  private String rootDirectory;
  private String testdataDirectory;
  private ImageReader imageReader;

  private JLabel dialogLabel;
  private boolean isSynopsisLoaded;
  private boolean isMetafileLoaded;

  private int numFrame = 0;  // bad design --> but keep it for dialog
  private int numImage = 0;

  // constructor
  // -----------
  public Parser(String rDir, String tDir) {
    rootDirectory = rDir;
    testdataDirectory = tDir;
    imageReader = new ImageReader();
  }

  public void setDialogLabel(JLabel label) {
    dialogLabel = label;
  }

  // loadSynopsis
  // ------------
  public BufferedImage loadSynopsis() {
    System.out.print("[Parser] Loading Synopsis ................ ");
    // BufferedImage img = imageReader.read(directory + Constants.SYNOPSIS_FILE);
    try {
      BufferedImage img = ImageIO.read(new File(rootDirectory + Constants.SYNOPSIS_FILE));

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
    System.out.print("[Parser] Loading Metafile ................ ");
    String fileName = Constants.META_FILE;
    List<Item> itemList = new ArrayList<>();
    List<String> imageFileNameList = new ArrayList();
    int imgWidth = 0;
    int imgHeight = 0;
    int imgSpan = 0;
    int numVideo = 0;

    try (FileReader reader = new FileReader(rootDirectory + fileName);
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
        } else if ("numVideo".equals(type)) {
          // line 2
          numVideo = Integer.parseInt(result[1]);
        } else if ("frame".equals(type)) {
          // frame
          int videoID = Integer.parseInt(result[1]);
          int frameNumber = Integer.parseInt(result[2]);
          int shotStartNumber = Integer.parseInt(result[3]);
          int shotEndNumber = Integer.parseInt(result[4]);
          int index = frameNumber - 1;
          int startIndex = shotStartNumber - 1;
          int endIndex = shotEndNumber - 1;
          itemList.add(new FrameItem(videoID, index, startIndex, endIndex));
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

    return new MetaData((int) (imgWidth / ratio), Constants.SYNOPSIS_HEIGHT, (int) (imgSpan / ratio), numVideo, itemList, imageFileNameList);
  }

  // loadAudio
  // ---------
  public List<Clip> loadAudio(int numVideo) {
    System.out.print("[Parser] Loading Audios .................. ");

    List<Clip> clipList = new ArrayList<>();

    for (int i = 0; i < numVideo; ++i) {
      int videoID = i + 1;
      File audioFile = new File(rootDirectory + testdataDirectory + Constants.VIDEO_DIR + videoID + Constants.AUDIO_FILE_NAME);
      Clip clip = null;
      try {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clipList.add(clip);
      } catch (Exception e) {
        System.out.printf("Completed (0 audio).\n");
        clipList.add(null);
      }
    }

    System.out.printf("Completed (" + numVideo + " audios).\n");

    return clipList;
  }


  // loadFrames
  // ----------
  public List<List<BufferedImage>> loadFrames(int numVideo) {

    // calculate total number of frames
    List<String[]> fileNameList = new ArrayList<>();
    numFrame = 0;
    for (int i = 0; i < numVideo; ++i) {
      int videoID = i + 1;
      File dirFile = new File(rootDirectory + testdataDirectory + Constants.VIDEO_DIR + videoID);
      String[] arr = dirFile.list();
      Arrays.sort(arr);
      for (String s : arr) {
        if (s.endsWith(".rgb")) {
          numFrame += 1;
        }
      }
      fileNameList.add(arr);
    }

    List<List<BufferedImage>> frameList = new ArrayList<>();

    // load frames
    int count = 1;
    for (int i = 0; i < numVideo; ++i) {
      int videoID = i + 1;
      System.out.print("[Parser] Loading Frames for Video " + videoID + " ...... ");
      List<BufferedImage> list = new ArrayList<>();
      for (String s : fileNameList.get(i)) {
        if (s.endsWith(".rgb")) {
          String fileStr = rootDirectory + testdataDirectory + Constants.VIDEO_DIR + videoID + "/" + s;
          BufferedImage img = imageReader.read(fileStr, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
          list.add(img);
          setDialogInfo(numVideo, numVideo, count, numFrame,  0, numImage);
          ++count;
        }
      }
      frameList.add(list);
      System.out.printf("Completed (%d frames).\n", list.size());
    }

    return frameList;
  }


  // loadIamges
  // ----------
  public List<BufferedImage> loadImages(List<String> fileNameList, int numVideo) {
    File dirFile = new File(rootDirectory + testdataDirectory + Constants.IMAGE_DIR);
    if (dirFile.exists() == false) {
      System.out.println("[Parser] The image directory is invalid.");
      throw new IllegalArgumentException();
    }

    System.out.print("[Parser] Loading Images .................. ");
    List<BufferedImage> imageList = new ArrayList<>();

    for (int i = 0; i < fileNameList.size(); ++i) {
      String fileName = dirFile.getPath() + "/" + fileNameList.get(i);
      File imgFile = new File(fileName);

      setDialogInfo(numVideo, numVideo, numFrame, numFrame, i + 1, numImage);

      BufferedImage img = this.imageReader.read(fileName, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
      imageList.add(img);
    }

    System.out.printf("Completed (%d images).\n", numImage);
    return imageList;
  }


  // setDialogInfo
  // -------------
  private void setDialogInfo(int currAudio, int totalAudio,
                             int currFrame, int totalNumFrame,
                             int currImage, int totalNumImage) {
    String synopsisStr = isSynopsisLoaded ? "Yes" : "No";
    String metafileStr = isSynopsisLoaded ? "Yes" : "No";
    dialogLabel.setText(String.format("<html>[Synopsis]  &nbsp;%s<br>[Metafile]  &nbsp;%s<br>[Audio]  &nbsp;%d / %d<br>[Frame]  &nbsp;%d / %d<br>[Image]  &nbsp;%d / %d<br></html>",
            synopsisStr, metafileStr,
            currAudio, totalAudio,
            currFrame, totalNumFrame,
            currImage, totalNumImage));
  }


}


