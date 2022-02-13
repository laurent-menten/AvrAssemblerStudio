package be.lmenten.avr.simulator.ui.registerview;

import be.lmenten.avr.core.data.CoreRegister;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class RegisterTableRowFactory<T>
	implements Callback<TableView<CoreRegister>, TableRow<CoreRegister>>
{
	@Override
	public TableRow<CoreRegister> call( TableView<CoreRegister> param )
	{
		return new TableRow<CoreRegister>()
		{		
			@Override
			protected void updateItem( CoreRegister item, boolean empty )
			{
				super.updateItem(item, empty);
			}
		};
	}

}
