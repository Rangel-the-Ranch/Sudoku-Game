package types;
//This class is used to store the move made by the player
@SuppressWarnings("unused")
public class SudokuMove {
    int x;
    int y;
    int value;
    public SudokuMove(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getValue() {
        return value;
    }

}
