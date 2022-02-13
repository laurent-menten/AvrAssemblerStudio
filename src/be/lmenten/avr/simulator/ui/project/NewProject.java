package be.lmenten.avr.simulator.ui.project;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.lmenten.avr.simulator.project.AvrSimulatorProject;
import be.lmenten.avr.simulator.ui.AvrAssemblerStudioController;
import be.lmenten.avr.simulator.ui.NewProjectControler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewProject
{
	private static final String FXML_FILE = "NewProject.fxml";

	// ========================================================================
	// = 
	// ========================================================================

	public static AvrSimulatorProject display()
	{
		AvrSimulatorProject project = null;
		
		try
		{
			Stage stage = new Stage();
			stage.initModality( Modality.APPLICATION_MODAL );
			stage.setTitle( "New projet" );
			stage.getIcons().add( new Image( NewProject.class.getResourceAsStream( "/be/lmenten/avr/simulator/images/icon-48.png" ) ) );

			FXMLLoader loader = new FXMLLoader( NewProject.class.getResource( FXML_FILE ) );
			Parent root = loader.load();
			NewProjectControler controler = (NewProjectControler)loader.getController();
			Scene scene = new Scene( root, Color.LIGHTYELLOW );

			stage.setScene( scene );
			stage.showAndWait();		

			project = AvrSimulatorProject.newProject( controler.getDevice() );
			project.setApplicationFile( controler.getApplicationFile() );
			project.setBootLoaderFile( controler.getBootLoaderFile() );
			project.setEepromFile( controler.getEepromFile() );

			return project;
		}

		catch( IOException ex )
		{
			LOG.log( Level.SEVERE, "Could not process " + NewProject.FXML_FILE, ex );
		}

		return project;
	}

	// ========================================================================
	// = 
	// ========================================================================

	// ========================================================================
	// = 
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( AvrAssemblerStudioController.class.getName() );
}
