enum ItemType {
  FRAME, IMAGE
}

public interface Item {
  public ItemType getType();
  public int getIndex();
}
