
public class FrameItem implements Item {

  private int videoID;  // 1-based
  private int index;
  private int shotStart;
  private int shotEnd;

  public FrameItem(int id, int i, int s, int e) {
    videoID = id;
    index = i;
    shotStart = s;
    shotEnd = e;
  }

  @Override
  public ItemType getType() {
    return ItemType.FRAME;
  }

  public int getVideoID() {
    return videoID;
  }

  @Override
  public int getIndex() {
    return index;
  }

  // public int getShotStart() {
  //   return shotStart;
  // }
  //
  // public int getShotEnd() {
  //   return shotEnd;
  // }
}
