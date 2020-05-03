import java.awt.image.BufferedImage;
import java.util.List;


public class Model {

  public List<List<BufferedImage>> frameList;
  public List<BufferedImage> imageList;
  public MetaData metaData;
  public BufferedImage synopsisImage;

  // constructor
  public Model(List<List<BufferedImage>> fList, List<BufferedImage> iList, MetaData md, BufferedImage img) {
    frameList = fList;
    imageList = iList;
    metaData = md;
    synopsisImage = img;
  }
}
