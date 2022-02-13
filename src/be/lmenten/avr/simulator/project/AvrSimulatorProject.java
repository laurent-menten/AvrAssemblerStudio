package be.lmenten.avr.simulator.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import be.lmenten.avr.binfmt.hex.FileFormatException;
import be.lmenten.avr.core.descriptor.AvrDevice;
import be.lmenten.utils.Dirty;
import javafx.beans.property.ObjectProperty;

public interface AvrSimulatorProject
	extends Serializable
{
	static final String MAGIC = "AvrSimuator";

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @param file
	 */
	public void setProjectFile( File file );

	/**
	 * 
	 * @return
	 */
	public File getProjectFile();

	/**
	 * 
	 * @return
	 */
	public ObjectProperty<File> projectFileProperty();

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @param isDirty
	 */
	public void setDirty( Dirty isDirty );

	/**
	 * 
	 * @return
	 */
	public Dirty isDirty();

	/**
	 * 
	 * @return
	 */
	public ObjectProperty<Dirty> isDirtyProperty();

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @return
	 */
	AvrDevice getAvrDevice();

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @param applicatonFile
	 */
	public void setApplicationFile( File applicatonFile );

	/**
	 * 
	 * @return
	 */
	public File getApplicationFile();

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @param applicatonFile
	 */
	public void setBootLoaderFile( File bootLoaderFile );

	/**
	 * 
	 * @return
	 */
	public File getBootLoaderFile();

	// ------------------------------------------------------------------------

	/**
	 * 
	 * @param applicatonFile
	 */
	public void setEepromFile( File eepromFile );

	/**
	 * 
	 * @return
	 */
	public File getEepromFile();

	// ========================================================================
	// = Factory ==============================================================
	// ========================================================================

	public static AvrSimulatorProject newProject( AvrDevice device )
	{
		return new AvrSimulatorProject_v1( device );
	}

	// ========================================================================
	// = Load =================================================================
	// ========================================================================

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static AvrSimulatorProject load( File file )
		throws IOException, ClassNotFoundException
	{
		AvrSimulatorProject project = null;

		try( FileInputStream fis = new FileInputStream( file );
				GZIPInputStream gzis = new GZIPInputStream( fis );
					ObjectInputStream ois = new ObjectInputStream( gzis ) )
		{
			String magic = (String) ois.readObject();
			if( ! MAGIC.equals( magic ) )
			{
				throw new FileFormatException( file.toString(), "Invalid magic" );
			}

			long version = ois.readLong();
			if( version == 1l )
			{				
				project = (AvrSimulatorProject) ois.readObject();
			}
			else
			{
				throw new RuntimeException( "Invalid version " + version );
			}
		}

		return project;
	}

	// ========================================================================
	// = Save =================================================================
	// ========================================================================

	/**
	 * 
	 * @throws IOException
	 */
	public default void save()
		throws IOException
	{
		AvrSimulatorProject.save( getProjectFile(), this );
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public default void save( File file )
		throws IOException
	{
		AvrSimulatorProject.save( file, this );
	}

	/**
	 * 
	 * @param file
	 * @param project
	 * @throws IOException
	 */
	public static void save( File file, AvrSimulatorProject project )
		throws IOException
	{
		try( FileOutputStream fos = new FileOutputStream( file );
				GZIPOutputStream gzos = new GZIPOutputStream( fos );
					ObjectOutputStream oos = new ObjectOutputStream( gzos ) )
		{
			oos.writeObject( MAGIC );

			if( project instanceof AvrSimulatorProject_v1 )
			{
				oos.writeLong( 1l );
			}

			oos.writeObject( project );
		}
	}
}
