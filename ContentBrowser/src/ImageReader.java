import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageReader {

  public ImageReader() {

  }

  public BufferedImage read(String fileName, int width, int height) {
    // check fileName
    if (fileName == null) {
      System.out.println("[ImageReader] The file path is null.");
      return null;
    }

    File file = new File(fileName);
    if (file.exists() == false) {
      System.out.println("[ImageReader] The image does not exist.");
      return null;
    }

    int[][][] imgRGB = new int[width][height][3];

    try {
      readImage(file, imgRGB, width, height);
    } catch (Exception e) {
      System.out.println("[ImageReader] Something is wrong here.");
      return null;
    }

    return generateImg(imgRGB, width, height);
  }

  private void readImage(File file, int[][][] imgRGB, int width, int height) throws Exception {
    try {
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      raf.seek(0);

      int frameLen = (int) (width * height * 3); // rgb
      byte[] bytes = new byte[frameLen];
      raf.read(bytes);

      int idx = 0;
      int numPixel = width * height;
      for (int y = 0; y < height; ++y) {
        for (int x = 0; x < width; ++x) {
          int r = 0xFF & bytes[idx]; // 0xFF is important here
          int g = 0xFF & bytes[idx + numPixel]; // height * width = #pixel
          int b = 0xFF & bytes[idx + numPixel * 2];

          imgRGB[x][y][0] = r;
          imgRGB[x][y][1] = g;
          imgRGB[x][y][2] = b;

          ++idx;
        }
      }
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException();
    } catch (Exception e) {
      throw new IOException();
    }
  }

  private BufferedImage generateImg(int[][][] rgbImg, int width, int height) {
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
