package nathsou;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nathsou.ScreenshotTask.ScreenshotTakenEvent.ScreenshotTakenEvent;
import nathsou.ScreenshotTask.ScreenshotTakenEvent.ScreenshotTakenListener;
import nathsou.ScreenshotTask.ScreenshotTask;
import nathsou.ScreenshotTask.ScreenshotTaskFinishedEvent;
import nathsou.ScreenshotTask.ScreenshotTaskFinishedListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

public class Main extends Application {

    private Stage window;
    private Timer timer;
    private ScreenshotTask snapTask;
    private String windowTitle = "Snapper";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        window.setTitle(windowTitle);

        BorderPane root = new BorderPane();

        VBox layout = new VBox(5);
        layout.setPadding(new Insets(5, 5, 5, 5));

        //Menu

        /*
        MenuBar menuBar = new MenuBar();
        Menu editMenu = new Menu("Edit");
        menuBar.setUseSystemMenuBar(true);

        RadioMenuItem launchOnStartup = new RadioMenuItem("Launch at startup");

        editMenu.getItems().add(launchOnStartup);

        menuBar.getMenus().add(editMenu);

        root.setTop(menuBar);
        */

        //Config box

        SetupVBox config = new SetupVBox(primaryStage);


        HBox startStopBox = new HBox(5);
        startStopBox.setPadding(config.setupBox.getPadding());

        Button startStopButton = new Button("Start");
        Button savePreferencesButton = new Button("Save preferences");

        startStopBox.getChildren().addAll(startStopButton, savePreferencesButton);

        config.saveFolderField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                startStopButton.setDisable(!new File(newValue).exists());
            }
        });

        layout.getChildren().add(config.setupBox);
        root.setCenter(layout);

        startStopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (startStopButton.getText().equals("Start")) {

                    long nbMs = config.freqHours.getValue() * 3600 + config.freqMins.getValue() * 60 + config.freqSecs.getValue();
                    timer = new Timer();
                    nbMs *= 1000;
                    snapTask = new ScreenshotTask(config.saveDir, config.limit.getValue());
                    snapTask.setCompressionFactor(config.doNotCompressCheckBox.isSelected() ? -1f : (float) config.compressionFactorSlider.getValue());
                    timer.schedule(snapTask, nbMs, nbMs);
                    config.setupBox.setDisable(true);
                    startStopButton.setText("Stop");

                    snapTask.addScreenshotTaskFinishedListener(new ScreenshotTaskFinishedListener() {
                        @Override
                        public void ScreenshotTaskFinished(ScreenshotTaskFinishedEvent event) {

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    config.setupBox.setDisable(false);
                                    startStopButton.setText("Start");
                                    window.setTitle(windowTitle);
                                    timer.cancel();
                                    timer.purge();
                                }
                            });
                        }
                    });
                } else {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            snapTask.stop();
                        }
                    });
                }

                snapTask.addScreenshotTakenListener(new ScreenshotTakenListener() {
                    @Override
                    public void ScreenshotTakenEvent(ScreenshotTakenEvent event) {
                        //Prompt date:
                        SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
                        System.out.println("Snap taken the " + ft.format(new Date()) + " [" + snapTask.getSnapCount() + "/" + (snapTask.isLimited() ? snapTask.getSnapLimit() : "Infinity") + "]");

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                window.setTitle(windowTitle + " [" + snapTask.getSnapCount() + "/" + (snapTask.isLimited() ? snapTask.getSnapLimit() : "Infinity") + "]");
                            }
                        });
                    }
                });
            }
        });

        savePreferencesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                config.prefs.put("SaveDir", config.saveFolderField.getText());
                config.prefs.putInt("freqHours", config.freqHours.getValue());
                config.prefs.putInt("freqMins", config.freqMins.getValue());
                config.prefs.putInt("freqSecs", config.freqSecs.getValue());
                config.prefs.putBoolean("limited", config.limited.isSelected());
                config.prefs.putFloat("compressionFactor", (float) config.compressionFactorSlider.getValue());
                config.prefs.putInt("limit", config.limit.getValue() != 0 ? config.limit.getValue() : -1);
                config.prefs.putBoolean("compress", config.doNotCompressCheckBox.isSelected());
            }
        });

        layout.getChildren().add(startStopBox);

        Scene scene = new Scene(root);

        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
               if(snapTask != null && !snapTask.isFinished()) snapTask.stop();
            }
        });

        window.setScene(scene);
        window.setResizable(false);
        window.show();
    }


}