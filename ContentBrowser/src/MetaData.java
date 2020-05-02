import java.util.List;

public class MetaData {

  private int numVideo;

  private int synopsisWidth;
  private int synopsisHeight;
  private int synopsisSpan;

  private List<Item> itemList;
  private List<String> imageFileNameList;

  public MetaData(int w, int h, int s, int num, List<Item> itemList, List<String> fileNameList) {
    synopsisWidth = w;
    synopsisHeight = h;
    synopsisSpan = s;
    numVideo = num;
    this.itemList = itemList;
    this.imageFileNameList = fileNameList;
  }

  public int getSynopsisWidth() {
    return synopsisWidth;
  }

  public int getSynopsisHeight() {
    return synopsisHeight;
  }

  public int getSynopsisSpan() {
    return synopsisSpan;
  }

  public int getNumVideo() {
    return numVideo;
  }

  public List<Item> getItemList() {
    return itemList;
  }

  public List<String> getImageFileNameList() {
    return imageFileNameList;
  }
}
