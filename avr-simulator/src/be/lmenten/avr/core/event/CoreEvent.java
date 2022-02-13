package be.lmenten.avr.core.event;

import be.lmenten.avr.core.Core;
import be.lmenten.avr.core.instruction.Instruction;
import be.lmenten.avr.core.instruction.InstructionSet;

public class CoreEvent
{
	private final CoreEventType type;
	private final Core core;
	private final long clockCyclesCount;
	private final int programCounter;

	private Instruction instruction;
	private InstructionSet instructionSetEntry;
	private Object oldValue;
	private Object newValue;
	private int value;
	private int targetAddress;

	private boolean shouldAbort;
	private boolean veto;

	// ========================================================================
	// === CONSTRUTOR(s) ======================================================
	// ========================================================================

	public CoreEvent( CoreEventType type, Core core )
	{
		this.type = type;
		this.core = core;
		this.clockCyclesCount = core.getClockCyclesCounter();
		this.programCounter = core.getProgramCounter();

		this.shouldAbort = false;
		this.veto = false;
	}

	// ========================================================================
	// ===
	// ========================================================================

	public CoreEventType getEventType()
	{
		return type;
	}

	public Core getCore()
	{
		return core;
	}

	public long getClockCyclesCount()
	{
		return clockCyclesCount;
	}

	public int getProgramCounter()
	{
		return programCounter;
	}

	// ------------------------------------------------------------------------

	public void setInstruction( Instruction instruction )
	{
		this.instruction = instruction;
	}

	public Instruction getInstruction()
	{
		return instruction;
	}

	public void setInstructionSetEntry( InstructionSet instructionSetEntry )
	{
		this.instructionSetEntry = instructionSetEntry;
	}

	public InstructionSet getInstructionSetEntry()
	{
		return instructionSetEntry;
	}

	// ------------------------------------------------------------------------

	public void setOldValue( Object oldValue )
	{
		this.oldValue = oldValue;
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	public void setNewValue( Object newValue )
	{
		this.newValue = newValue;
	}

	public Object getNewValue()
	{
		return newValue;
	}

	public void setValue( int value )
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public void setTargetAddress( int targetAddress )
	{
		this.targetAddress = targetAddress;
	}

	public int getTargetAddress()
	{
		return targetAddress;
	}

	// ------------------------------------------------------------------------

	/**
	 * 
	 */
	public void abort()
	{
		this.shouldAbort = true;
	}

	/**
	 * 
	 * @return
	 */
	public boolean shouldAbort()
	{
		return shouldAbort;
	}

	/**
	 * 
	 */
	public void veto()
	{
		this.veto = true;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasVeto()
	{
		return veto;
	}
}
