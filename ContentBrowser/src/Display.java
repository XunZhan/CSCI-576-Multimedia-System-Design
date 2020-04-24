import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.*;
import java.nio.Buffer;
import java.time.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class Display {

    JFrame frame;
    JLabel lbOrg;
    JButton btPlay;
    JButton btStop;
    BufferedImage buff_img;
    byte[][] color;
    int width = 352;
    int height = 288;

    /** Read Image RGB
     *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
     */
    private void readImageRGB(int width, int height, String imgPath, BufferedImage img)
    {
        try
        {
            int frameLength = width*height*3;

            File file = new File(imgPath);
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(0);

            long len = frameLength;
            byte[] bytes = new byte[(int) len];
            color = new byte[width*height][3];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    //int pix = ((a << 24) + (r << 16) + (g << 8) + b);

                    img.setRGB(x,y,pix);
//                    color[x*width + y][0] = r;
//                    color[x*width + y][1] = g;
//                    color[x*width + y][2] = b;
                    ind++;
                }
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void addComponent(Container pane)
    {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));

        // Image or Video Display Area
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.LINE_AXIS));
        lbOrg = new JLabel();
        GridBagConstraints o = new GridBagConstraints();
        o.fill = GridBagConstraints.HORIZONTAL;
        o.anchor = GridBagConstraints.CENTER;
//        o.weightx = 0.5;
//        o.gridx = 0;
//        o.gridy = 0;

        displayPanel.add(lbOrg, o);
        listPanel.add(displayPanel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout((new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS)));
        btPlay = new JButton("Play");
        GridBagConstraints bp = new GridBagConstraints();
        bp.fill = GridBagConstraints.HORIZONTAL;
        bp.anchor = GridBagConstraints.CENTER;
//        bp.weightx = 0.5;
//        bp.gridx = 0;
//        bp.gridy = 1;

        btStop = new JButton("Stop");
        GridBagConstraints bs = new GridBagConstraints();
        bs.fill = GridBagConstraints.HORIZONTAL;
        bs.anchor = GridBagConstraints.CENTER;
//        bs.weightx = 0.5;
//        bs.gridx = 1;
//        bs.gridy = 1;

        buttonPanel.add(btPlay, bp);
        buttonPanel.add(btStop, bs);

        // Synopsis Image
//        JPanel SynopsisPanel = new JPanel();
//        SynopsisPanel.setLayout(new BoxLayout(SynopsisPanel, BoxLayout.LINE_AXIS));
//        lbOrg = new JLabel();
//        GridBagConstraints sc = new GridBagConstraints();
//        sc.fill = GridBagConstraints.HORIZONTAL;
//        sc.anchor = GridBagConstraints.CENTER;

        listPanel.add(buttonPanel);
        pane.add(listPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private void addListener()
    {
        lbOrg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked: " +  e.getX() + " " + e.getY());

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

        btPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play Button Clicked");
            }
        });

        btStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Stop Button Clicked");
            }
        });
    }

    public void buildUI()
    {
        // Use label to display the image
        frame = new JFrame();
        GridBagLayout gLayout = new GridBagLayout();
        frame.getContentPane().setLayout(gLayout);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 450));
        addComponent(frame.getContentPane());

        addListener();

    }


    public void showIms(String[] args){

        // Read a parameter from command line
        //String param1 = args[1];
        //System.out.println("The second parameter was: " + param1);

        // Read in the specified image
        buff_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //readImageRGB(width, height, args[0], buff_img);
        readImageRGB(width, height, "/Users/xun/Downloads/CSCI576ProjectMedia/Image/RGB/image-0003.rgb", buff_img);

        lbOrg.setIcon(new ImageIcon(buff_img));

    }

    public static void main(String[] args) {
        Display ren = new Display();
        ren.buildUI();
        ren.showIms(args);
    }

}
