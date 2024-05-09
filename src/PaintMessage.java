import javafx.scene.paint.Color;
import java.awt.*;
import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.List;

public class PaintMessage implements Serializable {
    private int id; // 操作者的 id
    private String shape; // 形状类型
    private List<Point2D> path; // 路径信息
    private String color; // 颜色信息

    public PaintMessage(int id, String shape, List<Point2D> path, Color color) {
        this.id = id;
        this.shape = shape;
        this.path = path;
        this.color = color.toString();
    }

    // 添加 getter 和 setter 方法
    // 注意：这里需要手动添加

    @Override
    public String toString() {
        return "PaintMessage{" +
                "id=" + id +
                ", shape='" + shape + '\'' +
                ", path=" + path +
                ", color='" + color + '\'' +
                '}';
    }
}
