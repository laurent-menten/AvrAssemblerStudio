package be.lmenten.avr.simulator.project;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import be.lmenten.avr.core.descriptor.AvrDevice;
import be.lmenten.utils.Dirty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/*package*/ abstract class AvrSimulatorProjectBaseImpl
	implements AvrSimulatorProject
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------
	// - Project propeties ----------------------------------------------------
	// ------------------------------------------------------------------------

	private transient ObjectProperty<Dirty> isDirty
		= new SimpleObjectProperty<>();

	private transient ObjectProperty<File> projectFile
		= new SimpleObjectProperty<>();

	// ------------------------------------------------------------------------
	// - Project data ---------------------------------------------------------
	// ------------------------------------------------------------------------

	private final AvrDevice device;

	private File applicationFile;
	private File bootLoaderFile;
	private File eepromFile;

	// ========================================================================
	// = 
	// ========================================================================

	public AvrSimulatorProjectBaseImpl( AvrDevice device )
	{
		this.device = device;
	}

	// ========================================================================
	// = Project properties ===================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectFile( File file )
	{
		this.projectFile.set( file );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getProjectFile()
	{
		return projectFile.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectProperty<File> projectFileProperty()
	{
		return projectFile;
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDirty( Dirty isDirty )
	{
		this.isDirty.set( isDirty );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dirty isDirty()
	{
		return isDirty.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectProperty<Dirty> isDirtyProperty()
	{
		return isDirty;
	}

	// ========================================================================
	// = Project data =========================================================
	// ========================================================================

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AvrDevice getAvrDevice()
	{
		return device;
	}

	// ------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationFile( File applicatonFile )
	{
		this.applicationFile = applicatonFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getApplicationFile()
	{
		return applicationFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBootLoaderFile( File bootLoaderFile )
	{
		this.bootLoaderFile = bootLoaderFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getBootLoaderFile()
	{
		return bootLoaderFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEepromFile( File eepromFile )
	{
		this.eepromFile = eepromFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getEepromFile()
	{
		return eepromFile;
	}

	// ========================================================================
	// = Serialisation ========================================================
	// ========================================================================

	/**
	 * 
	 * @param ois the {@link ObjectInputStream} from where to read
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject( ObjectInputStream ois )
		throws IOException, ClassNotFoundException
	{
		ois.defaultReadObject();

		isDirty = new SimpleObjectProperty<>( Dirty.UNKNOWN );
		projectFile = new SimpleObjectProperty<>();
	}

	/**
	 * 
	 * @param oos the {@link ObjectOutputStream} where to write
	 * @throws IOException
	 */
	private void writeObject( ObjectOutputStream oos )
		throws IOException
	{
		oos.defaultWriteObject();
	}

	// ========================================================================
	// =
	// ========================================================================

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();

		s.append( "AvrSimultatorProject@" ).append( hashCode() )
		 .append( "[" )
		 	.append( "project file:" ).append( projectFile ).append( ", " )
		 	.append( "state: " ).append( isDirty ).append( ", " )

		 	.append( "device: " ).append( device ).append( ", " )		 
		 	.append( "application file: " ).append( applicationFile ).append( ", " )
		 	.append( "boot loader file: " ).append( bootLoaderFile ).append( ", " )
		 	.append( "eeprom file: " ).append( eepromFile )
		 .append( "]" );

		return s.toString();
	}
}
