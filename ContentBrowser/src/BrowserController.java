import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Controller {
  public static void main(String[] args) {
    String arg1 = "./";  // path to the folder

    Controller cb = new Controller(arg1);
  }

  // constant
  private final static int WINDOW_WIDTH = 500;
  private final static int WINDOW_HEIGHT = 450;
  private final static int IMAGE_WIDTH = 352;
  private final static int IMAGE_HEIGHT = 288;

  // member variables
  private Display display;
  private VideoPlayer player;

  // constructor
  public Controller(String directory) {
    System.out.println("[ContentBrowser] Directory: " + directory);

    // Display
    this.display = new Display(WINDOW_WIDTH, WINDOW_HEIGHT);
    this.display.buildUI();

    // VideoParser
    VideoParser vp = new VideoParser(directory + "/video", IMAGE_WIDTH, IMAGE_HEIGHT);
    List<BufferedImage> frameList = vp.parse();

    // Audio
    File audioFile = new File(directory + "audio/sound.wav");
    AudioInputStream audioInputStream = null;
    try {
      audioInputStream = AudioSystem.getAudioInputStream(audioFile);
    } catch (UnsupportedAudioFileException | IOException e) {
      System.out.println("[Audio] No audio file or the file is not supported.");
    }

    // VideoPlayer
    this.player = new VideoPlayer(frameList, audioInputStream);
    this.player.setDisplay(this.display);

    // Listener
    addListener();
  }


  // Events
  private void addListener() {
    // playButton
    this.display.playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (player.getState() == PlayerState.PAUSE) {
          System.out.println("[Display] Play Button Clicked (-> PLAYING)");
          display.playButton.setText("Pause");
          player.play();
        } else  // PLAYING
        {
          System.out.println("[Display] Play Button Clicked (-> PAUSE)");
          display.playButton.setText("Resume");
          player.pause();
        }
      }
    });

    // stopButton
    this.display.stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("[Display] Stop Button Clicked");
        display.playButton.setText("Play");
        player.stop();
      }
    });
  }

}
