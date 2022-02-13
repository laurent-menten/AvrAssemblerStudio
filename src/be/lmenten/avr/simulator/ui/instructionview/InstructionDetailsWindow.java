package be.lmenten.avr.simulator.ui.instructionview;

import java.util.Map.Entry;

import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.OperandType;
import be.lmenten.avr.core.instruction.flow.CALL;
import be.lmenten.avr.core.instruction.flow.JMP;
import be.lmenten.avr.core.instruction.transfer.LDS;
import be.lmenten.avr.core.instruction.transfer.STS;
import be.lmenten.utils.ui.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class InstructionDetailsWindow
	extends Dialog<Void>
{
	public InstructionDetailsWindow( Instruction instruction )
	{
		InstructionSet entry = instruction.getInstructionSetEntry();

		setTitle( "Instruction: " + entry.name() );

		setHeaderText( entry.getDescription() );

		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------

		GridPane content = new GridPane();
		content.setPadding( new Insets( 10, 20, 10, 20 ) );
		content.setHgap( 8 );
		content.setVgap( 4 );

		int row = 0;

		// --------------------------------------------------------------------
		// - Display opcode (16 bits) -----------------------------------------
		// --------------------------------------------------------------------

		Label [] bits = new Label [16];
		HBox opcode = new HBox();

		for( int i = 15 ; i >= 0 ; i-- )
		{
			bits[i] = new Label();
			bits[i].setText( instruction.bit(i) ? " 1 " : " 0 " );
			opcode.getChildren().add( bits[i] );

			if( (i % 4) == 0 )
			{
				opcode.getChildren().add( new Label( "   " ) );
			}
		}

		content.add( opcode, 0, row );
		GridPane.setColumnSpan( opcode, 4 );
		row++;

		// --------------------------------------------------------------------
		// - Display second word (32 bits) ------------------------------------
		// --------------------------------------------------------------------

		if( instruction.is32bits() )
		{
			Label [] bits32 = new Label [16];
			HBox opcode32 = new HBox();

			for( int i = 15 ; i >= 0 ; i-- )
			{
				bits32[i] = new Label();
				bits32[i].setText( instruction.getSecondWord().bit(i) ? " 1 " : " 0 " );
				FXUtils.addStyle( bits32[i], "-fx-background-color", OperandType.k.getColor() );
				opcode32.getChildren().add( bits32[i] );

				if( (i % 4) == 0 )
				{
					opcode32.getChildren().add( new Label( "   " ) );
				}
			}

			content.add( opcode32, 0, row );
			GridPane.setColumnSpan( opcode32, 4 );			
			row++;
		}

		// --------------------------------------------------------------------

		Separator separator = new Separator();
		content.add( separator, 0, row );
		GridPane.setColumnSpan( separator, 4 );
		row++;

		// --------------------------------------------------------------------
		// - Display opcode value ---------------------------------------------	
		// --------------------------------------------------------------------

		Label colorLegend0 = new Label( " � " );
		content.add( colorLegend0, 0, row );

		Label description0 = new Label( "Instruction opcode" );
		content.add( description0, 2, row );

		Label value0 = new Label(  String.format( "0x%04X", entry.getOpcodeMaskValue() ) );
		content.add( value0, 3, row );

		row++;

		// --------------------------------------------------------------------
		// - Process operands -------------------------------------------------
		// --------------------------------------------------------------------

		for( Entry<OperandType, Integer> operand : entry.getOperands() )
		{
			String color = operand.getKey().getColor();

			// ----------------------------------------------------------------

			if( operand.getKey() == OperandType.x )
			{
				continue;
			}

			// ----------------------------------------------------------------

			for( int i = 15 ; i >= 0 ; i-- )
			{
				if( (operand.getValue() & (1<<i)) == (1<<i) )
				{
					FXUtils.addStyle( bits[i], "-fx-background-color", color );
				}
			}			

			// ----------------------------------------------------------------

			if( ! instruction.is32bits() || (operand.getKey() != OperandType.k) )
			{
				Label colorLegend = new Label( " � " );
				FXUtils.addStyle( colorLegend, "-fx-background-color", color );
				content.add( colorLegend, 0, row );
	
				Label code = new Label(  operand.getKey().getCode() );
				content.add( code, 1, row );
				
				Label description = new Label(  operand.getKey().getDescription() );
				content.add( description, 2, row );
	
				int op = instruction.getInstructionSetEntry().extractOperand( operand.getKey(), instruction.getOpcode() );
	
				Label value = new Label(  String.format( "0x%02X", op ) );
				content.add( value, 3, row );
	
				row++;
			}
		}

		if( instruction.is32bits() )
		{
			Label colorLegend = new Label( " � " );
			FXUtils.addStyle( colorLegend, "-fx-background-color", OperandType.k.getColor() );
			content.add( colorLegend, 0, row );

			Label code = new Label(  OperandType.k.getCode() );
			content.add( code, 1, row );
			
			Label description = new Label(  OperandType.k.getDescription() );
			content.add( description, 2, row );

			int op = 0;
			if( instruction instanceof LDS )
			{
				op = ((LDS)instruction).getAddress();
			}
			else if( instruction instanceof STS )
			{
				op = ((STS)instruction).getAddress();
			}
			else if( instruction instanceof CALL )
			{
				op = ((CALL)instruction).getAddress();
			}
			else if( instruction instanceof JMP )
			{
				op = ((JMP)instruction).getAddress();
			}

			// FIXME correct with core

			Label value = new Label(  String.format( "0x%06X", op ) );
			content.add( value, 3, row );

			row++;
		}

		// --------------------------------------------------------------------
 
		separator = new Separator();
		content.add( separator, 0, row );
		GridPane.setColumnSpan( separator, 4 );
		row++;

		// --------------------------------------------------------------------
		// - Show status register impact --------------------------------------
		// --------------------------------------------------------------------

		Label text;

		text = new Label();
		text.setText( "Syntax : " + instruction.getSyntax() );
		content.add( text, 0, row );
		GridPane.setColumnSpan( text, 4 );
		row++;

		// --------------------------------------------------------------------

		text = new Label( "Status register : " + entry.getRawStatusRegister() );
		content.add( text, 0, row );
		GridPane.setColumnSpan( text, 4 );
		row++;

		// --------------------------------------------------------------------

		if( entry.getRemark() != null )
		{
			text = new Label( "Remark : " + entry.getRemark() );
			content.add( text, 0, row );
			GridPane.setColumnSpan( text, 4 );
			row++;
		}

		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------

		getDialogPane().setContent( content );

		// --------------------------------------------------------------------
		// - 
		// --------------------------------------------------------------------

		getDialogPane().getButtonTypes().addAll( ButtonType.OK );

		setResultConverter( button ->
		{
			return null;
		} );
	}
}
