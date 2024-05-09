import java.io.Serializable;

public class MyPoint2D implements Serializable {
    private double x;
    private double y;

    public MyPoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getter 和 Setter 方法
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
