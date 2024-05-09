import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.io.ByteArrayOutputStream;


public class SimplePaintApp extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private int id;
    private Canvas canvas;
    private GraphicsContext gc;
    private Color currentColor = Color.BLACK;
    private Shape currentShape; // 当前选中的形状
    private List<Point2D> path; // 记录路径曲线的坐标信息

    public SimplePaintApp(int id) {
        this.id = id;
        System.out.println("PaintApp " + id);
        //this.canvas = canvas;
        //this.gc = canvas.getGraphicsContext2D();
        path = new ArrayList<>(); // 初始化路径
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        path = new ArrayList<>(); // 初始化路径

        // Create control buttons
        Button lineButton = new Button("Line");
        Image lineIcon = new Image(getClass().getResourceAsStream("pic/line.png"));
        lineButton.setGraphic(new ImageView(lineIcon));
        lineButton.setOnAction(e -> setCurrentShape(new Line(gc)));

        Button circleButton = new Button("Circle");
        Image circleIcon = new Image(getClass().getResourceAsStream("pic/circle.png"));
        circleButton.setGraphic(new ImageView(circleIcon));
        circleButton.setOnAction(e -> setCurrentShape(new Circle(gc)));

        Button rectangleButton = new Button("Rectangle");
        Image rectangleIcon = new Image(getClass().getResourceAsStream("pic/rect.png"));
        rectangleButton.setGraphic(new ImageView(rectangleIcon));
        rectangleButton.setOnAction(e -> setCurrentShape(new Rectangle(gc)));

        Button eraseButton = new Button("Eraser");
        Image eraseIcon = new Image(getClass().getResourceAsStream("pic/eraser.png"));
        eraseButton.setGraphic(new ImageView(eraseIcon));
        eraseButton.setOnAction(e -> setCurrentShape(new Eraser(gc)));

        Button brushButton = new Button("Brush");
        Image brushIcon = new Image(getClass().getResourceAsStream("pic/pen.png"));
        brushButton.setGraphic(new ImageView(brushIcon));
        brushButton.setOnAction(e -> setCurrentShape(new Brush(gc)));

        Button colorButton = new Button("");
        Image colorIcon = new Image(getClass().getResourceAsStream("pic/color.png"));
        colorButton.setGraphic(new ImageView(colorIcon));
        colorButton.setOnAction(e -> selectColor());

        Button clearButton = new Button("Clear");
        Image clearIcon = new Image(getClass().getResourceAsStream("pic/clear.png"));
        clearButton.setGraphic(new ImageView(clearIcon));
        clearButton.setOnAction(e -> clearCanvas());

        // 在 start 方法中，创建 outputButton 并添加到 controlBox 中
        Button outputButton = new Button("Output");
        Image outputIcon = new Image(getClass().getResourceAsStream("pic/output.png"));
        outputButton.setGraphic(new ImageView(outputIcon));
        outputButton.setOnAction(e -> saveCanvasAsImage(primaryStage));
        outputButton.setPrefWidth(100);
        outputButton.setPrefHeight(40);


        // 在 SimplePaintApp 类中添加 saveCanvasAsImage 方法

        // 设置按钮的宽度和高度
        lineButton.setPrefWidth(100);
        lineButton.setPrefHeight(50);

        circleButton.setPrefWidth(100);
        circleButton.setPrefHeight(50);

        rectangleButton.setPrefWidth(100);
        rectangleButton.setPrefHeight(50);

        eraseButton.setPrefWidth(100);
        eraseButton.setPrefHeight(50);

        brushButton.setPrefWidth(100);
        brushButton.setPrefHeight(50);

        colorButton.setPrefWidth(100);
        colorButton.setPrefHeight(50);

        clearButton.setPrefWidth(100);
        clearButton.setPrefHeight(50);


        HBox controlBox = new HBox(10);
        controlBox.setPadding(new Insets(10));
        controlBox.getChildren().addAll(lineButton, circleButton, rectangleButton, eraseButton, brushButton, colorButton, clearButton, outputButton);

//        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
//            // Send paint message every INTERVAL milliseconds
//            //broadcastPaintMessage();
//        }));
//
//        timeline.setCycleCount(Timeline.INDEFINITE); // Repeat indefinitely
//        timeline.play(); // Start the animation

        root.setTop(controlBox);
        root.setCenter(canvas);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("pic/drawBoard.png")));

        // 默认选中线条工具
        lineButton.fire();

        // 设置鼠标事件
        canvas.setOnMousePressed(event -> {
            currentShape.onMousePressed(event);
            path.clear(); // 清空路径
            path.add(new Point2D(event.getX(), event.getY())); // 添加起始点坐标
            broadcastPaintMessage();
        });
        canvas.setOnMouseDragged(event -> {
            currentShape.onMouseDragged(event);
            path.add(new Point2D(event.getX(), event.getY())); // 添加拖动过程中的点坐标
            broadcastPaintMessage();
        });
        canvas.setOnMouseReleased(event -> {
            currentShape.onMouseReleased(event);
            path.add(new Point2D(event.getX(), event.getY())); // 添加释放鼠标时的点坐标
            broadcastPaintMessage();
            //broadcastPaintMessage(); // 广播绘画消息
            PaintMessage message = new PaintMessage(id, currentShape.getClass().getSimpleName(), path, currentColor);
            System.out.println(message.toString());
        });

        primaryStage.setScene(scene);
        // primaryStage.setTitle("Simple Paint App");
        primaryStage.setTitle("drawBoard");
        primaryStage.show();

        // Start listening for incoming data
        //startListening();
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.WHITE); // 设置背景色为白色
        gc.fillRect(0, 0, WIDTH, HEIGHT); // 填充整个画板为白色
    }


    private void setCurrentShape(Shape shape) {
        this.currentShape = shape;
        // 如果当前形状是刷子，确保其是 Brush 类的实例，然后设置大小和颜色
        if (currentShape instanceof Brush) {
            Brush brush = (Brush) currentShape;
            brush.setSize(5.0); // 设置笔触宽度为5.0
            brush.setColor(currentColor); // 设置颜色
        }
    }


    private void startListening() {
        Thread thread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(8888);
                byte[] buffer = new byte[65536];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);
                    ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                    WritableImage receivedImage = new WritableImage(WIDTH, HEIGHT);
                    ImageIO.read(bais); // Read the image from the stream
                    gc.drawImage(receivedImage, 0, 0); // Display the received image on the canvas
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }



    private void selectColor() {
        // 创建颜色选择器
        ColorPicker colorPicker = new ColorPicker();



        // 监听颜色选择器的值变化事件
        colorPicker.setOnAction(event -> {
            // 获取所选颜色，并设置为当前颜色
            currentColor = colorPicker.getValue();
            // 更新当前形状的颜色
            if (currentShape != null) {
                currentShape.setColor(currentColor);
            }
        });

        // 创建一个新的舞台用于显示颜色选择器
        Stage colorStage = new Stage();
        colorStage.setScene(new Scene(colorPicker));
        colorStage.setTitle("Select Color");
        colorStage.show();
    }

    public void saveCanvasAsImage(Stage primaryStage) {
        // 创建一个 WritableImage 对象，使用画板的内容来填充它
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(new SnapshotParameters(), writableImage);

        // 创建一个 FileChooser 对象，用于选择保存图片的目标文件路径和文件名
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));

        // 获取用户选择的文件路径和文件名
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                // 将 WritableImage 对象保存到选定的文件中
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void broadcastPaintMessage() {
        try {
            // Create DatagramSocket object and specify port number
            DatagramSocket socket = new DatagramSocket();
            // Get local IP address
            InetAddress address = InetAddress.getLocalHost();

            // Convert canvas content to image
            WritableImage writableImage = new WritableImage(WIDTH, HEIGHT);
            canvas.snapshot(null, writableImage);
            // Convert image to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // Create DatagramPacket object with data to send, data length, destination address, and port number
            DatagramPacket packet = new DatagramPacket(imageData, imageData.length, address, 8888);

            // Send data
            socket.send(packet);

            // Close socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
