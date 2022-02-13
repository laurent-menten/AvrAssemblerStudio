package be.lmenten.avr.simulator.ui.control;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class CombinedControl
	extends HBox
{
	private TextField textField;
	private Button button;

	public CombinedControl()
	{
		String css =  CombinedControl.class.getResource( "combined.css" ).toExternalForm();
        getStylesheets().add( css );

        initGraphics();

        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics()
    {
        getStyleClass().add( "combined-control" );

        textField = new TextField();
 //       textField.setFocusTraversable(false);
//        textField.setTextFormatter(new TextFormatter<>(change -> change.getText().matches("[0-9]*(\\.[0-9]*)?") ? change : null));

        button = new Button("°C");
//        button.setFocusTraversable(false);

 //       setSpacing(0);
 //       setFocusTraversable(true);
        setFillHeight(false);
        setAlignment(Pos.CENTER);

        getChildren().addAll(textField, button);
    }

    private void registerListeners() {
        button.setOnMousePressed( e -> {
        	System.out.println( e );
        } );
    }
}