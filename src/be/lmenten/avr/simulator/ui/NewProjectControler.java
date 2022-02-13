package be.lmenten.avr.simulator.ui;

import java.io.File;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import be.lmenten.avr.core.descriptor.AvrDevice;
import be.lmenten.avr.core.descriptor.CoreDescriptor;
import be.lmenten.avr.core.descriptor.CoreFeatures;
import be.lmenten.utils.ui.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

public class NewProjectControler
	implements Initializable
{
	@FXML
	private ChoiceBox<AvrDevice> deviceList;

	private AvrDevice device;
	private boolean deviceHasBootloaderFeature;

	// ------------------------------------------------------------------------
	
	@FXML
	private TextField applicationFilename;
	private File applicationFile;
	private boolean applicationFileExists;

	@FXML
	private Button selectApplication;

	// ------------------------------------------------------------------------
	
	@FXML
	private CheckBox useFakeBootloader;

	@FXML
	private TextField bootloaderFilename;
	private File bootloaderFile;
	private boolean bootloaderFileExists;

	@FXML
	private Button selectBootloader;

	// ------------------------------------------------------------------------

	@FXML
	private TextField eepromFilename;
	private File eepromFile;
	private boolean eepromFileExists;

	@FXML
	private Button selectEeprom;


	// ========================================================================
	// = 
	// ========================================================================

	@Override
	public void initialize( URL location, ResourceBundle resources )
	{
		// --------------------------------------------------------------------
		// - Configure TextField ----------------------------------------------
		// --------------------------------------------------------------------

		configureTextField( applicationFilename );

		configureTextField( bootloaderFilename );

		configureTextField( eepromFilename );

		// ---------------------------------------------------------------------
		// - Configure ChoiceBox for device and initialise to default ----------
		// ---------------------------------------------------------------------

		for( AvrDevice device : AvrDevice.values() )
		{
			deviceList.getItems().add( device );
		}

		deviceList.setOnAction( ev ->
		{
			device = deviceList.getValue();

			configForDevice( device );
		} );

		deviceList.setValue( AvrDevice.ATMEGA2560 ); // FIXME make default configurable

		// --------------------------------------------------------------------

		useFakeBootloader.setSelected( true ); // FIXME make default configurable
		useFakeBootloader( null );
	}

	// ========================================================================
	// = 
	// ========================================================================

	private void configureTextField( TextField textField )
	{
		textField.setOnKeyPressed( ev ->
		{
			if( ev.getCode() == KeyCode.ENTER )
			{
				setImageFilename( textField );
			}
		} );

		textField.focusedProperty().addListener( (node, hadFocus, hasFocus ) ->
		{
			if( hasFocus == false )
			{
				setImageFilename( textField );
			}
		} );		
	}

	// ------------------------------------------------------------------------

	private void configForDevice( AvrDevice device )
	{
		CoreDescriptor cdesc = device.getDescriptor();

		deviceHasBootloaderFeature = cdesc.hasFeature( CoreFeatures.BOOT_LOADER );

		useFakeBootloader( null );
	}

	// ========================================================================
	// = 
	// ========================================================================

	@FXML
	private void useFakeBootloader( ActionEvent ev )
	{
		if( deviceHasBootloaderFeature && ! useFakeBootloader.isSelected() )
		{
			selectBootloader.setDisable( false );
			bootloaderFilename.setDisable( false );
		}
		else
		{
			selectBootloader.setDisable( true );
			bootloaderFilename.setDisable( true );
		}
	}

	@FXML
	private void setImageFilename( TextField textField )
	{
		FXUtils.removeStyle( textField, "-fx-text-inner-color" );

		String filename = textField.getText();
		if( filename.isBlank() )
		{
			if( textField == applicationFilename )
			{
				applicationFile = null;
				applicationFileExists = false;
			}

			else if( textField == bootloaderFilename )
			{
				bootloaderFile = null;
				bootloaderFileExists = false;
			}

			else if( textField == eepromFilename )
			{
				eepromFile = null;
				eepromFileExists = false;
			}

			return;
		}

		// --------------------------------------------------------------------

		String color = "red";

		try
		{
			String filePath = Paths.get( filename ).toAbsolutePath().normalize().toString();
			File file = new File( filePath );
			if( file.exists() )
			{
				if( textField == applicationFilename )
				{
					applicationFile = file;
					applicationFileExists = true;
				}

				else if( textField == bootloaderFilename )
				{
					bootloaderFile = file;
					bootloaderFileExists = true;
				}

				else if( textField == eepromFilename )
				{
					eepromFile = file;
					eepromFileExists = true;
				}

				color = "green";
			}
		}

		catch( InvalidPathException ex )
		{
		}

		finally
		{
			FXUtils.addStyle( textField, "-fx-text-inner-color", color );
		}
	}

	@FXML
	private void selectImageFile( ActionEvent ev )
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter( "Intel HEX file", "*.hex", "*.ihex" ),
			new FileChooser.ExtensionFilter( "ELF binary", "*.elf" ),
			new FileChooser.ExtensionFilter( "All files", "*.*", "*" )
		);

		String currentPath = Paths.get( "." ).toAbsolutePath().normalize().toString();
		fileChooser.setInitialDirectory( new File( currentPath ) );

		fileChooser.setTitle( "Select application file" );

		TextField textField = null;
		File file = fileChooser.showOpenDialog( null );
		if( file != null )
		{
			if( ev.getSource() == selectApplication )
			{
				textField = applicationFilename;
			}

			else if( ev.getSource() == selectBootloader )
			{
				textField = bootloaderFilename;
			}

			else if( ev.getSource() == selectEeprom )
			{
				textField = eepromFilename;
			}

			textField.setText( file.toString() );

			FXUtils.removeStyle( textField, "-fx-text-inner-color" );

			if( file.exists() )
			{
				FXUtils.addStyle( textField, "-fx-text-inner-color", "green" );

				if( ev.getSource() == selectApplication )
				{
					applicationFile = file;
					applicationFileExists = true;
				}

				else if( ev.getSource() == selectBootloader )
				{
					bootloaderFile = file;
					applicationFileExists = true;
				}

				else if( ev.getSource() == selectEeprom )
				{
					eepromFile = file;
					eepromFileExists = true;
				}
			}
			else
			{
				FXUtils.addStyle( textField, "-fx-text-inner-color", "red" );
			}
		}
	}

	@SuppressWarnings("unused")
	private boolean allFilesExists()
	{
		return applicationFileExists
				&& ((!deviceHasBootloaderFeature) || ((bootloaderFile != null) && bootloaderFileExists))
				&& ((eepromFile == null) || eepromFileExists)
				;
	}

	// ========================================================================
	// = 
	// ========================================================================

	public AvrDevice getDevice()
	{
		return device;
	}

	public File getApplicationFile()
	{
		return applicationFile;
	}

	public File getBootLoaderFile()
	{
		return bootloaderFile;
	}

	public File getEepromFile()
	{
		return eepromFile;
	}

	// ========================================================================
	// = 
	// ========================================================================

	@SuppressWarnings("unused")
	private static final Logger LOG
		= Logger.getLogger( AvrAssemblerStudioController.class.getName() );
}
