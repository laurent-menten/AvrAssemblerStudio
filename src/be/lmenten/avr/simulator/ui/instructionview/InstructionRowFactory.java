package be.lmenten.avr.simulator.ui.instructionview;

import be.lmenten.avr.core.instruction.Instruction;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class InstructionRowFactory<T>
	implements Callback<TableView<Instruction>, TableRow<Instruction>>
{
	@Override
	public TableRow<Instruction> call( TableView<Instruction> param )
	{
		return new TableRow<Instruction>()
		{		
			@Override
			protected void updateItem( Instruction item, boolean empty )
			{
				super.updateItem(item, empty);
			}
		};
	}

}
