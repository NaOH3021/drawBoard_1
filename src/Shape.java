import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;

abstract class Shape {
    protected GraphicsContext gc;
    protected double startX, startY;
    protected double endX, endY;
    protected Color color;

    public Shape(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setColor(Color color) {
        this.color = color;
        gc.setStroke(color); // 设置形状的描边颜色
        gc.setFill(color);   // 设置形状的填充颜色
    }

    public abstract void onMousePressed(MouseEvent event);

    public abstract void onMouseDragged(MouseEvent event);

    public abstract void onMouseReleased(MouseEvent event);
}

class Line extends Shape {
    public Line(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        gc.strokeLine(startX, startY, endX, endY);
    }
}

class Circle extends Shape {
    public Circle(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        // 计算椭圆中心（即线段中点）
        double centerX = (startX + endX) / 2.0;
        double centerY = (startY + endY) / 2.0;

        // 计算直径的一半，即椭圆的宽和高（这里假设椭圆是圆形，所以宽度和高度相等）
        double diameterHalf = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2.0;

        // 使用椭圆的中心坐标和直径的一半来绘制椭圆（这里实际上是圆形）
        gc.strokeOval(centerX - diameterHalf, centerY - diameterHalf, diameterHalf * 2, diameterHalf * 2);
    }
}



class Rectangle extends Shape {
    public Rectangle(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        startX = event.getX();
        startY = event.getY();
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        endX = event.getX();
        endY = event.getY();
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY),
                Math.abs(endX - startX), Math.abs(endY - startY));
    }
}

class Eraser extends Shape {
    public Eraser(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        gc.clearRect(x - 10, y - 10, 40, 40);
        gc.setFill(Color.WHITE); // 设置背景色为白色
        gc.fillRect(x - 10, y - 10, 40, 40);
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        gc.clearRect(x - 10, y - 10, 40, 40);
        gc.setFill(Color.WHITE); // 设置背景色为白色
        gc.fillRect(x - 10, y - 10, 40, 40);
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        // No additional actions needed for eraser
    }
}

class Brush extends Shape {
    private double size;
    private Color color;
    private double previousSize; // 记录之前笔的宽度

    public Brush(GraphicsContext gc) {
        super(gc);
        this.size = 1.0; // 默认大小为1.0
        this.color = Color.BLACK; // 默认颜色为黑色
        this.previousSize = 1.0; // 默认之前笔的宽度为1.0
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void onMousePressed(MouseEvent event) {
        previousSize = gc.getLineWidth(); // 记录之前笔的宽度
        gc.beginPath();
        gc.lineTo(event.getX(), event.getY());
        gc.setStroke(color);
        gc.setLineWidth(size);
        gc.stroke();
    }

    @Override
    public void onMouseDragged(MouseEvent event) {
        gc.lineTo(event.getX(), event.getY());
        gc.setStroke(color);
        gc.setLineWidth(size);
        gc.stroke();
    }

    @Override
    public void onMouseReleased(MouseEvent event) {
        gc.setLineWidth(previousSize); // 在释放鼠标时恢复之前笔的宽度
    }
}