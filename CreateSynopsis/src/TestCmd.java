import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestCmd {
  public static void main(String[] args) {
    String cmdStr = "ifconfig";
    Runtime run = Runtime.getRuntime();
    try {
      Process process = run.exec(cmdStr);
      InputStream in = process.getInputStream();
      InputStreamReader reader = new InputStreamReader(in);
      BufferedReader br = new BufferedReader(reader);
      StringBuffer sb = new StringBuffer();
      String message;
      while((message = br.readLine()) != null) {
        sb.append(message);
      }
      System.out.println(sb);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}