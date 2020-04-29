
public class FrameItem implements Item {

  private int index;

  public FrameItem(int i) {
    index = i;
  }

  public ItemType getType() {
    return ItemType.FRAME;
  }

  public int getIndex() {
    return index;
  }
}
