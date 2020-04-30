
public class FrameItem implements Item {

  private int index;
  private int shotStart;
  private int shotEnd;

  public FrameItem(int i, int s, int e) {
    index = i;
    shotStart = s;
    shotEnd = e;
  }

  @Override
  public ItemType getType() {
    return ItemType.FRAME;
  }

  @Override
  public int getIndex() {
    return index;
  }

  public int getShotStart() {
    return shotStart;
  }

  public int getShotEnd() {
    return shotEnd;
  }
}
