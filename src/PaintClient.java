import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PaintClient extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 320, 50);

        Label label = new Label("Enter id:");
        TextField portField = new TextField();
        //int id = Integer.parseInt(portField.getText());
        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> {
            int id = Integer.parseInt(portField.getText());
            openPaintApp(id);
        });

        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10));
        inputBox.getChildren().addAll(label, portField, connectButton);

        root.setCenter(inputBox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Paint Client");
        primaryStage.show();
    }

    private void openPaintApp(int id) {
        // 打开 SimplePaintApp 窗口
        // 打开 SimplePaintApp 窗口，并传递Canvas对象
        SimplePaintApp paintApp = new SimplePaintApp(id);
        paintApp.start(new Stage());


        // 关闭客户端窗口
        //primaryStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
