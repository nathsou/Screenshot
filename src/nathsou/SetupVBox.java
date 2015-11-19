package nathsou;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created by Nathan on 13/11/2015.
 */
public class SetupVBox {

    public File saveDir;
    private Stage primaryStage;
    public VBox setupBox;
    public HBox chooseFolderBox;
    public TextField saveFolderField;
    public Button chooseFolderButton;
    public HBox chooseFreqBox;
    public IntegerField freqHours;
    public IntegerField freqMins;
    public IntegerField freqSecs;
    public IntegerField limit;
    public HBox limitBox;
    public Slider compressionFactorSlider;
    public CheckBox doNotCompressCheckBox;
    public CheckBox limited;
    public Preferences prefs;

    public SetupVBox(Stage primaryStage){

        this.primaryStage = primaryStage;

        prefs = Preferences.userNodeForPackage(this.getClass());

        saveDir = new File(prefs.get("SaveDir", System.getProperty("user.home")));

        build();

    }

    private void build(){

        setupBox = new VBox(5);
        setupBox.setPadding(new Insets(5, 5, 5, 5));

        setupBox.getChildren().add(new Label("Screenshots's directory"));

        chooseFolderBox = new HBox(5);
        chooseFolderBox.setPadding(setupBox.getPadding());

        saveFolderField = new TextField(saveDir.getAbsolutePath());
        saveFolderField.setMinWidth(250);

        chooseFolderButton = new Button("Choose");
        chooseFolderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser dirChooser = new DirectoryChooser();
                dirChooser.setTitle("Choose directory");
                dirChooser.setInitialDirectory(saveDir);
                saveDir = dirChooser.showDialog(primaryStage);
                if(saveDir != null)
                    saveFolderField.setText(saveDir.getAbsolutePath());
            }
        });

        chooseFolderBox.getChildren().addAll(saveFolderField, chooseFolderButton);
        setupBox.getChildren().add(chooseFolderBox);

        setupBox.getChildren().add(new Label("Frequency"));

        chooseFreqBox = new HBox(5);
        chooseFolderBox.setPadding(new Insets(5, 5, 5, 5));

        freqHours = new IntegerField(0, 24, prefs.getInt("freqHours", 0));
        freqHours.setMaxWidth(35);
        Label freqHoursLabel = new Label(" hour ");
        freqMins = new IntegerField(0, 60, prefs.getInt("freqMins", 5));
        freqMins.setMaxWidth(35);
        Label freqMinsLabel = new Label(" min ");
        freqSecs = new IntegerField(0, 60, prefs.getInt("freqSecs", 0));
        freqSecs.setMaxWidth(35);
        Label freqSecsLabel = new Label(" sec ");

        chooseFreqBox.getChildren().addAll(freqHours, freqHoursLabel, freqMins, freqMinsLabel, freqSecs, freqSecsLabel);

        setupBox.getChildren().add(chooseFreqBox);

        //setupBox.getChildren().add(new Label("Limit"));

        limitBox = new HBox(5);
        limitBox.setPadding(setupBox.getPadding());

        limited = new CheckBox();
        limit = new IntegerField(1, Integer.MAX_VALUE);
        limit.setDisable(true);
        limit.setPromptText("50");

        if(prefs.getInt("limit", -1) != -1)
            limit.setValue(prefs.getInt("limit", 0));

        limitBox.getChildren().addAll(new Label("Limited"), limited, limit);

        limited.setSelected(prefs.getBoolean("limited", false));
        limit.setDisable(!prefs.getBoolean("limited", false));

        limited.setOnAction(e -> limit.setDisable(!limited.isSelected()));

        //Slider

        compressionFactorSlider = new Slider(0f, 1f, prefs.getFloat("compressionFactor", 1f));

        compressionFactorSlider.showTickLabelsProperty();

        Label compressionFactorLabel = new Label("Compression quality : " + Math.round(prefs.getFloat("compressionFactor", 1f) * 100) + "%");

        compressionFactorSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                compressionFactorLabel.setText("Compression quality : " + (int) Math.round((double) newValue * 100) + "%");
            }
        });

        //End Slider

        compressionFactorLabel.setDisable(prefs.getBoolean("compress", false));
        compressionFactorSlider.setDisable(prefs.getBoolean("compress", false));

        doNotCompressCheckBox = new CheckBox("Do not compress");

        doNotCompressCheckBox.setSelected(prefs.getBoolean("compress", false));

        doNotCompressCheckBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        compressionFactorLabel.setDisable(doNotCompressCheckBox.isSelected());
                        compressionFactorSlider.setDisable(doNotCompressCheckBox.isSelected());
                    }
                });
            }
        });

        setupBox.getChildren().addAll(limitBox, compressionFactorLabel, compressionFactorSlider, doNotCompressCheckBox);

    }
}
