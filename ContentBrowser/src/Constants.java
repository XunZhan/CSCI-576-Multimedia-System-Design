/**
 * @author Junhao Wang
 * @date 04/26/2020
 */

public class Constants {
  public final static int WINDOW_WIDTH = 500;
  public final static int WINDOW_HEIGHT = 450;
  public final static int IMAGE_WIDTH = 352;
  public final static int IMAGE_HEIGHT = 288;

  public final static String VIDEO_DIR = "/video";
  public final static String AUDIO_FILE_PATH = "/audio/sound.wav";
  public final static String FRAME_FILE_PREFIX = "image-";

  public final static double FPS = 29.94;
  public final static int MILLISECOND_INTERVAL = (int) ((double) 1 / FPS * 1000.0);  // miliseconds
  public final static int NANOSECOND_INTERVAL = (int) (((double) 1 / FPS * 1000.0 - 33.0) * 1000 * 1000);  // nanoseconds
}
