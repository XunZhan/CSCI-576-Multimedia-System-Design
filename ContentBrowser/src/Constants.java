/**
 * @author Junhao Wang
 * @date 04/26/2020
 */

public class Constants {
  public final static int WINDOW_WIDTH = 900;
  public final static int WINDOW_HEIGHT = 548;

  public final static int DISPLAY_WIDTH = WINDOW_WIDTH;
  public final static int DISPLAY_HEIGHT = 380;

  public final static int SYNOPSIS_WIDTH = WINDOW_WIDTH;
  public final static int SYNOPSIS_HEIGHT = 100;

  public final static int CONTROL_WIDTH = WINDOW_WIDTH;
  public final static int CONTROL_HEIGHT = WINDOW_HEIGHT - DISPLAY_HEIGHT - SYNOPSIS_HEIGHT;

  public final static int IMAGE_WIDTH = 352;
  public final static int IMAGE_HEIGHT = 288;

  public final static int BUTTON_SIZE = 27;
  public final static int BUTTON_ICON_SIZE = BUTTON_SIZE - 10;

  public final static int FRAME_LABEL_WIDTH = 70;
  public final static int FRAME_LABEL_HEIGHT = 20;

  public final static int PROGRESSBAR_WIDTH = 550;
  public final static int PROGRESSBAR_HEIGHT = 10;

  public final static int SLIDER_WIDTH = 130;
  public final static int SLIDER_HEIGHT = 20;

  public final static int DIALOG_WIDTH = 300;
  public final static int DIALOG_HEIGHT = 150;
  public final static int DIALOG_ICON_SIZE = 80;

  public final static String VIDEO_DIR = "/video";
  public final static String IMAGE_DIR = "/image";
  public final static String AUDIO_FILE_PATH = "/audio/sound.wav";
  public final static String SYNOPSIS_FILE = "/synopsis.jpg";
  public final static String META_FILE = "/synopsis.metafile";
  public final static String FRAME_FILE_PREFIX = "image-";

  public final static double FPS = 29.94;
  public final static int MILLISECOND_INTERVAL = (int) ((double) 1 / FPS * 1000.0);  // miliseconds
  public final static int NANOSECOND_INTERVAL = (int) (((double) 1 / FPS * 1000.0 - 33.0) * 1000 * 1000);  // nanoseconds
}
