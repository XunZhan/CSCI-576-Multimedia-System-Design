import java.awt.Color;

public class Constants {
  // basic info
  // ----------
  public final static String ROOT_DIR = "../";
  // public final static String TESTDATA_DIR = "TestData";
  public final static String TESTDATA_DIR = "TestDataDay1";
  // public final static String TESTDATA_DIR = "TestDataDay2";
  public final static String VIDEO_DIR = "/video";
  public final static String IMAGE_DIR = "/image";
  public final static String AUDIO_FILE_NAME = "/audio.wav";
  public final static String SYNOPSIS_FILE = "/synopsis.rgb";
  public final static String META_FILE = "/synopsis.metafile";

  public final static int BROWSING_OFFSET = 100;
  public final static double FPS = 29.97;
  public final static long FPS_NANOSECOND_INTERVAL = 33_366_700;
  public final static int FPS_MILLISECOND_PART = (int) ((double) 1 / FPS * 1000.0);  // miliseconds
  public final static int FPS_NANOSECOND_PART = (int) (((double) 1 / FPS * 1000.0 - 33.0) * 1000 * 1000);  // nanoseconds

  // UI stuff
  // --------

  // window
  public final static int WINDOW_WIDTH = 900;
  public final static int WINDOW_HEIGHT = 568;

  // display
  public final static int DISPLAY_WIDTH = WINDOW_WIDTH;
  public final static int DISPLAY_HEIGHT = 380;

  // synopsis
  public final static int SYNOPSIS_WIDTH = WINDOW_WIDTH;
  public final static int SYNOPSIS_HEIGHT = 100;
  public final static int SYNOPSIS_HEIGHT_WITH_BAR = 120;
  public final static int SYNOPSIS_TEXT_OFFSET_X = 16;
  public final static int SYNOPSIS_TEXT_OFFSET_Y = 8;
  public final static int SYNOPSIS_TEXT_SIZE = 12;

  public final static int SYNOPSIS_RECT_OFFSET = 20;
  public final static int SYNOPSIS_RECT_SIZE = 15;
  public final static int SYNOPSIS_RECT_CORNER = 7;

  public final static Color SYNOPSIS_FRAME_RECT_COLOR = new Color(52, 143, 149);
  public final static Color SYNOPSIS_IMAGE_RECT_COLOR = new Color(73, 123, 55);


  public final static int SYNOPSIS_SELECTED_RECT_THICKNESS = 3;
  public final static int SYNOPSIS_SELECTED_RECT_CORNER = 10;

  // control panel
  public final static int CONTROL_WIDTH = WINDOW_WIDTH;
  public final static int CONTROL_HEIGHT = WINDOW_HEIGHT - DISPLAY_HEIGHT - SYNOPSIS_HEIGHT;

  // image
  public final static int IMAGE_WIDTH = 352;
  public final static int IMAGE_HEIGHT = 288;

  // button
  public final static int BUTTON_SIZE = 27;
  public final static int BUTTON_ICON_SIZE = BUTTON_SIZE - 10;

  // frame label
  public final static int FRAME_LABEL_WIDTH = 90;
  public final static int FRAME_LABEL_HEIGHT = 20;

  // progress bar
  public final static int PROGRESSBAR_WIDTH = 530;
  public final static int PROGRESSBAR_HEIGHT = 10;
  public final static Color PROGRESSBAR_COLOR = new Color(236, 162, 42);

  // sound slider
  public final static int SLIDER_WIDTH = 130;
  public final static int SLIDER_HEIGHT = 20;

  // dialog
  public final static int DIALOG_WIDTH = 300;
  public final static int DIALOG_HEIGHT = 150;
  public final static int DIALOG_ICON_SIZE = 80;
}
