import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableScheduledFuture;

import javax.imageio.ImageIO;

public class Main {

  private final static int WIDTH = 352;
  private final static int HEIGHT = 288;

  private static int width;
  private static int height;

  public static void main(String[] args) {
    List<String> fileArray = new ArrayList<>();
    // frame
    fileArray.add("video/image-0030.rgb");
    fileArray.add("video/image-0092.rgb");
    fileArray.add("video/image-0104.rgb");
    fileArray.add("video/image-0152.rgb");
    fileArray.add("video/image-0194.rgb");
    fileArray.add("video/image-0220.rgb");
    fileArray.add("video/image-0247.rgb");
    fileArray.add("video/image-0262.rgb");
    fileArray.add("video/image-0314.rgb");
    fileArray.add("video/image-0362.rgb");
    // image
    fileArray.add("image/image-0003.rgb");
    fileArray.add("image/image-0058.rgb");
    fileArray.add("image/image-0073.rgb");
    fileArray.add("image/image-0091.rgb");
    fileArray.add("image/image-0177.rgb");
    fileArray.add("image/image-0242.rgb");
    fileArray.add("image/image-0320.rgb");

    width = WIDTH * fileArray.size();
    height = HEIGHT;

    int[][][] imgRGB = new int[width][height][3];
    for (int i = 0; i < fileArray.size(); ++i) {
      read(fileArray.get(i), imgRGB, i);
    }

    int[][][] outputRGB = new int[width][height][3];
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        outputRGB[i][j][0] = imgRGB[i][j][0];
        outputRGB[i][j][1] = imgRGB[i][j][1];
        outputRGB[i][j][2] = imgRGB[i][j][2];
      }
    }

    blur(imgRGB, outputRGB);

    // generate buffered image
    BufferedImage img = generateImg(outputRGB);
    try {
      File outputFile = new File("synopsis.jpg");
      ImageIO.write(img, "jpg", outputFile);
    } catch (IOException e) {
      System.out.println("hihi");
    }
  }




  public static void read(String fileName, int[][][] imgRGB, int index) {

    File file = new File(fileName);

    try {
      readImage(file, imgRGB, index);
    } catch (Exception e) {
      System.out.println("[ImageReader] Something is wrong here.");
    }
  }

  private static void readImage(File file, int[][][] imgRGB, int index) throws Exception {
    try {
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      raf.seek(0);

      int frameLen = (int) (WIDTH * HEIGHT * 3); // rgb
      byte[] bytes = new byte[frameLen];
      raf.read(bytes);

      int idx = 0;
      int numPixel = WIDTH * HEIGHT;
      for (int y = 0; y < HEIGHT; ++y) {
        for (int x = 0; x < WIDTH; ++x) {
          int r = 0xFF & bytes[idx]; // 0xFF is important here
          int g = 0xFF & bytes[idx + numPixel]; // height * width = #pixel
          int b = 0xFF & bytes[idx + numPixel * 2];

          imgRGB[x + index * WIDTH][y][0] = r;
          imgRGB[x + index * WIDTH][y][1] = g;
          imgRGB[x + index * WIDTH][y][2] = b;

          // set RGB value in a byte
          // int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
          ++idx;
        }
      }
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException();
    } catch (Exception e) {
      throw new IOException();
    }
  }

  static List<int[]> dir = new ArrayList<>();

  private static void blur(int[][][] imgRGB, int[][][] outputRGB) {
    int lo = -3;
    int hi = +3;
    int size = 5;

    for (int i = lo; i <= hi; ++i) {
      for (int j = lo; j <= hi; ++j) {
        dir.add(new int[] { i, j });
      }
    }

    for (int x = 0; x < width; ++x) {
      for (int y = 0; y < height; ++y) {
        if (x != 0 && x != width - 1) {
          if (x % WIDTH == 0) {
            int leftX = x - size;
            int rightX = x + size - 1;
            for (int i = leftX; i <= rightX; ++i) {
              for (int j = 0; j <= height - 1; ++j) {
                int total = 0;
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;
                for (int[] d : dir) {
                  int ii = i + d[0];
                  int jj = j + d[1];
                  // check jj
                  if (jj >= 0 && jj <= height - 1) {
                    total += 1;
                    sumR += imgRGB[ii][jj][0];
                    sumG += imgRGB[ii][jj][1];
                    sumB += imgRGB[ii][jj][2];
                  }
                }
                // output
                outputRGB[i][j][0] = sumR / total;
                outputRGB[i][j][1] = sumG / total;
                outputRGB[i][j][2] = sumB / total;
              }
            }
          }
        }
      }
    }
  }

  private static BufferedImage generateImg(int[][][] rgbImg) {
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
}
