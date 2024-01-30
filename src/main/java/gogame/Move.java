package gogame;

public class Move {

  protected int[] intersectionLocation;
  protected Color color;

  public Move(int[] intersectionLocation, Color color) {
    this.intersectionLocation = intersectionLocation;
    this.color = color;
  }

  public static String intersectionLocationToString(int[] intersectionLocation) {
    return intersectionLocation[0] + "," + intersectionLocation[1];
  }

}
