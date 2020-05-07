import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class DisplayView extends JFrame {

  // ProgressUI
  // ----------
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


  // SynopsisLabel
  // -------------
  public class SynopsisLabel extends JLabel {
    // set [public] because we use this DisplayView.SynopsisLabel in mouseMoved in Controller
    private MetaData metaData;
    private int currentSelectedIndex = -1;

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (metaData != null) {
        draw(g);
      }
    }

    private void draw(Graphics g) {

      Graphics2D g2d = (Graphics2D) g.create();

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2d.setFont(new Font("Menlo", Font.BOLD, Constants.SYNOPSIS_TEXT_SIZE));

      int x = metaData.getSynopsisSpan();
      int y = metaData.getSynopsisHeight();
      List<Item> itemList = metaData.getItemList();
      for (int i = 0; i < itemList.size(); ++i) {
        ItemType type = itemList.get(i).getType();
        int xxText = x - Constants.SYNOPSIS_TEXT_OFFSET_X;
        int yyText = y - Constants.SYNOPSIS_TEXT_OFFSET_Y;
        int xxRect = x - Constants.SYNOPSIS_RECT_OFFSET;
        int yyRect = y - Constants.SYNOPSIS_RECT_OFFSET;

        if (type == ItemType.FRAME) {
          g2d.setColor(Constants.SYNOPSIS_FRAME_RECT_COLOR);
          // g2d.fillOval(xx - 15, yy - 15, 15, 15);
          g2d.fillRoundRect(xxRect, yyRect, Constants.SYNOPSIS_RECT_SIZE, Constants.SYNOPSIS_RECT_SIZE, Constants.SYNOPSIS_RECT_CORNER, Constants.SYNOPSIS_RECT_CORNER);

          g2d.setColor(Color.WHITE);
          FrameItem fItem = (FrameItem) itemList.get(i);
          g2d.drawString(fItem.getVideoID() + "", xxText, yyText);
        } else {
          g2d.setColor(Constants.SYNOPSIS_IMAGE_RECT_COLOR);
          // g2d.fillOval(xx - 15, yy - 15, 15, 15);
          g2d.fillRoundRect(xxRect, yyRect, Constants.SYNOPSIS_RECT_SIZE, Constants.SYNOPSIS_RECT_SIZE, Constants.SYNOPSIS_RECT_CORNER, Constants.SYNOPSIS_RECT_CORNER);

          g2d.setColor(Color.WHITE);
          g2d.drawString("I", xxText, yyText);  // lmao design
        }
        x += metaData.getSynopsisSpan();
      }

      // draw selected rectangle
      if (currentSelectedIndex != -1) {
        if (metaData.getItemList().get(currentSelectedIndex).getType() == ItemType.FRAME) {
          g2d.setColor(Constants.SYNOPSIS_FRAME_RECT_COLOR);
        } else {
          g2d.setColor(Constants.SYNOPSIS_IMAGE_RECT_COLOR);
        }
        // g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(Constants.SYNOPSIS_SELECTED_RECT_THICKNESS));
        g2d.drawRoundRect(
                metaData.getSynopsisSpan() * currentSelectedIndex + currentSelectedIndex / 10,
                1,
                metaData.getSynopsisSpan() + 2,
                metaData.getSynopsisHeight() - 3, Constants.SYNOPSIS_SELECTED_RECT_CORNER, Constants.SYNOPSIS_SELECTED_RECT_CORNER);
      }

      g2d.dispose();
    }

    public void setMetaData(MetaData data) {
      metaData = data;
      updateUI();
    }

    public void setCurrentSelectedIndex(int index) {
      currentSelectedIndex = index;
      updateUI();
    }

    public int getCurrentSelectedIndex() {
      return currentSelectedIndex;
    }
  }

  private JPanel mainPanel;

  private JDialog dialog;
  private JLabel dialogLabel;

  private JLabel soundLabel;
  private JLabel frameLabel;
  private JLabel displayLabel;
  private SynopsisLabel synopsisLabel;

  private JButton playButton;
  private JButton stopButton;

  private List<JButton> videoButtonList;

  private JProgressBar progressBar;
  private JSlider soundSlider;

  // resources
  private Image playImage;
  private Image pauseImage;
  private Image stopImage;
  private Image soundImage;
  private Image dialogImage;

  // constructor
  // -----------
  public DisplayView() {
    super("CSCI-576  |  Explore Synopsis  |  " + Constants.TESTDATA_DIR + "  |  Authors: Junhao Wang & Xun Zhan");

    initResources();

    mainPanel = new JPanel();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(200, 200, 0, 0);
    setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
    setResizable(false);

    // Main Panel
    // ----------
    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    mainPanel.setLayout(new BorderLayout(0, 0));
    // mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
    // mainPanel.setLayout(new BorderLayout(0, 5));
  }

  // DialogView
  // ----------
  public void initDialogView() {
    dialog = new JDialog(this, "Loading", false);
    dialog.setLocationRelativeTo(this);
    dialog.setLocation(510, 370);  // mysterious values
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

    // default
    dialogLabel.setText("<html>[Synopsis]  &nbsp;No<br>[Metafile]  &nbsp;No<br>[Audio]  &nbsp;No<br>[Frame]  &nbsp;0 / 0<br>[Image]  &nbsp;0 / 0<br></html>");

    dialog.setVisible(true);
  }

  public void initDisplayView(int numVideoButton) {
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
    addComponentsToControlPanel(controlPanel, numVideoButton);
    // controlPanel.setBackground(Color.YELLOW);

    // Synopsis Panel
    // --------------
    Dimension synopsisDimension = new Dimension(Constants.SYNOPSIS_WIDTH, Constants.SYNOPSIS_HEIGHT_WITH_BAR);
    JScrollPane synopsisPanel = new JScrollPane();
    synopsisLabel = new SynopsisLabel();

    synopsisPanel.add(synopsisLabel);
    synopsisPanel.setViewportView(synopsisLabel);
    JScrollBar bar = synopsisPanel.getHorizontalScrollBar();
    bar.setUnitIncrement(20);

    synopsisPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

    synopsisPanel.setPreferredSize(synopsisDimension);
    synopsisPanel.setMinimumSize(synopsisDimension);
    synopsisPanel.setMaximumSize(synopsisDimension);

    // synopsisPanel.setBackground(Color.ORANGE);

    // Add
    mainPanel.add(displayPanel, BorderLayout.NORTH);
    mainPanel.add(controlPanel, BorderLayout.CENTER);
    mainPanel.add(synopsisPanel, BorderLayout.SOUTH);

    setContentPane(mainPanel);

    pack();
    setVisible(true);
  }

  // initResources
  // -------------
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

  // addComponentsToControlPanel
  // ---------------------------
  private void addComponentsToControlPanel(JPanel controlPanel, int numVideoButton) {
    Dimension buttonDimension = new Dimension(Constants.BUTTON_SIZE, Constants.BUTTON_SIZE);

    videoButtonList = new ArrayList<>();

    // video button
    for (int i = 0; i < numVideoButton; ++i) {
      JButton videoButton = new JButton();
      videoButton.setActionCommand("VideoButton" + (i + 1));
      videoButton.setText("" + (i + 1));
      videoButton.setPreferredSize(buttonDimension);
      videoButton.setSelected(false);
      controlPanel.add(videoButton);
      videoButtonList.add(videoButton);
    }
    videoButtonList.get(0).setSelected(true);

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
    frameLabel = new JLabel("1 / 1");
    frameLabel.setUI(new BasicLabelUI());
    frameLabel.setPreferredSize(frameLabelDimension);
    frameLabel.setHorizontalAlignment(JLabel.CENTER);

    // progress bar
    Dimension barDimension = new Dimension(Constants.PROGRESSBAR_WIDTH - Constants.BUTTON_SIZE * numVideoButton, Constants.PROGRESSBAR_HEIGHT);
    progressBar = new JProgressBar();
    progressBar.setOpaque(true);
    progressBar.setUI(new ProgressUI(progressBar, Constants.PROGRESSBAR_COLOR, Color.LIGHT_GRAY));
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


  // initListener
  // ------------
  public void initListener(BrowserController controller) {
    // Button
    for (JButton b : videoButtonList) {
      // b.addActionListener(controller);
      b.addMouseListener(controller);
    }
    playButton.addActionListener(controller);
    stopButton.addActionListener(controller);
    // ProgressBar
    progressBar.addMouseListener(controller);
    progressBar.addMouseMotionListener(controller);
    // Slider
    soundSlider.addChangeListener(controller);
    // Synopsis Label
    synopsisLabel.addMouseListener(controller);
    synopsisLabel.addMouseMotionListener(controller);
  }


  // showImg
  // -------
  public void showImg(BufferedImage img) {
    float ratio = (float) img.getWidth() / (float) img.getHeight();
    int width = (int) (ratio * Constants.DISPLAY_HEIGHT);

    displayLabel.setIcon(new ImageIcon(img.getScaledInstance(width, Constants.DISPLAY_HEIGHT, Image.SCALE_SMOOTH)));
  }


  // setFrameLabelValues
  // -------------------
  public void setFrameLabelValues(int current, int total) {
    frameLabel.setText(current + " / " + total);
  }


  // set button state
  // ------------------
  public void setPlayButtonState(int state) {
    if (state == 0) {
      playButton.setIcon(new ImageIcon(playImage));
    } else {
      playButton.setIcon(new ImageIcon(pauseImage));
    }
  }

  public void setVideoButtonSelected(int videoID) {
    for (JButton b : videoButtonList) {
      b.setSelected(false);
    }
    videoButtonList.get(videoID - 1).setSelected(true);
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


  // Synopsis Image
  // --------------
  public void setSynopsisImage(BufferedImage img) {
    synopsisLabel.setIcon(new ImageIcon(img));
    synopsisLabel.setPreferredSize(new Dimension(img.getWidth(), Constants.SYNOPSIS_HEIGHT));
  }

  public void setSynopsisLabelMetadata(MetaData data) {
    synopsisLabel.setMetaData(data);
  }

  public void setSynopsisLabelCurrentSelectedIndex(int index) {
    synopsisLabel.setCurrentSelectedIndex(index);
  }

  public int getSynopsisLabelCurrentSelectedIndex() {
    return synopsisLabel.currentSelectedIndex;
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
