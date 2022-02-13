package be.lmenten.avr.simulator.ui.registerview;

import be.lmenten.avr.core.CoreMemoryValue;
import be.lmenten.avr.core.data.CoreRegister;
import be.lmenten.avr.core.data.CoreRegisterType;
import be.lmenten.avr.core.descriptor.CoreRegisterDescriptor;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class RegisterEditorWindow
	extends Dialog<CoreMemoryValue.Value>
{
	public RegisterEditorWindow( CoreRegister register )
	{
		CoreRegisterDescriptor rdesc = register.getRegisterDescriptor();
		CoreMemoryValue.Value value = register.getValue();

		if( register.getType() != CoreRegisterType.RESERVED )
		{
			if( rdesc.getType() != CoreRegisterType.RESERVED )
			{
				setTitle( rdesc.getName() );
			}

			if( rdesc.getDescription() != null )
			{
				setHeaderText( rdesc.getDescription() );
			}
		}
		else
		{
			setTitle( "Reserved" );

			setHeaderText( "Warning!!!\n"
				+ "This register is not assigned in "
					+ rdesc.getCoreDescriptor().getPartName() );
		}

		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------

		GridPane content = new GridPane();
		content.setPadding( new Insets( 10, 20, 10, 20 ) );
		content.setHgap( 8 );
		content.setVgap( 4 );

		for( int i = 7 ; i>= 0 ; i-- )
		{
			CheckBox checkbox = new CheckBox();
			checkbox.setSelected( value.bit(i) );

			final int bit = i;
			checkbox.setOnAction( ev ->
			{
				value.bit( bit, checkbox.isSelected() );
			} );

			content.add( checkbox, 0, 7-i );

			// ----------------------------------------------------------------

			if( (rdesc != null) && rdesc.hasBitNames() )
			{
				String bitName = rdesc.getBitName(i);
				if( bitName != null )
				{
					content.add( new Label(bitName), 1, 7-i );
				}
				else
				{
					content.add( new Label( "-" ), 1, 7-i );					
				}

				String bitDescription = rdesc.getBitDescription(i);
				if( bitDescription != null )
				{
					content.add( new Label(bitDescription), 2, 7-i );
				}
			}
			else
			{
				content.add( new Label( "Bit #" + i ), 1, 7-i );									
			}
		}

		getDialogPane().setContent( content );
		
		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------

		getDialogPane().getButtonTypes().addAll( ButtonType.OK, ButtonType.CANCEL );

		setResultConverter( button ->
		{
			if( button == ButtonType.OK )
			{
				return value;
			}

			return null;
		} );
	}
}
