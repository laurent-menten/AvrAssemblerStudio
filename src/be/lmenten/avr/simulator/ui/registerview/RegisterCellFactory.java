package be.lmenten.avr.simulator.ui.registerview;

import java.util.Optional;

import be.lmenten.avr.core.CoreMemoryValue;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.descriptor.CoreRegisterDescriptor;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.util.Duration;

public class RegisterCellFactory<T>
	implements Callback<TableColumn<CoreRegister, T>, TableCell<CoreRegister, T>> 
{
	private static final Duration TOOLTIP_SHOW_DELAY
		= Duration.seconds( 0.33 );

	private final RegisterViewField field;

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @param field
	 */
	public RegisterCellFactory( RegisterViewField field )
	{
		this.field = field;
	}

	// ========================================================================
	// = 
	// ========================================================================

	@Override
	@SuppressWarnings("unchecked")
	public TableCell<CoreRegister, T> call( TableColumn<CoreRegister, T> p )
	{
		switch( field )
		{
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case ADDRESS:
				
				return (TableCell<CoreRegister, T>) new TableCell<CoreRegister,Integer>()
				{
					@Override
					protected void updateItem( Integer item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							return;
						}

						String text;
						CoreRegister register = this.getTableRow().getItem();
						if( register != null )
						{
							int address = register.getCellAddress();
							if( register.getType() == CoreRegisterType.IO )
							{
								text = String.format( "0x%04X (0x%02X)", address, address - 0x20 );
							}
							else
							{
								text = String.format( "0x%04X", address );					
							}
						}
						else
						{
							text = String.format( "0x%04X", item );
						}						

						setText( text );
						setAlignment( Pos.CENTER_LEFT );
					}

					// --------------------------------------------------------
					// - 
					// --------------------------------------------------------

					@Override
					public void startEdit()
					{
						super.startEdit();
						
						editRegister( this );

						cancelEdit();
					}

					@Override
					public void cancelEdit()
					{
						super.cancelEdit();
					}
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case NAME:

				return (TableCell<CoreRegister, T>) new TableCell<CoreRegister,String>()
				{
					@Override
					protected void updateItem( String item, boolean empty )
					{
						super.updateItem( item, empty );

						CoreRegister register = this.getTableRow().getItem();
						if( (register != null)
								&& (register.getType() != CoreRegisterType.RESERVED) )
						{
							CoreRegisterDescriptor rdesc = register.getRegisterDescriptor();
							if( (rdesc != null) )
							{
								String rName = rdesc.getName();
								String rDescription = rdesc.getDescription();
								String tooltipText = rName
									+ ((rDescription != null) ? " - " + rDescription : "")
									;
	
								Tooltip tooltip = new Tooltip( tooltipText );
								tooltip.setShowDelay( TOOLTIP_SHOW_DELAY );
								setTooltip( tooltip );
							}
						}
						else
						{
							Tooltip tooltip = new Tooltip( "Reserved" );
							tooltip.setShowDelay( TOOLTIP_SHOW_DELAY );
							setTooltip( tooltip );
						}

						setText( item );
						setAlignment( Pos.CENTER_LEFT );
					}

					// --------------------------------------------------------
					// - 
					// --------------------------------------------------------

					@Override
					public void startEdit()
					{
						super.startEdit();
						
						editRegister( this );

						cancelEdit();
					}

					@Override
					public void cancelEdit()
					{
						super.cancelEdit();
					}
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case VALUE:

				return (TableCell<CoreRegister, T>) new TableCell<CoreRegister,Integer>()
				{
					@Override
					protected void updateItem( Integer item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							return;
						}

						String text;
						CoreRegister register = this.getTableRow().getItem();
						if( register != null )
						{
							text = String.format( "0x%02X", register.getData() & 0xFF );
						}
						else
						{
							text = String.format( "0x%02X", item );
						}

						setText( text );
						setAlignment( Pos.CENTER );
					}

					// --------------------------------------------------------
					// - 
					// --------------------------------------------------------

					@Override
					public void startEdit()
					{
						super.startEdit();
						
						editRegister( this );

						cancelEdit();
					}

					@Override
					public void cancelEdit()
					{
						super.cancelEdit();
					}
				};

			default:
		}

		throw new RuntimeException( "Unhandled CoreRegister field type " + field );
	}

	// ========================================================================
	// = 
	// ========================================================================

	private void editRegister( TableCell<CoreRegister,?> cell )
	{
		CoreRegister register = cell.getTableRow().getItem();
		if( register != null )
		{
			RegisterEditorWindow dialog = new RegisterEditorWindow( register );
			Optional<CoreMemoryValue.Value> result = dialog.showAndWait();
			result.ifPresent( value ->
			{
				register.setValue( value );
			} );

		}

	}
}
