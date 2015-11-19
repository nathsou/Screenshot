package nathsou;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Created by nathan on 09/08/15.
 */
public class IntegerField extends TextField
{

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerField(){

    }

    public IntegerField(int value){
        setValue(value);
    }

    public IntegerField(int min, int max) {
        this.min = min;
        this.max = max;

        listenForChange();
    }

    public IntegerField(int min, int max, int value) {
        this.min = min;
        this.max = max;
        setValue(value);

        listenForChange();
    }

    public void setValue(int val){
        setText(Integer.toString(val));
    }

    public int getValue(){
        return getText().equals("") ? 0 : Integer.parseInt(getText());
    }

    private void listenForChange(){
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.equals("") && !newValue.equals("-")) {
                    try {
                        int val = Integer.parseInt(newValue);
                        if (val < min) setValue(min);
                        if (val > max) setValue(max);
                    } catch (NumberFormatException nfe) {
                        setText(oldValue);
                    }
                }
            }
        });
    }
}