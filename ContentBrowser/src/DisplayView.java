import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class DisplayView extends JFrame {

  private class ProgressUI extends BasicProgressBarUI {

    private JProgressBar jProgressBar;
    private Color forecolor;
    private Color backcolor;

    ProgressUI(JProgressBar jProgressBar, Color forecolor, Color backcolor) {
      this.jProgressBar = jProgressBar;
      this.forecolor = forecolor;
      this.backcolor = backcolor;
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
      this.jProgressBar.setBackground(backcolor);
      this.jProgressBar.setForeground(forecolor);

      super.paintDeterminate(g, c);
    }
  }

  private JDialog dialog;
  private JLabel dialogLabel;

  private JLabel soundLabel;
  private JLabel frameLabel;
  private JLabel displayLabel;
  private JLabel synopsisLabel;

  private JButton playButton;
  private JButton stopButton;

  private JProgressBar progressBar;
  private JSlider soundSlider;

  // resources
  private Image playImage;
  private Image pauseImage;
  private Image stopImage;
  private Image soundImage;
  private Image dialogImage;

  // constructor
  public DisplayView() {
    super("CSCI-576 Content Browser");

    initResources();

    JPanel mainPanel = new JPanel();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(200, 200, 0, 0);
    setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
    setResizable(false);

    // Main Panel
    // ----------
    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    mainPanel.setLayout(new BorderLayout(0, 0));


    // Display Panel
    // -------------
    Dimension displayDimension = new Dimension(Constants.DISPLAY_WIDTH, Constants.DISPLAY_HEIGHT);
    JPanel displayPanel = new JPanel();
    displayLabel = new JLabel();
    displayLabel.setHorizontalAlignment(JLabel.CENTER);
    displayLabel.setVerticalAlignment(JLabel.CENTER);

    displayPanel.setLayout(new BorderLayout(0, 0));
    displayPanel.add(displayLabel);
    displayPanel.setPreferredSize(displayDimension);
    displayPanel.setMinimumSize(displayDimension);
    displayPanel.setMaximumSize(displayDimension);

    displayPanel.setBackground(Color.BLACK);

    // Control Panel
    // -------------
    Dimension controlDimension = new Dimension(Constants.CONTROL_WIDTH, Constants.CONTROL_HEIGHT);

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new FlowLayout());
    addComponentsToControlPanel(controlPanel);
    // controlPanel.setBackground(Color.YELLOW);

    // Synopsis Panel
    // --------------
    Dimension synopsisDimension = new Dimension(Constants.SYNOPSIS_WIDTH, Constants.SYNOPSIS_HEIGHT);
    JPanel synopsisPanel = new JPanel();
    synopsisLabel = new JLabel();

    synopsisPanel.setLayout(new BorderLayout(0, 0));
    synopsisPanel.add(synopsisLabel);
    synopsisPanel.setPreferredSize(synopsisDimension);
    synopsisPanel.setMinimumSize(synopsisDimension);
    synopsisPanel.setMaximumSize(synopsisDimension);

    synopsisPanel.setBackground(Color.BLACK);

    // Add
    mainPanel.add(displayPanel, BorderLayout.NORTH);
    mainPanel.add(controlPanel, BorderLayout.CENTER);
    mainPanel.add(synopsisPanel, BorderLayout.SOUTH);

    setContentPane(mainPanel);

    pack();
    setVisible(true);

    showDialog();
  }

  private void initResources() {
    int size = Constants.BUTTON_ICON_SIZE;
    try {
      playImage = ImageIO.read(new File("resources/play.png")).getScaledInstance(size, size, Image.SCALE_SMOOTH);
      pauseImage = ImageIO.read(new File("resources/pause.png")).getScaledInstance(size, size, Image.SCALE_SMOOTH);
      stopImage = ImageIO.read(new File("resources/stop.png")).getScaledInstance(size, size, Image.SCALE_SMOOTH);
      soundImage = ImageIO.read(new File("resources/sound.png")).getScaledInstance(size, size, Image.SCALE_SMOOTH);
      dialogImage = ImageIO.read(new File("resources/dialog.png")).getScaledInstance(Constants.DIALOG_ICON_SIZE, Constants.DIALOG_ICON_SIZE, Image.SCALE_SMOOTH);
    } catch (Exception e) {
      System.out.println("[DisplayView] Button Icon Exception.");
    }
  }


  private void addComponentsToControlPanel(JPanel controlPanel) {
    Dimension buttonDimension = new Dimension(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE);

    // play button
    playButton = new JButton();
    playButton.setActionCommand("PlayButton");
    playButton.setPreferredSize(buttonDimension);
    playButton.setBorderPainted(false);
    setPlayButtonState(0);

    // stop button
    stopButton = new JButton();
    stopButton.setActionCommand("StopButton");
    stopButton.setPreferredSize(buttonDimension);
    stopButton.setBorderPainted(false);
    setStopButtonState();

    // frame label
    Dimension frameLabelDimension = new Dimension(Constants.FRAME_LABEL_WIDTH, Constants.FRAME_LABEL_HEIGHT);
    frameLabel = new JLabel("1 / 2000");
    frameLabel.setUI(new BasicLabelUI());
    frameLabel.setPreferredSize(frameLabelDimension);
    frameLabel.setHorizontalAlignment(JLabel.CENTER);

    // progress bar
    Dimension barDimension = new Dimension(Constants.PROGRESSBAR_WIDTH, Constants.PROGRESSBAR_HEIGHT);
    progressBar = new JProgressBar();
    progressBar.setOpaque(true);
    progressBar.setUI(new ProgressUI(progressBar, new Color(59, 136, 253), Color.LIGHT_GRAY));
    progressBar.setPreferredSize(barDimension);
    progressBar.setBorderPainted(false);
    progressBar.setStringPainted(false);

    // sound label
    soundLabel = new JLabel();
    soundLabel.setPreferredSize(buttonDimension);
    soundLabel.setIcon(new ImageIcon(soundImage));
    soundLabel.setHorizontalAlignment(JLabel.RIGHT);

    // sound slider
    Dimension sliderDimension = new Dimension(Constants.SLIDER_WIDTH, Constants.SLIDER_HEIGHT);
    soundSlider = new JSlider(0, 100);
    soundSlider.setUI(new BasicSliderUI(soundSlider));
    soundSlider.setPreferredSize(sliderDimension);
    soundSlider.setValue(100);

    // add
    controlPanel.add(playButton);
    controlPanel.add(stopButton);
    controlPanel.add(frameLabel);
    controlPanel.add(progressBar);
    controlPanel.add(soundLabel);
    controlPanel.add(soundSlider);
  }

  private void showDialog() {
    dialog = new JDialog(this, "Loading", false);
    dialog.setLocationRelativeTo(this);
    dialog.setLocation(540, 370);  // mysterious values
    dialog.setAlwaysOnTop(true);
    dialog.setSize(Constants.DIALOG_WIDTH, Constants.DIALOG_HEIGHT);
    dialog.setResizable(false);
    dialog.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
    JLabel iconLabel = new JLabel();
    // iconLabel.setOpaque(true);
    // iconLabel.setBackground(Color.orange);
    iconLabel.setIcon(new ImageIcon(dialogImage));
    dialogLabel = new JLabel();
    // dialogLabel.setOpaque(true);
    // dialogLabel.setBackground(Color.GRAY);
    dialog.add(iconLabel);
    dialog.add(dialogLabel);

    dialog.setVisible(true);
  }


  public void initListener(BrowserController controller) {
    // Button
    playButton.addActionListener(controller);
    stopButton.addActionListener(controller);
    // ProgressBar
    progressBar.addMouseListener(controller);
    // Slider
    soundSlider.addChangeListener(controller);
  }

  public void showImg(BufferedImage img) {
    float ratio = (float) img.getWidth() / (float) img.getHeight();
    int width = (int) (ratio * Constants.DISPLAY_HEIGHT);

    displayLabel.setIcon(new ImageIcon(img.getScaledInstance(width, Constants.DISPLAY_HEIGHT, Image.SCALE_SMOOTH)));
  }


  public void setFrameLabelValues(int current, int total) {
    frameLabel.setText(current + " / " + total);
  }

  public void setPlayButtonState(int state) {
    if (state == 0) {
      playButton.setIcon(new ImageIcon(playImage));
    } else {
      playButton.setIcon(new ImageIcon(pauseImage));
    }
  }

  public void setStopButtonState() {
    stopButton.setIcon(new ImageIcon(stopImage));
  }

  public void setSoundSliderValue(int level) {
    soundSlider.setValue(level);
  }

  public void setProgressBarValue(int val) {
    progressBar.setValue(val);
  }

  public void setProgressBarRange(int min, int max) {
    progressBar.setMinimum(min);
    progressBar.setMaximum(max);
  }



  // Dialog
  // ------
  public JLabel getDialogLabel() {
    return dialogLabel;
  }

  public void dismissDialog() {
    dialog.dispose();
  }

}
