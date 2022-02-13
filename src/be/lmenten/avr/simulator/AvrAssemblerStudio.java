package be.lmenten.avr.simulator;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.InstructionSetDebug;
import be.lmenten.avr.simulator.ui.AvrAssemblerStudioController;
import be.lmenten.utils.Dirty;
import be.lmenten.utils.ResourceBundleWithCommon;
import be.lmenten.utils.app.fx.FxApplication;
import be.lmenten.utils.lang.UnicodeChar;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 
 * @author <a href="mailto:laurent.menten@gmail.com">Laurent Menten<a>
 * @version 1.0
 * @since 1.0 - 2021 / 08 / 23
 */
public class AvrAssemblerStudio
	extends FxApplication
{
	// ------------------------------------------------------------------------
	// - WARNING: These value are updated by build script ---------------------
	// ------------------------------------------------------------------------

	public static final String APP_TITLE = "Avr Assembler Studio";
	public static final Runtime.Version APP_VERSION = Runtime.Version.parse( "1.0.1-ea0-1-devel" );
	public static final long APP_BUILD_NUMBER = 1L;
	public static final LocalDateTime APP_BUILD_DATETIME = LocalDateTime.parse( "2022-01-16T16:22:04" );

	// ------------------------------------------------------------------------
	// - Application constants ------------------------------------------------
	// ------------------------------------------------------------------------

	public static final ResourceBundle APP_RESOURCE
		= ResourceBundle.getBundle( AvrAssemblerStudio.class.getName() );

	public static final String APP_STYLESHEET_URL
		= AvrAssemblerStudio.class
			.getResource( "AvrAssemblerStudio.css" )
			.toExternalForm();

	private final Image APP_ICON
		= new Image( AvrAssemblerStudio.class.getResourceAsStream( "images/icon-48.png" ) )	;

	// ========================================================================
	// = Application entrypoint ===============================================
	// ========================================================================

	public AvrAssemblerStudio()
	{
	}

	// ========================================================================
	// = FxApplication interface ==============================================
	// ========================================================================

	@Override
	public String getAppTitle()
	{
		return APP_TITLE;
	}

	@Override
	public Runtime.Version getAppVersion()
	{
		return APP_VERSION;
	}

	@Override
	public Image getAppIcon()
	{
		return APP_ICON;
	}

	@Override
	public long getBuildNumber()
	{
		return APP_BUILD_NUMBER;
	}

	@Override
	public LocalDateTime getBuildDateTime()
	{
		return APP_BUILD_DATETIME;
	}

	// ========================================================================
	// = JavaFX application initialisation ====================================
	// ========================================================================

	@Override
	public void init()
		throws Exception
	{
		super.init();
	}

	// ========================================================================
	// = Application logic ====================================================
	// ========================================================================

	private Stage stage;
	private AvrAssemblerStudioController controller;
	private File currentFile;

	@Override
	public void start( Stage stage )
		throws Exception
	{
		super.start( stage );

		LOG.info( "Application running ..." );

		// --------------------------------------------------------------------
		// - Initialise stage [mandatory things] ------------------------------
		// --------------------------------------------------------------------

		stage.setOnCloseRequest( ev ->
		{
			ev.consume();
			closeRequest();
		} );

		// --------------------------------------------------------------------
		
		stage.setTitle( RES.getString( "app.title" ) );
		stage.getIcons().add( APP_ICON );

		URL fxml = AvrAssemblerStudio.class.getResource( "ui/AvrAssemblerStudio.fxml" );
		FXMLLoader loader = new FXMLLoader(  fxml, RES );
		Pane root = loader.load();

		Scene scene = new Scene( root );
		scene.getStylesheets().add( APP_STYLESHEET_URL );

		stage.setScene( scene );
		stage.setMaximized( true );
		stage.show();

		// --------------------------------------------------------------------

		controller = (AvrAssemblerStudioController) loader.getController();

		controller.currentProjectProperty().addListener( (project,oldProject,newProject) ->
		{
			newProject.projectFileProperty().addListener( (file,oldFile,newFile) ->
			{
				if( newFile != null )
				{
					currentFile = newFile;
					stage.setTitle( RES.getString( "app.title" ) + " - " + newFile );
				}
			} );

			newProject.isDirtyProperty().addListener( (state,wasDirty,isDirty) ->
			{
				if( currentFile != null )
				{
					stage.setTitle( RES.getString( "app.title" ) + " - " + currentFile
						+ (isDirty == Dirty.YES ? " "+UnicodeChar.CHECKED : " "+UnicodeChar.UNCHECKED) );
				}
				else
				{
					stage.setTitle( RES.getString( "app.title" ) + " - " 
						+ (isDirty == Dirty.YES? UnicodeChar.CHECKED : UnicodeChar.UNCHECKED) );
				}
			} );
		} );
	}

	@Override
	public void stop()
			throws Exception
	{
		LOG.info( "Finishing application ..." );

		// ------------------------------------------------------------
		// - Close main window ----------------------------------------
		// ------------------------------------------------------------

		if( stage != null )
		{
			stage.close();
		}

		super.stop();
	}

	// ========================================================================
	// =
	// ========================================================================

	/**
	 * 
	 */
	private void closeRequest()
	{
		Alert alert = new Alert( AlertType.CONFIRMATION );
		alert.initOwner( stage );
		alert.initModality( Modality.APPLICATION_MODAL );

		alert.setTitle( RES.getString( "app.title" ) );
		alert.setHeaderText( RES.getString( "alert.quit.header" ) );

		if( controller.isProjectDirty() )
		{	
			alert.setContentText( RES.getString( "alert.quit.content.save" ) );

			// --------------------------------------------------------------------

			ButtonType yesButton = new ButtonType( RES.getString("yes"), ButtonData.YES );
			ButtonType noButton  = new ButtonType( RES.getString("save"), ButtonData.NO );
			ButtonType cancelButton = new ButtonType( RES.getString("cancel"), ButtonData.CANCEL_CLOSE );
			alert.getButtonTypes().setAll( yesButton, noButton, cancelButton );
		}
		else
		{
			alert.setContentText( RES.getString( "alert.quit.content.nosave" ) );

			// --------------------------------------------------------------------

			ButtonType yesButton = new ButtonType( RES.getString("yes"), ButtonData.YES );
			ButtonType cancelButton = new ButtonType( RES.getString("cancel"), ButtonData.CANCEL_CLOSE );
			alert.getButtonTypes().setAll( yesButton, cancelButton );
		}
		
		DialogPane pane = alert.getDialogPane();
		for( ButtonType button : alert.getButtonTypes() )
		{
			((Button) pane.lookupButton( button ) )
				.setDefaultButton( button.getButtonData() == ButtonData.CANCEL_CLOSE );
		}
		
		// --------------------------------------------------------------------

		alert.showAndWait().ifPresent( r ->
		{
			// ----------------------------------------------------------------
			// - Without saving -----------------------------------------------
			// ----------------------------------------------------------------

			if( r.getButtonData() == ButtonData.YES )
			{
				Platform.exit();
			}

			// ----------------------------------------------------------------
			// - With saving --------------------------------------------------
			// ----------------------------------------------------------------

			else if( r.getButtonData() == ButtonData.NO )
			{
				controller.saveOnCloseRequest();
			}

			// ----------------------------------------------------------------
			// - Cancel -------------------------------------------------------
			// ----------------------------------------------------------------

			else if( r.getButtonData() == ButtonData.CANCEL_CLOSE )
			{
			}
		} );
	}

	// ========================================================================
	// =
	// ========================================================================

	/*package*/ void quickTest()
	{
//		Stage stage = new Stage();
//		stage.setTitle( "Quick test" );
//
//		// --------------------------------------------------------------------
//
//		...
//
//		// --------------------------------------------------------------------
//
//        HBox root = new HBox();
//        root.getChildren().addAll( ... );
//
//        stage.setScene( new Scene( root, 600, 400 ) );
//        stage.showAndWait();

		Platform.exit();
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( AvrAssemblerStudio.class.getName() );

	// ------------------------------------------------------------------------

	private static final ResourceBundle RES
		= ResourceBundle.getBundle( AvrAssemblerStudio.class.getName() );
}
