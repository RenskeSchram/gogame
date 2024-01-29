package gogame;

import java.util.Random;

public class TestFunctions {

  public static int getRandomPort() {
    Random random = new Random();
    return random.nextInt(9999 - 1) + 1;
  }

}
