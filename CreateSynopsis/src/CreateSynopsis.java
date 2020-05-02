import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class CreateSynopsis {

  public static void main(String[] args) {
    String videoDirStr = Constants.TESTDATA_DIR + Constants.VIDEO_DIR;
    System.out.println(videoDirStr);
    File videoFile = new File(videoDirStr);

    String[] fileNameArr = videoFile.list();
    Arrays.sort(fileNameArr);

    ImageReader reader = new ImageReader();

    List<int[][]> grayList = new ArrayList();

    for (String fileNameStr : fileNameArr) {
      // only for rgb file
      if (fileNameStr.endsWith("rgb") == false) {
        continue;
      }

      String s = videoDirStr + "/" + fileNameStr;
      int[][][] imgRGB = new int[Constants.IMAGE_WIDTH][Constants.IMAGE_HEIGHT][3];
      reader.readRGB(s, imgRGB, Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);

      // convert to gray or Y in YUV
      int[][] imgGRAY = new int[Constants.IMAGE_WIDTH][Constants.IMAGE_HEIGHT];
      for (int x = 0; x < Constants.IMAGE_WIDTH; ++x) {
        for (int y = 0; y < Constants.IMAGE_HEIGHT; ++y) {
          int r = imgRGB[x][y][0];
          int g = imgRGB[x][y][1];
          int b = imgRGB[x][y][2];
          int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
          imgGRAY[x][y] = gray;
        }
      }

      // add to list
      grayList.add(imgGRAY);
    }

    System.out.println("okay");

    // compare

    // gray
    compareGray(grayList);

    // motion
    // compareMotionError(grayList);
  }

  private static void compareMotionError(List<int[][]> grayList) {
    // int numPixel = Constants.IMAGE_WIDTH * Constants.IMAGE_HEIGHT;
    for (int i = 0; i < grayList.size() - 1; ++i) {
      int[][] curr = grayList.get(i);
      int[][] next = grayList.get(i + 1);
      float e = calculate(curr, next);
      System.out.println("[ " + (i + 1) + " " + (i + 2) + " ] Error: " + e);
      if (e > 1000) {
        System.out.println("SHOT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
    }
  }


  private static int blockSize = 16;
  private static int k = 16;  // searh area

  private static float calculate(int[][] curr, int[][] next) {
    // for each macroblock in next
    float totalError = 0;
    // for each microblock
    int numBlock = 0;
    for (int x = 0; x < Constants.IMAGE_WIDTH; x += blockSize) {
      for (int y = 0; y < Constants.IMAGE_HEIGHT; y += blockSize) {
        numBlock += 1;
        // current block (x, y)
        int searchStartX = x - k;
        int searchStartY = y - k;
        int searchEndX = x + k;
        int searchEndY = y + k;
        // for each block in search area find the block with smallest error
        float minError = Float.MAX_VALUE;
        for (int i = searchStartX; i <= searchEndX; ++i) {
          for (int j = searchStartY; j <= searchEndY; ++j) {
            // candidate block (i, j)
            // check boundary
            if (i < 0 || (i + blockSize) >= Constants.IMAGE_WIDTH || j < 0 || (j + blockSize) >= Constants.IMAGE_HEIGHT) {
              continue;
            }
            // search candidates in curr
            float error = blockError(curr, i, j, next, x, y);
            if (error < minError) {
              minError = error;
            }
          }
        }
        totalError += minError;
      }
    }
    return totalError / (float) (numBlock);
  }

  private static float blockError(int[][] curr, int xc, int yc, int[][] next, int xn, int yn) {
    // candidate in curr
    int sum = 0;
    for (int i = 0; i < blockSize; ++i) {
      for (int j = 0; j < blockSize; ++j) {
        int diff = curr[xc + i][yc + j] - next[xn + i][yn + j];
        sum += diff * diff;
      }
    }
    return (float) sum / (float) (blockSize * blockSize);
  }


  // gray
  // ----
  private static void compareGray(List<int[][]> grayList) {
    int numPixel = Constants.IMAGE_WIDTH * Constants.IMAGE_HEIGHT;
    // int T = 0;
    int T = 10;
    for (int i = 0; i < grayList.size() - 1; ++i) {
      int[][] curr = grayList.get(i);
      int[][] next = grayList.get(i + 1);
      int diffSum = 0;
      for (int x = 0; x < Constants.IMAGE_WIDTH; ++x) {
        for (int y = 0; y < Constants.IMAGE_HEIGHT; ++y) {
          int currVal = curr[x][y];
          int nextVal = next[x][y];
          if (Math.abs(currVal - nextVal) > T) {
            diffSum += 1;
          }
          diffSum += Math.abs(currVal - nextVal);
        }
      }
      float diff = diffSum / (float) (numPixel);
      System.out.println("[ " + (i + 1) + " " + (i + 2) + " ] " + diff);
      if (diff > 40) {
        System.out.println("SHOT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
    }
  }



  private static void show(BufferedImage img) {
    System.out.println("[Showing] ...");

    JFrame frame = new JFrame();
    // frame.setSize(1080, 1080); // fixed size
    GridBagLayout gLayout = new GridBagLayout();
    frame.getContentPane().setLayout(gLayout);

    JLabel label = new JLabel();
    frame.getContentPane().add(label);

    // frame.pack();
    frame.setVisible(true);
    label.setIcon(new ImageIcon(img));
    frame.pack();
  }


  public static BufferedImage generateGrayImg(int[][] grayImg, int width, int height) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < Constants.IMAGE_WIDTH; ++x) {
      for (int y = 0; y < Constants.IMAGE_HEIGHT; ++y) {
        int r = grayImg[x][y];
        int g = grayImg[x][y];
        int b = grayImg[x][y];
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        img.setRGB(x, y, pix);
      }
    }

    return img;
  }
}
