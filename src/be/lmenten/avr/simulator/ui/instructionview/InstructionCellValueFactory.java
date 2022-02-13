package be.lmenten.avr.simulator.ui.instructionview;

import be.lmenten.avr.core.instruction.Instruction;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class InstructionCellValueFactory<T>
	implements Callback<CellDataFeatures<Instruction, T>, ObservableValue<T>>
{
	private final InstructionViewField field;

	public InstructionCellValueFactory( InstructionViewField field )
	{
		this.field = field;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObservableValue<T> call( CellDataFeatures<Instruction, T> p )
	{
		switch( field )
		{
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case LABEL:
			{
				return (ObservableValue<T>) new SimpleStringProperty( "" );				
			}

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case BREAKPOINT:
			{
				return (ObservableValue<T>) new SimpleBooleanProperty( p.getValue().breakpointEnabled() );
			}

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

			case OPCODE:
			{
				return (ObservableValue<T>) new SimpleIntegerProperty( p.getValue().getOpcode() );
			}

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case MNEMONIC:
			{
				return (ObservableValue<T>) new SimpleObjectProperty<>( p.getValue().getInstructionSetEntry() );
			}

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case OPERANDS:
			{
				String text = null;

				String op1 = p.getValue().getOperand1( null );
				if( op1 != null )
				{
					text = op1;

					String op2 = p.getValue().getOperand2( null );
					if( op2 != null )
					{
						text += ", " + op2;
					}
				}

				return (ObservableValue<T>) new SimpleStringProperty( text );
			}

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case COMMENT:
			{
				return (ObservableValue<T>) new SimpleStringProperty( p.getValue().getComment( null ) );
			}

			default:
		}

		throw new RuntimeException( "Unhandled Instruction field type " + field );
	}
}
