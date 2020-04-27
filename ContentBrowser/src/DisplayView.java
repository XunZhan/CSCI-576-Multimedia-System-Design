import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Display {
  private final static int WIDTH = 352;
  private final static int HEIGHT = 288;
  private final static double FPS = 29.94;

  private List<BufferedImage> imageList;

  private boolean isPlaying = false;

  private int currentFrame = 0;

  private Clip clip;
  private long clipLoc = 0;

  public static void main(String[] args) {
    Display display = new Display(800, 450);

    // Display
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        display.buildUI();
      }
    });

    VideoParser vp = new VideoParser("video", 352, 288);
    display.imageList = vp.parse();

    // Audio
    File audioFile = new File( "audio/sound.wav");
    AudioInputStream audioInputStream = null;
    try {
      audioInputStream = AudioSystem.getAudioInputStream(audioFile);
      display.clip = AudioSystem.getClip();
      display.clip.open(audioInputStream);
    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
      System.out.println("[Audio] No audio file or the file is not supported.");
    }

    // display.loop();

    // end
    System.out.println("[Finished]");
  }

  public void play() {
    System.out.println(Thread.currentThread().getName() + "play()");
    try {
      Thread.sleep(5000);
    } catch(Exception e) {

    }

  }

  public void loop() {

    int milliDelta = (int) ((double) 1 / FPS * 1000.0);  // miliseconds
    int nanoDelta = (int) (((double) 1 / FPS * 1000.0 - 33.0) * 1000 * 1000);  // nanoseconds
    long nanoError = 0;

    while (true) {

      try {
        Thread.sleep(0);
      } catch (Exception e) {

      }
      // OR

      // System.out.println(Thread.currentThread().getName() + " -> " + isPlaying);

      if (isPlaying) {
        System.out.println(clipLoc);
        this.clip.setMicrosecondPosition(clipLoc);
        this.clip.start();

        // playing
        while (currentFrame < imageList.size()) {

          // pause
          if (isPlaying == false) {
            this.clipLoc = clip.getMicrosecondPosition();
            this.clip.stop();
            System.out.println("pause at + " + clipLoc);
            break;
          }

          long start = System.nanoTime();
          showImg(imageList.get(currentFrame));
          long end = System.nanoTime();
          nanoError += end - start;
          long milli = milliDelta;
          if (nanoError > 10_000_000) {
            nanoError = 0;
            milli = milliDelta - 10;
          }
          try {
            Thread.sleep(milli, nanoDelta);
          } catch (InterruptedException e) {

          }

          ++currentFrame;
        }
      }


    }
  }


  public JFrame frame;
  public JLabel label;
  public JButton playButton;
  public JButton pauseButton;
  public JButton stopButton;

  private int windowWidth;
  private int windowHeight;

  public Display(int windowWidth, int windowHeight) {
    this.windowWidth = windowWidth;
    this.windowHeight = windowHeight;
  }

  public void buildUI() {
    // Use label to display the image
    this.frame = new JFrame();
    GridBagLayout gLayout = new GridBagLayout();
    this.frame.getContentPane().setLayout(gLayout);
    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.frame.setPreferredSize(new Dimension(windowWidth, windowHeight));
    addComponents(this.frame.getContentPane());

    addListener();
  }

  public void showImg(BufferedImage img) {
    this.label.setIcon(new ImageIcon(img));
  }

  private void addComponents(Container pane) {
    JPanel listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));

    // Image or Video Display Area
    JPanel displayPanel = new JPanel();
    displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.LINE_AXIS));
    this.label = new JLabel();
    GridBagConstraints o = new GridBagConstraints();
    o.fill = GridBagConstraints.HORIZONTAL;
    o.anchor = GridBagConstraints.CENTER;
//        o.weightx = 0.5;
//        o.gridx = 0;
//        o.gridy = 0;

    displayPanel.add(this.label, o);
    listPanel.add(displayPanel);

    // Buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout((new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS)));

    this.playButton = new JButton("Play");
    GridBagConstraints bp = new GridBagConstraints();
    bp.fill = GridBagConstraints.HORIZONTAL;
    bp.anchor = GridBagConstraints.CENTER;
//        bp.weightx = 0.5;
//        bp.gridx = 0;
//        bp.gridy = 1;

    this.pauseButton = new JButton("Pause");
    GridBagConstraints ps = new GridBagConstraints();
    ps.fill = GridBagConstraints.HORIZONTAL;
    ps.anchor = GridBagConstraints.CENTER;
//        bp.weightx = 0.5;
//        bp.gridx = 0;
//        bp.gridy = 1;

    this.stopButton = new JButton("Stop");
    GridBagConstraints bs = new GridBagConstraints();
    bs.fill = GridBagConstraints.HORIZONTAL;
    bs.anchor = GridBagConstraints.CENTER;
//        bs.weightx = 0.5;
//        bs.gridx = 1;
//        bs.gridy = 1;

    buttonPanel.add(this.playButton, bp);
    buttonPanel.add(this.pauseButton, ps);
    buttonPanel.add(this.stopButton, bs);

    // Synopsis Image
//        JPanel SynopsisPanel = new JPanel();
//        SynopsisPanel.setLayout(new BoxLayout(SynopsisPanel, BoxLayout.LINE_AXIS));
//        lbOrg = new JLabel();
//        GridBagConstraints sc = new GridBagConstraints();
//        sc.fill = GridBagConstraints.HORIZONTAL;
//        sc.anchor = GridBagConstraints.CENTER;

    listPanel.add(buttonPanel);
    pane.add(listPanel);

    this.frame.pack();
    this.frame.setVisible(true);
  }

  private void addListener() {
    this.label.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked: " + e.getX() + " " + e.getY());

      }

      @Override
      public void mousePressed(MouseEvent e) {
        //System.out.println("Pressed");
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        //System.out.println("Released");
      }
    });

    this.playButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Play Button Clicked");
        isPlaying = true;
      }
    });

    this.pauseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Pause Button Clicked");
        isPlaying = false;
      }
    });

    this.stopButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Stop Button Clicked");

        Thread thread = new Thread(new Runnable() {
          @Override
          public void run() {
            isPlaying = true;
            play();
          }
        }, "my thread");
          // @Override
          // public void run() {
          //   play();
          // }
        thread.start();
      }
    });
  }


}
