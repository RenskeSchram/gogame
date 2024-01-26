package gogame;

public class Move {
    int[] intersectionLocation;
    Color color;

    public Move(int[] intersectionLocation) {
        this.intersectionLocation = intersectionLocation;
        this.color = Color.EMPTY;
    }

    public Move(int[] intersectionLocation, Color color) {
        this.intersectionLocation = intersectionLocation;
        this.color = color;
    }

    public static String intersectionLocationToString(int[] intersectionLocation) {
        return intersectionLocation[0] + "," + intersectionLocation[1];
    }

}
