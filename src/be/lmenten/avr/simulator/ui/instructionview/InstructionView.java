package be.lmenten.avr.simulator.ui.instructionview;

import java.util.logging.Logger;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.descriptor.CoreMemoryRange;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import be.lmenten.avr.core.instruction.flow.FlowInstruction;
import be.lmenten.avr.core.instruction.transfer.TransferInstruction;
import be.lmenten.utils.ui.fx.ConfirmBox;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class InstructionView
	extends TableView<Instruction>
{
	private SimpleObjectProperty<Core> core
		= new SimpleObjectProperty<Core>( null );

	// ------------------------------------------------------------------------

	private final ContextMenu contextMenu;

	private final MenuItem cmBreakpoint;
	private final MenuItem cmGoToTarget;
	private final MenuItem cmEditSymbol;
	private final MenuItem cmEditTargetSymbol;
	private final MenuItem cmInstructionDetails;
	private final MenuItem cmStatistics;

	// ========================================================================
	// = 
	// ========================================================================

	public InstructionView()
	{
		setRowFactory( new InstructionRowFactory<>() );

		// --------------------------------------------------------------------
		// - Columns ----------------------------------------------------------
		// --------------------------------------------------------------------

		// FIXME add breakpoint column

		TableColumn<Instruction,String> labelColumn = new TableColumn<>( "Label" );
		labelColumn.setMinWidth( 150 );
		labelColumn.setSortable( false );
		labelColumn.setSortable( false );
		labelColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.LABEL ) );
		labelColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.LABEL ) );
		getColumns().add( labelColumn );
				
		TableColumn<Instruction,Boolean> breakpointColumn = new TableColumn<>();
		breakpointColumn.setStyle( "-fx-alignment: CENTER;" );
		breakpointColumn.setMinWidth( 25 );
		breakpointColumn.setMaxWidth( 25 );
		breakpointColumn.setResizable( false );
		breakpointColumn.setSortable( false );
		breakpointColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.BREAKPOINT ) );
		breakpointColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.BREAKPOINT ) );
		getColumns().add( breakpointColumn );

		TableColumn<Instruction,Integer> addrColumn = new TableColumn<>( "Address" );
		addrColumn.setMinWidth( 95 );
		addrColumn.setSortable( false );
		addrColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.ADDRESS) );
		addrColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.ADDRESS ) );
		getColumns().add( addrColumn );

		TableColumn<Instruction,Integer> opcodeColumn = new TableColumn<>( "Opcode" );
		opcodeColumn.setMinWidth( 95 );
		opcodeColumn.setSortable( false );
		opcodeColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.OPCODE ) );
		opcodeColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.OPCODE ) );
		getColumns().add( opcodeColumn );

		TableColumn<Instruction,InstructionSet> instrColumn = new TableColumn<>( "Instruction" );
		instrColumn.setSortable( false );
		instrColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.MNEMONIC ) );
		instrColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.MNEMONIC ) );
		getColumns().add( instrColumn );

		TableColumn<Instruction,String> op1Column = new TableColumn<>( "Operands" );
		op1Column.setMinWidth( 150 );
		op1Column.setSortable( false );
		op1Column.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.OPERANDS ) );
		op1Column.setCellFactory( new InstructionCellFactory<>( InstructionViewField.OPERANDS ) );
		getColumns().add( op1Column );

		TableColumn<Instruction,String> commentColumn = new TableColumn<>( "Comment" );
		commentColumn.setMinWidth( 350 );
		commentColumn.setSortable( false );
		commentColumn.setCellValueFactory( new InstructionCellValueFactory<>( InstructionViewField.COMMENT ) );
		commentColumn.setCellFactory( new InstructionCellFactory<>( InstructionViewField.COMMENT ) );
		getColumns().add( commentColumn );

		// --------------------------------------------------------------------
		// - Context menu - create --------------------------------------------
		// --------------------------------------------------------------------

		cmBreakpoint = new MenuItem( "Enable breakpoint" );

		cmBreakpoint.setOnAction( (ev) ->
		{
			Instruction instr = InstructionView.this.getSelectionModel().getSelectedItem();
			if( instr != null )
			{
				instr.setBreakpoint( ! instr.breakpointEnabled() );
				refresh();
			}
		} );
		
		// --------------------------------------------------------------------

		cmGoToTarget = new MenuItem( "Go to target" );

		cmGoToTarget.setOnAction( (ev) ->
		{
			Instruction instr = InstructionView.this.getSelectionModel().getSelectedItem();
			if( (instr != null) && (instr instanceof FlowInstruction) )
			{
				FlowInstruction finstr = (FlowInstruction)instr;

				Instruction target = core.get().getFlashCell( finstr.getTargetAddress() );
				if( target != null )
				{
					getItems()
						.stream()
						.filter( candidate -> candidate == target )
						.findAny()
						.ifPresent( elected ->
						{
							getSelectionModel().select( elected );
							scrollTo( elected );
						} );
				}
				else
				{
					ConfirmBox.display( "Ooops!",  "I won't jump into the void" );
				}
			}			
		} );

		// --------------------------------------------------------------------

		cmEditSymbol = new MenuItem( "Edit symbol" );

		cmEditSymbol.setOnAction( (ev) ->
		{
			LOG.severe( "Edit symbol not yet implemented" );
		} );

		// --------------------------------------------------------------------

		cmEditTargetSymbol = new MenuItem( "Edit target symbol" );

		cmEditTargetSymbol.setOnAction( (ev) ->
		{
			LOG.severe( "Edit target symbol not yet implemented" );			
		} );

		// --------------------------------------------------------------------

		cmInstructionDetails = new MenuItem( "Instruction details" );

		cmInstructionDetails.setOnAction( (ev) ->
		{
			Instruction instr = InstructionView.this.getSelectionModel().getSelectedItem();
			if( instr != null )
			{
				InstructionDetailsWindow window = new InstructionDetailsWindow( instr );
				window.initOwner( getScene().getWindow() );
				window.showAndWait();
			}
		} );

		// --------------------------------------------------------------------

		cmStatistics = new MenuItem( "Statistics" );

		// --------------------------------------------------------------------
		// - Context menu - install -------------------------------------------
		// --------------------------------------------------------------------

		contextMenu = new ContextMenu();

		contextMenu.getItems().add( cmBreakpoint );
		contextMenu.getItems().add( cmGoToTarget );
		contextMenu.getItems().add( new SeparatorMenuItem() );
		contextMenu.getItems().add( cmEditSymbol );
		contextMenu.getItems().add( cmEditTargetSymbol );
		contextMenu.getItems().add( new SeparatorMenuItem() );
		contextMenu.getItems().add( cmInstructionDetails );
		contextMenu.getItems().add( cmStatistics );

		getItems().addListener( new InvalidationListener()
		{
			@Override
			public void invalidated( Observable observable )
			{
				if( getItems().size() == 0 )
				{
						setContextMenu( null );				
					}
					else
					{
						setContextMenu( contextMenu );				
					}
				}
		} );

		setOnContextMenuRequested( (ev) ->
		{
			Instruction instr = InstructionView.this.getSelectionModel().getSelectedItem();
			if( instr != null )
			{
				if( instr instanceof FillerInstruction )
				{
					cmBreakpoint.setDisable( true );
					cmGoToTarget.setDisable( true );
					cmEditSymbol.setDisable( true );
					cmEditTargetSymbol.setDisable( true );
					cmInstructionDetails.setDisable( true );
					cmStatistics.setDisable( true );
				}
				else
				{
					if( instr.breakpointEnabled() )
					{
						cmBreakpoint.setDisable( false );
						cmBreakpoint.setText( "Disable breakpoint" );
					}
					else
					{
						cmBreakpoint.setDisable( false );
						cmBreakpoint.setText( "Enable breakpoint" );
					}
					
					if( instr instanceof FlowInstruction )
					{
						cmGoToTarget.setDisable( false );					
					}
					else
					{
						cmGoToTarget.setDisable( true );
					}

					cmEditSymbol.setDisable( false );

					if( (instr instanceof TransferInstruction) || (instr instanceof FlowInstruction) )
					{
						cmEditTargetSymbol.setDisable( false );
					}
					else
					{
						cmEditTargetSymbol.setDisable( true );
					}

					cmInstructionDetails.setDisable( false );

					cmStatistics.setDisable( false );
				}
			}
		} );
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

		CoreMemoryRange range
			= newCore.getDescriptor()
				.getMemoryRange( CoreMemory.FLASH, CoreMemoryRange.FLASH_RANGE_NAME );

//		for( int i = 0 ; i < 10 ; i++ )
//		{
//			getItems().add( new FillerInstruction( -1, -1 ) );
//		}

		// --------------------------------------------------------------------

		for( int i = 0 ; i < range.getRangeSize() ; )
		{
			int address = range.getRangeBase() + i;

			Instruction instruction = newCore.getFlashCell( address );
			if( instruction != null )
			{
				getItems().add( instruction );

				if( instruction.is32bits() )
				{
					i += 2;
				}

				i += 2;
			}
			else
			{
				int address2 = 0;
				while( i < range.getRangeSize() )
				{
					address2 = range.getRangeBase() + i;

					if( newCore.getFlashCell( address2 ) != null )
					{
						break;
					}

					i += 2;
				};

				getItems().add( new FillerInstruction( address, address2 - address ) );
			}
		}	

		// --------------------------------------------------------------------

//		for( int i = 0 ; i < 10 ; i++ )
//		{
//			getItems().add( new FillerInstruction( -1, -1 ) );
//		}
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

	public void showInstruction( Instruction instruction )
	{
		if( instruction != null )
		{
			getItems()
				.stream()
					.filter( instr -> instr == instruction )
					.findAny()
						.ifPresent( instr ->
						{
							getSelectionModel().select( instr );
							scrollTo( instr );
					} );
		}		
	}

	// ========================================================================
	// = Utilities ============================================================
	// ========================================================================

	private static final Logger LOG
		= Logger.getLogger( InstructionView.class.getName() );
}
