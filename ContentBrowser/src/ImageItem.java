
public class ImageItem implements Item {

  private int index;

  public ImageItem(int i) {
    index = i;
  }

  @Override
  public ItemType getType() {
    return ItemType.IMAGE;
  }

  @Override
  public int getIndex() {
    return index;
  }
}
