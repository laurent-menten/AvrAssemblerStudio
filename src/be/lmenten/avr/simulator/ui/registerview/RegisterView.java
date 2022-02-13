package be.lmenten.avr.simulator.ui.registerview;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.analysis.AccessEvent;
import be.lmenten.avr.core.analysis.AccessEventListener;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.descriptor.CoreMemoryRange;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RegisterView
	extends TableView<CoreRegister>
	implements AccessEventListener
{
	private SimpleObjectProperty<Core> core
		= new SimpleObjectProperty<Core>( null );

	private String memoryRangeName;

	// ========================================================================
	// = 
	// ========================================================================

	public RegisterView( String memoryRangeName )
	{
		this.memoryRangeName = memoryRangeName;

		setEditable( true );
		setRowFactory( new RegisterTableRowFactory<>() );

		TableColumn<CoreRegister,Integer> addrColumn = new TableColumn<>( "Address" );
		addrColumn.setMinWidth( 95 );
		addrColumn.setMaxWidth( 95 );
		addrColumn.setResizable( false );
		addrColumn.setEditable( true );
		addrColumn.setSortable( false );
		addrColumn.setReorderable( false );
		addrColumn.setCellValueFactory( new RegisterCellValueFactory<>( RegisterViewField.ADDRESS ) );
		addrColumn.setCellFactory( new RegisterCellFactory<>( RegisterViewField.ADDRESS ) );
		getColumns().add( addrColumn );

		TableColumn<CoreRegister,String> nameColumn = new TableColumn<>( "Name" );
		nameColumn.setMinWidth( 93 );
		nameColumn.setMaxWidth( 93 );
		nameColumn.setResizable( false );
		nameColumn.setEditable( true );
		nameColumn.setSortable( false );
		nameColumn.setReorderable( false );
		nameColumn.setCellValueFactory( new RegisterCellValueFactory<>( RegisterViewField.NAME ) );
		nameColumn.setCellFactory( new RegisterCellFactory<>( RegisterViewField.NAME ) );
		getColumns().add( nameColumn );

		TableColumn<CoreRegister,Integer> valueColumn = new TableColumn<>( "Value" );
		valueColumn.setMinWidth( 50 );
		valueColumn.setMaxWidth( 50 );
		valueColumn.setResizable( false );
		valueColumn.setEditable( true );
		valueColumn.setSortable( false );
		valueColumn.setReorderable( false );
		valueColumn.setCellValueFactory( new RegisterCellValueFactory<>( RegisterViewField.VALUE ) );
		valueColumn.setCellFactory( new RegisterCellFactory<>( RegisterViewField.VALUE ) );
		getColumns().add( valueColumn );

		// FIXME set column width aligment etc...
	}

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @param newCore
	 */
	public void setCore( Core newCore )
	{
		core.set( newCore );

		getItems().clear();

		CoreMemoryRange range
			= newCore.getDescriptor()
				.getMemoryRange( CoreMemory.SRAM, memoryRangeName );

		for( int i = 0; i < range.getRangeSize() ; i++ )
		{
			int address = range.getRangeBase() + i;
			CoreRegister coreRegister = newCore.getRegisterByPhysicalAddress( address );		

			getItems().add( coreRegister );

			coreRegister.addAccessListener( this );
		}
	}

	/**
	 * 
	 * @return
	 */
	public Core getCore()
	{
		return core.get();
	}

	/**
	 * 
	 * @return
	 */
	public SimpleObjectProperty<Core> coreProperty()
	{
		return core;
	}

	// ========================================================================
	// = 
	// ========================================================================

	@Override
	public void onAccessEvent( AccessEvent ev )
	{
		getItems()
			.stream()
				.filter( register -> register == ev.getSource() )
				.findAny()
					.ifPresent( register ->
					{
						refresh();

			        	getSelectionModel().select( register );
			        	scrollTo( register );
					} );
	}
}
