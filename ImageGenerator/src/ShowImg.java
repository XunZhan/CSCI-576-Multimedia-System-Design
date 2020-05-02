/**
 * Author: 576 Instructor & TAs, Junhao Wang
 * Date: 02/22/2020
 */

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ShowImg {

  // Member Variables
  private static final int width = 352;
  private static final int height = 288;

  // public static BufferedImage toGray(BufferedImage srcImg){
  //   return new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(srcImg, null);
  // }

  private static int colorToRGB(int alpha, int red, int green, int blue) {
    int newPixel = 0;
    newPixel += alpha;
    newPixel = newPixel << 8;
    newPixel += red;
    newPixel = newPixel << 8;
    newPixel += green;
    newPixel = newPixel << 8;
    newPixel += blue;
    return newPixel;
  }

  public static BufferedImage grayImage(BufferedImage bufferedImage) throws
          Exception {
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    BufferedImage grayBufferedImage = new BufferedImage(width, height,
            BufferedImage.TYPE_BYTE_GRAY);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        // 计算灰度值
        final int color = bufferedImage.getRGB(x, y);
        final int r = (color >> 16) & 0xff;
        final int g = (color >> 8) & 0xff;
        final int b = color & 0xff;
        int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
        int newPixel = colorToRGB(255, gray, gray, gray);
        grayBufferedImage.setRGB(x, y, newPixel);
      }
    }
    return grayBufferedImage;
  }

  // main
  public static void main(String[] args) {
    ShowImg display = new ShowImg(args);
  }

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
    try {
      img = grayImage(img);
    } catch (Exception e) {

    }

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
