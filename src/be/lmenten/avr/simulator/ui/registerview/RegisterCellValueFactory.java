package be.lmenten.avr.simulator.ui.registerview;

import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.register.Register;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class RegisterCellValueFactory<T>
	implements Callback<CellDataFeatures<CoreRegister, T>, ObservableValue<T>>
{
	private final RegisterViewField field;

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @param field
	 */
	public RegisterCellValueFactory( RegisterViewField field )
	{
		this.field = field;
	}

	// ========================================================================
	// = 
	// ========================================================================

	@Override
	@SuppressWarnings("unchecked")
	public ObservableValue<T> call( CellDataFeatures<CoreRegister, T> p )
	{
		switch( field )
		{
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case ADDRESS:
			{
				return (ObservableValue<T>) new SimpleIntegerProperty( p.getValue().getCellAddress() );
			}

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case NAME:
				if( p.getValue().getType() == CoreRegisterType.RESERVED )
				{
					return (ObservableValue<T>) new SimpleStringProperty( "-" );
				}

				String name = p.getValue().getName();
				
				int address = p.getValue().getCellAddress();
				if( (address >= 0) && (address <= 31) )
				{
					Register reg = Register.lookup( address );

					String alias = reg.getAlias();
					if( alias != null )
					{
						name += " (" + alias + ")";
					}
				}

				return (ObservableValue<T>) new SimpleStringProperty( name );

				// ----------------------------------------------------------------
				// - 
				// ----------------------------------------------------------------

			case VALUE:
			{
				return (ObservableValue<T>) new SimpleIntegerProperty( p.getValue().getData() );
			}

			default:
		}

		throw new RuntimeException( "Unhandled CoreRegister field type " + field );
	}
}
