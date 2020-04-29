
public class ImageItem implements Item {

  private int index;

  public ImageItem(int i) {
    index = i;
  }

  public ItemType getType() {
    return ItemType.IMAGE;
  }
  
  public int getIndex() {
    return index;
  }
}
