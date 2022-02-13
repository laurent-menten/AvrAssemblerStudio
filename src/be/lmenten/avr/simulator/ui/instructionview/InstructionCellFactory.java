package be.lmenten.avr.simulator.ui.instructionview;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.descriptor.CoreMemory;
import be.lmenten.avr.core.instruction.DataInstruction;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.util.Duration;

public class InstructionCellFactory<T>
	implements Callback<TableColumn<Instruction, T>, TableCell<Instruction, T>> 
{
	private static final Duration TOOLTIP_SHOW_DELAY
		= Duration.seconds( 0.33 );

	private final InstructionViewField field;

	// ========================================================================
	// = 
	// ========================================================================

	/**
	 * 
	 * @param field
	 */
	public InstructionCellFactory( InstructionViewField field )
	{
		this.field = field;
	}

	// ========================================================================
	// = 
	// ========================================================================

	@Override
	@SuppressWarnings("unchecked")
	public TableCell<Instruction, T> call( TableColumn<Instruction, T> p )
	{
		switch( field )
		{
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case LABEL:

				return (TableCell<Instruction, T>) new TableCell<Instruction,String>()
				{
					@Override
					protected void updateItem( String item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Core core = ((InstructionView)this.getTableRow().getTableView()).getCore();
						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction instanceof FillerInstruction )
							{
								text = null;
							}
							else
							{
								text = core.getSymbol( CoreMemory.FLASH, instruction.getCellAddress() );
							}
						}

						setText( text );
					}
				};
			
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case BREAKPOINT:
				return (TableCell<Instruction, T>) new TableCell<Instruction,Boolean>()
				{
					@Override
					protected void updateItem( Boolean item, boolean empty )
					{
						super.updateItem(item, empty);

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction.breakpointEnabled() )
							{
								text = "\u26D4";
							}
							else
							{
								text = "";
							}
						}

						setText( text );
					}
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case ADDRESS:

				return (TableCell<Instruction, T>) new TableCell<Instruction,Integer>()
				{
					@Override
					protected void updateItem( Integer item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction.getCellAddress() != -1 )
							{
								text = String.format( "0x%06X", instruction.getCellAddress() );
							}
						}

						setText( text );
					}	
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case OPCODE:

				return (TableCell<Instruction, T>) new TableCell<Instruction,Integer>()
				{
					@Override
					protected void updateItem( Integer item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction instanceof FillerInstruction )
							{
								text = null;
							}

							else if( instruction.is32bits() )
							{
								text = String.format( "%04X %04X",
									instruction.getOpcode(),
									instruction.getSecondWord().getOpcode()
								);
							}
							else
							{
								text = String.format( "%04X",
									instruction.getOpcode()
								);
							}
						}

						setText( text );
					}
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case MNEMONIC:

				return (TableCell<Instruction, T>) new TableCell<Instruction,InstructionSet>()
				{
					@Override
					protected void updateItem( InstructionSet item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction instanceof FillerInstruction )
							{
								text = null;

								Tooltip tooltip = new Tooltip( "Uninitialised space" );
								tooltip.setShowDelay( TOOLTIP_SHOW_DELAY );
								setTooltip( tooltip );
							}

							else if( instruction instanceof DataInstruction )
							{
								text = "DATA";

								Tooltip tooltip = new Tooltip( "DataInstruction" );
								tooltip.setShowDelay( TOOLTIP_SHOW_DELAY );
								setTooltip( tooltip );
							}

							else
							{
								text = instruction.getMnemonic();

								String tooltipText = instruction.getDescription();
								Tooltip tooltip = new Tooltip( tooltipText );
								tooltip.setShowDelay( TOOLTIP_SHOW_DELAY );
								setTooltip( tooltip );
							}
						}

						setText( text );
					}
				};

			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case OPERANDS:

				return (TableCell<Instruction, T>) new TableCell<Instruction,String>()
				{
					@Override
					protected void updateItem( String item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Core core = ((InstructionView)this.getTableRow().getTableView()).getCore();
						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							if( instruction instanceof FillerInstruction )
							{
								text = null;
							}
							else
							{
								String op1 = instruction.getOperand1( core );
								if( op1 != null )
								{
									text = op1;

									String op2 = instruction.getOperand2( core );
									if( op2 != null )
									{
										text += ", " + op2;
									}
								}
							}
						}

						setText( text );
					}
				};
					
			// ----------------------------------------------------------------
			// - 
			// ----------------------------------------------------------------

			case COMMENT:

				return (TableCell<Instruction, T>) new TableCell<Instruction,String>()
				{
					@Override
					protected void updateItem( String item, boolean empty )
					{
						super.updateItem( item, empty );

						if( empty || (item == null) )
						{
							setText( null );
							return;
						}

						String text = null;

						Core core = ((InstructionView)this.getTableRow().getTableView()).getCore();
						Instruction instruction = this.getTableRow().getItem();
						if( instruction != null )
						{
							text = instruction.getComment( core );
						}

						setText( text );
					}
				};

			default:
		}

		throw new RuntimeException( "Unhandled Instruction field type " + field );
	}
}
