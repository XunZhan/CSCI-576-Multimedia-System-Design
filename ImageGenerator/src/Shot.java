import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * @author Junhao Wang
 * @date 04/30/2020
 */

public class Shot {
  public static void main(String[] args) {

  }

  public BufferedImage toGray(BufferedImage srcImg){
    return new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(srcImg, null);
  }
}
