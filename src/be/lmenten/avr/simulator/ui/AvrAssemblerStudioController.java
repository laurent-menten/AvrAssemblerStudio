package be.lmenten.avr.simulator.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.CoreFactory;
import be.lmenten.avr.core.descriptor.AvrDevice;
import be.lmenten.avr.core.descriptor.CoreMemoryRange;
import be.lmenten.avr.core.event.CoreEvent;
import be.lmenten.avr.core.event.CoreEventListener;
import be.lmenten.avr.core.exception.ConfigurationException;
import be.lmenten.avr.simulator.AvrAssemblerStudio;
import be.lmenten.avr.simulator.project.AvrSimulatorProject;
import be.lmenten.avr.simulator.ui.instructionview.InstructionView;
import be.lmenten.avr.simulator.ui.project.NewProject;
import be.lmenten.avr.simulator.ui.registerview.RegisterView;
import be.lmenten.utils.Dirty;
import be.lmenten.utils.app.fx.FxController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

public class AvrAssemblerStudioController
	extends FxController<AvrAssemblerStudio>
	implements CoreEventListener
{
	private static final File DEFAULT_FILE
		= new File( "Untitled.aspz" );

	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

	@FXML
	private BorderPane root;

	@FXML
	private Button openButton;
	@FXML
	private Button newButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button quickSaveButton;
	@FXML
	private Button saveAsButton;

	@FXML
	private Button testButton;
	@FXML
	private Button stepButton;
	@FXML
	private Button stepOverButton;
	@FXML
	private Button stepOutButton;
	@FXML
	private Button runButton;

	@FXML
	private Label statusLeft;
	@FXML
	private Label statusRight;
	
	@FXML
	private Accordion registersAccordion;

	@FXML
	private TitledPane generalRegisters;
	private RegisterView generalRegistersView;

	@FXML
	private TitledPane ioRegisters;
	private RegisterView ioRegistersView;

	@FXML
	private TitledPane extendedIoRegisters;
	private RegisterView extendedIoRegistersView;
	
	@FXML
	private BorderPane disassembly;
	private InstructionView disassemblyView;

	@FXML
	private Accordion memoryAccordion;

	// ------------------------------------------------------------------------

	private ResourceBundle res;
	private Core core;
	

	// ========================================================================
	// = Initialise controller ================================================
	// ========================================================================

	@Override
	public void initialize( URL url, ResourceBundle res )
	{
		this.res = res;

		// --------------------------------------------------------------------

		generalRegistersView = new RegisterView( CoreMemoryRange.REGISTERS_RANGE_NAME );
		generalRegisters.setContent( generalRegistersView );

		ioRegistersView = new RegisterView( CoreMemoryRange.IO_RANGE_NAME );
		ioRegisters.setContent( ioRegistersView );

		extendedIoRegistersView = new RegisterView( CoreMemoryRange.EXTENDED_IO_RANGE_NAME );
		extendedIoRegisters.setContent( extendedIoRegistersView );

		// --------------------------------------------------------------------

		disassemblyView = new InstructionView();
		root.setCenter( disassemblyView );

		// --------------------------------------------------------------------

		saveButton.setDisable( true );
		saveAsButton.setDisable( true );

		// --------------------------------------------------------------------

		registersAccordion.setExpandedPane( generalRegisters );
		memoryAccordion.setExpandedPane( ioRegisters );
	}

	// ========================================================================
	// = Simulation ===========================================================
	// ========================================================================

	public void step()
	{
		if( core != null )
		{
			try
			{
				core.step();	
			}

			catch( Throwable ex )
			{
				LOG.log( Level.SEVERE, "STEP " + ex.getMessage(), ex );
			}

			finally
			{
				reportState();
				disassemblyView.showInstruction( core.getCurrentInstruction() );
			}
		}
	}

	public void stepOver()
	{
		if( core != null )
		{
			try
			{
				core.stepOver();
			}

			catch( Throwable ex )
			{
				LOG.log( Level.SEVERE, "STEP OVER " + ex.getMessage(), ex );
			}

			finally
			{
				reportState();
				disassemblyView.showInstruction( core.getCurrentInstruction() );
			}
		}
	}

	public void stepOut()
	{
		if( core != null )
		{
			try
			{
				core.stepOut();
			}

			catch( Throwable ex )
			{
				LOG.log( Level.SEVERE, "STEP OUT " + ex.getMessage(), ex );
			}

			finally
			{
				reportState();
				disassemblyView.showInstruction( core.getCurrentInstruction() );
			}
		}
	}

	public void run()
	{
		if( core != null )
		{
			try
			{
				core.run();
			}

			catch( Throwable ex )
			{
				LOG.log( Level.SEVERE, "RUN " + ex.getMessage(), ex );
			}

			finally
			{
				reportState();
				disassemblyView.showInstruction( core.getCurrentInstruction() );
			}
		}
	}

	// ------------------------------------------------------------------------

	private void reportState()
	{
		statusLeft.setText(
			String.format( "PC = 0x%06X, SP = 0x%06X", core.getProgramCounter(), core.getStackPointer() )
		);
	}

	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

	@Override
	public void onEvent( CoreEvent ev )
	{
		switch( ev.getEventType() )
		{
			case CORE_MODE_CHANGED:
				LOG.info( "MCU mode: " + ev.getOldValue() + " -> " + ev.getNewValue() );
				break;

			default:
				LOG.info( "CoreEvent: " + ev.getEventType() );
				break;
		}
	}

	// ------------------------------------------------------------------------
	// - 
	// ------------------------------------------------------------------------

	public void test()
	{
		AvrSimulatorProject project = AvrSimulatorProject.newProject( AvrDevice.ATMEGA2560 );
		project.setApplicationFile( new File( "Blink.2560.hex" ) );
		project.setBootLoaderFile( null );
		project.setEepromFile( null );

		createProject( project );
	}

	// ========================================================================
	// = For AvrAssemblerStudio.closeRequest() ======================================
	// ========================================================================

	/**
	 * 
	 */
	public void saveOnCloseRequest()
	{
		if( currentProject.get().getProjectFile() != null )
		{
			saveButton.fire();
		}
		else
		{
			saveAsButton.fire();
		}

		if( currentProject.get().isDirty() == Dirty.NO )
		{
			Platform.exit();
		}
	}

	// ========================================================================
	// = Project buttons ======================================================
	// ========================================================================

	@FXML
	public void openProject( Event ev )
	{
		if( isProjectDirty() )
		{
			if( ! askSaveDirtyProject( "alert.openproject.header", "alert.openproject.content.save", ev ) )
			{
				return;
			}
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle( "Open projet:" );
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter( "AvrAssemblerStudio project", "*.aspz" )
		);

		String currentPath = Paths.get( "." ).toAbsolutePath().normalize().toString();
		fileChooser.setInitialDirectory( new File( currentPath ) );

		File file = fileChooser.showOpenDialog( null );
		if( file != null )
		{
			try
			{
				loadProject( file );
			}
			catch( Throwable ex )
			{
				LOG.log( Level.SEVERE, "failed to load projet", ex );
			}
		}
	}

	@FXML
	public void openNewProject( Event ev )
	{
		if( isProjectDirty() )
		{
			if( ! askSaveDirtyProject( "alert.newproject.header", "alert.newproject.content.save", ev ) )
			{
				return;
			}
		}

		AvrSimulatorProject project = NewProject.display();
		if( project != null )
		{
			try
			{
				createProject( project );
			}
			catch( Exception ex )
			{
				LOG.log( Level.SEVERE, "Failed to create new project", ex );
			}
		}
	}

	@FXML
	public void saveProject( Event ev )
	{
		if( isProjectDirty() )
		{
			if( currentProject.get().getProjectFile() == null )
			{
				saveProjectAs( ev );
			}
			else
			{
				saveProject();
			}

			currentProject.get().setDirty( Dirty.NO );
		}
	}

	@FXML
	public void saveProjectAs( Event ev )
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle( "Save projet as:" );
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter( "AvrAssemblerStudio project", "*.aspz" )
		);

		String currentPath = Paths.get( "." ).toAbsolutePath().normalize().toString();
		fileChooser.setInitialDirectory( new File( currentPath ) );

		fileChooser.setInitialFileName( DEFAULT_FILE.getName() );

		File file = fileChooser.showSaveDialog( null );
		if( file != null )
		{
			saveProjectAs( file );

			if( currentProject.get().getProjectFile() == null )
			{
				currentProject.get().setProjectFile( file );
				currentProject.get().setDirty( Dirty.NO );
			}
		}
	}

	// ------------------------------------------------------------------------

	private boolean askSaveDirtyProject( String headerKey, String contentKey, Event ev )
	{
		Alert alert = new Alert( AlertType.CONFIRMATION );
		alert.initOwner( root.getScene().getWindow() );
		alert.initModality( Modality.APPLICATION_MODAL );

		alert.setTitle( res.getString( "app.title" ) );
		alert.setHeaderText( res.getString( headerKey ) );
		
		alert.setContentText( res.getString( contentKey ) );

		// ----------------------------------------------------------------

		ButtonType yesButton = new ButtonType( res.getString("yes"), ButtonData.YES );
		ButtonType noButton  = new ButtonType( res.getString("save"), ButtonData.NO );
		ButtonType cancelButton = new ButtonType( res.getString("cancel"), ButtonData.CANCEL_CLOSE );
		alert.getButtonTypes().setAll( yesButton, noButton, cancelButton );

		DialogPane pane = alert.getDialogPane();

		for( ButtonType button : alert.getButtonTypes() )
		{
			((Button) pane.lookupButton( button ) )
				.setDefaultButton( button.getButtonData() == ButtonData.CANCEL_CLOSE );
		}
		
		// --------------------------------------------------------------------

		boolean rc = false;
	
		Optional<ButtonType> r = alert.showAndWait();
		if( r.isPresent() )
		{
			// ----------------------------------------------------------------
			// - Without saving -----------------------------------------------
			// ----------------------------------------------------------------

			if( r.get().getButtonData() == ButtonData.YES )
			{
				rc = true;
			}

			// ----------------------------------------------------------------
			// - With saving --------------------------------------------------
			// ----------------------------------------------------------------

			else if( r.get().getButtonData() == ButtonData.NO )
			{
				rc = false;
			}

			// ----------------------------------------------------------------
			// - Cancel -------------------------------------------------------
			// ----------------------------------------------------------------

			else if( r.get().getButtonData() == ButtonData.CANCEL_CLOSE )
			{
				rc = false;
			}

			// ----------------------------------------------------------------
			// - Not supposed to happen !? ------------------------------------
			// ----------------------------------------------------------------

			else
			{
				throw new RuntimeException( "Unknown button !?" );
			}
		}

		return rc;
	}

	@FXML
	private void ttestt( Event ev )
	{
		if( currentProject.get() != null )
		{
			currentProject.get().setDirty( Dirty.YES );
		}

		if( core != null )
		{
			int r0 = core.getRegister( "R0" ).getData();
			core.getRegister( "R0" ).setData( r0 + 1 );
		}
	}

	// ========================================================================
	// = Project ==============================================================
	// ========================================================================

	private ObjectProperty<AvrSimulatorProject> currentProject
		= new SimpleObjectProperty<>();

	// ------------------------------------------------------------------------

	private void setCurrentProject( AvrSimulatorProject project )
	{
		currentProject.set( project );

		saveAsButton.setDisable( false );	

		currentProject.get().isDirtyProperty().addListener( (o,wasDirty,isDirty) -> 
		{
			if( isDirty == Dirty.YES )
			{
				saveButton.setDisable( currentProject.get().getProjectFile() == null );
			}
			else
			{
				saveButton.setDisable( true );
			}

		} );

		currentProject.get().projectFileProperty().addListener( (o,oldFile,newFile) ->
		{
			if( currentProject.get().isDirty() == Dirty.YES )
			{
				saveButton.setDisable( (newFile == null) || (newFile == DEFAULT_FILE) );
			}
			else
			{
				saveButton.setDisable( newFile == DEFAULT_FILE );
			}
		} );
	}

	/**
	 * 
	 * @return
	 */
	public AvrSimulatorProject getCurrentProject()
	{
		return currentProject.get();
	}

	/**
	 * 
	 * @return
	 */
	public ObjectProperty<AvrSimulatorProject> currentProjectProperty()
	{
		return currentProject;
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @return
	 */
	public boolean isProjectDirty()
	{
		return (currentProject.get() != null)
					&& (currentProject.get().isDirty() == Dirty.YES);
	}

	// ========================================================================
	// = Project I/O ==========================================================
	// ========================================================================

	/**
	 * 
	 * @param file
	 */
	public void loadProject( File file )
	{
		try
		{
			setCurrentProject( AvrSimulatorProject.load( file ) );

			currentProject.get().setDirty( Dirty.NO );
			currentProject.get().setProjectFile( file );

			// --------------------------------------------------------------------

			newCore( currentProject.get().getAvrDevice() );

		}

		catch( ClassNotFoundException ex )
		{
			ex.printStackTrace();
		}
		catch( IOException ex )
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param device
	 */
	private void newCore( AvrDevice device )
	{
		Properties config = new Properties();
		config.put( "HIGH_BYTE.OCDEN", "enabled" );

		try
		{
			core = CoreFactory.forDevice( device, config );
			core.addCoreEventListener( this );

			core.loadFlash( currentProject.get().getApplicationFile() );

			if( currentProject.get().getBootLoaderFile() != null )
			{
				core.loadFlash( currentProject.get().getBootLoaderFile() );
			}
			else
			{
				core.installFakeBootLoader();
			}

			// --------------------------------------------------------------------

			generalRegistersView.setCore( core );
			ioRegistersView.setCore( core );
			extendedIoRegistersView.setCore( core );

			// --------------------------------------------------------------------

			disassemblyView.setCore( core );
			disassemblyView.showInstruction( core.getCurrentInstruction() );
		}

		catch( ConfigurationException ex )
		{
			LOG.log( Level.SEVERE, ex.getMessage(), ex );
		}

		catch( IOException ex )
		{
			LOG.log( Level.SEVERE, "Failed to load iHex file", ex );
		}

	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @param project
	 */
	public void createProject( AvrSimulatorProject project )
	{
		setCurrentProject( project );

		currentProject.get().setDirty( Dirty.YES );
		currentProject.get().setProjectFile( DEFAULT_FILE );

		// --------------------------------------------------------------------

		newCore( currentProject.get().getAvrDevice() );
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	public void saveProject()
	{
		try
		{
			currentProject.get().save();
		}

		catch( IOException ex )
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param file
	 */
	public void saveProjectAs( File file )
	{
		try
		{
			currentProject.get().save( file );
		}

		catch( IOException ex )
		{
			ex.printStackTrace();
		}
	}

	// ========================================================================
	// = 
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( AvrAssemblerStudioController.class.getName() );
}
