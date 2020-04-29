public class FrameItem implements Item {
  private int index;
  public ItemType getType() {
    return ItemType.FRAME;
  }
  
  public int getIndex() {
    return index;
  }
}
