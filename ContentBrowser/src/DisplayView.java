import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DisplayView {

  public JFrame frame;
  public JLabel label;
  public JButton playButton;
  public JButton stopButton;

  // constructor
  public DisplayView() {
    frame = new JFrame();
    GridBagLayout gLayout = new GridBagLayout();
    frame.getContentPane().setLayout(gLayout);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
    addComponents(frame.getContentPane());
  }

  public void initListener(BrowserController controller) {
    playButton.addActionListener(controller);
    stopButton.addActionListener(controller);
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

    playButton = new JButton("Play");
    playButton.setActionCommand("PlayButton");
    GridBagConstraints bp = new GridBagConstraints();
    bp.fill = GridBagConstraints.HORIZONTAL;
    bp.anchor = GridBagConstraints.CENTER;
//        bp.weightx = 0.5;
//        bp.gridx = 0;
//        bp.gridy = 1;

    stopButton = new JButton("Stop");
    stopButton.setActionCommand("StopButton");
    GridBagConstraints bs = new GridBagConstraints();
    bs.fill = GridBagConstraints.HORIZONTAL;
    bs.anchor = GridBagConstraints.CENTER;
//        bs.weightx = 0.5;
//        bs.gridx = 1;
//        bs.gridy = 1;

    buttonPanel.add(this.playButton, bp);
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


}
