/**
 * Author: 576 Instructor & TAs, Junhao Wang
 * Date: 02/22/2020
 */

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ShowImg {

  // main
  public static void main(String[] args) {
    ShowImg display = new ShowImg(args);
  }

  // Member Variables
  private static final int width = 352;
  private static final int height = 288;

  // Constructor
  public ShowImg(String[] args) {
    if (args.length != 1) {
      throw new IllegalArgumentException("You need to provide the image file!");
    }
    String fileName = args[0];

    // read image
    int[][][] inputRGB = new int[width][height][3];
    readImage(fileName, inputRGB);
    // generate images
    BufferedImage img = generateImg(inputRGB);
    // show image
    show(img, fileName);
  }

  private void readImage(String fileName, int[][][] imgRGB) {
    try {
      System.out.print("\n[Reading image] \"" + fileName + "\"");
      File file = new File(fileName);
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      raf.seek(0);

      int frameLen = (int) (width * height * 3); // rgb
      byte[] bytes = new byte[frameLen];
      raf.read(bytes);

      int idx = 0;
      for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
          int r = 0xFF & bytes[idx]; // 0xFF is important here
          int g = 0xFF & bytes[idx + height * width]; // height * width = #pixel
          int b = 0xFF & bytes[idx + height * width * 2];

          imgRGB[x][y][0] = r;
          imgRGB[x][y][1] = g;
          imgRGB[x][y][2] = b;

          // set RGB value in a byte
          // int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
          ++idx;
        }
      }

      System.out.print("    ... Done\n");

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private BufferedImage generateImg(int[][][] rgbImg) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        int r = rgbImg[x][y][0];
        int g = rgbImg[x][y][1];
        int b = rgbImg[x][y][2];
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        img.setRGB(x, y, pix);
      }
    }

    return img;
  }

  private void show(BufferedImage img, String filename) {
    System.out.println("[Showing] ...");

    JFrame frame = new JFrame();
    frame.setTitle(filename);
    // frame.setSize(1080, 1080); // fixed size
    GridBagLayout gLayout = new GridBagLayout();
    frame.getContentPane().setLayout(gLayout);

    JLabel label = new JLabel();
    frame.getContentPane().add(label);

    // frame.pack();
    frame.setVisible(true);
    label.setIcon(new ImageIcon(img));
    frame.pack();

    System.out.println("[Done] :)");
  }

}
