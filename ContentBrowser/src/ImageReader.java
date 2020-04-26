import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageReader
{
  private BufferedImage generateImg(int[][][] rgbImg)
  {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < width; ++x)
    {
      for (int y = 0; y < height; ++y)
      {
        int r = rgbImg[x][y][0];
        int g = rgbImg[x][y][1];
        int b = rgbImg[x][y][2];
        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
        img.setRGB(x, y, pix);
      }
    }

    return img;
  }

  private void readImage(String fileName, int[][][] imgRGB)
  {
    try
    {
      System.out.print("\n[Reading image] \"" + fileName + "\"");
      File file = new File(fileName);
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      raf.seek(0);

      int frameLen = (int) (width * height * 3); // rgb
      byte[] bytes = new byte[frameLen];
      raf.read(bytes);

      int idx = 0;
      for (int y = 0; y < height; ++y)
      {
        for (int x = 0; x < width; ++x)
        {
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

    } catch (FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
