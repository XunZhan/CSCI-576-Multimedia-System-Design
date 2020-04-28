import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

  public Parser(String dir) {
    directory = dir;
    imageReader = new ImageReader();
  }

  public void setDialogLabel(JLabel label) {
    dialogLabel = label;
  }

  public void loadSynopsis() {
    System.out.print("[Parser] Loading Synopsis ...... ");
    System.out.printf("Completed (0 synopsis).\n");
    isSynopsisLoaded = false;
  }

  public void loadMetafile() {
    System.out.print("[Parser] Loading Metafile ...... ");
    System.out.printf("Completed (0 metafile).\n");
    isMetafileLoaded = false;
  }

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

  public List<BufferedImage> loadFrames() {
    // check directory
    File dirFile = new File(directory + Constants.VIDEO_DIR);
    if (dirFile.exists() == false) {
      System.out.println("[Parser] The video frame directory is invalid.");
      throw new IllegalArgumentException();
    }

    int numFrame = dirFile.listFiles().length - 1;

    System.out.print("[Parser] Loading Frames ...... ");
    List<BufferedImage> videoImages = new ArrayList<>();
    int frameIndex = 1;

    DecimalFormat f = new DecimalFormat("0000");
    while (true) {
      String fileName = dirFile.getPath() + "/" + Constants.FRAME_FILE_PREFIX + f.format(frameIndex) + ".rgb";
      File imgFile = new File(fileName);
      if (imgFile.exists() == false) {
        System.out.printf("Completed (%d frames).\n", frameIndex - 1);
        break;
      }

      setDialogInfo(frameIndex, numFrame, 0, 0);
      BufferedImage img = this.imageReader.read(fileName);

      videoImages.add(img);
      ++frameIndex;
    }

    return videoImages;
  }

  public List<BufferedImage> loadImages() {
    System.out.print("[Parser] Loading Images ...... ");
    System.out.printf("Completed (%d images).\n", 0);
    return null;
  }

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


