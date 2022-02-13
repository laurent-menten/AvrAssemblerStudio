package be.lmenten.avr.core.instruction;

import java.io.File;
import java.io.PrintStream;

public final class InstructionSetDebug
{
	/**
	 * Dump instruction set (as ordered by declaration).
	 * 
	 * @param out
	 */
	public static void dumpDeclarationOrdered( PrintStream out )
	{
		out.println( " mnem.    opc.    mask   sz.   structure              sreg     description "   );
		out.println( "--------+-------+------+-----+----------------------+--------+-------------" );

		for( InstructionSet i : InstructionSet.values() )
		{
			out.println( i );
		}
	}

	/**
	 * Dump instruction set (as ordered by declaration).
	 * 
	 * @param file
	 */
	public static void dumpDeclarationOrdered( String file )
	{
		try( PrintStream out = new PrintStream( new File( file ) ) )
		{
			dumpDeclarationOrdered( out );			
			out.close();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Dump instruction set (as ordered by declaration).
	 */
	public static void dumpDeclarationOrdered()
	{
		dumpDeclarationOrdered( System.out );
	}

	// ------------------------------------------------------------------------

	/**
	 * Dump instruction set (as ordered for the disassembler).
	 * 
	 * @param out
	 */
	public static void dumpDisassemblerOrdered( PrintStream out )
	{
		out.println( " mnem.    opc.    mask   sz.   structure              sreg     description "   );
		out.println( "--------+-------+------+-----+----------------------+--------+-------------" );

		for( InstructionSet i : InstructionSet.getDisassemblerList() )
		{
			out.println( i );
		}
	}

	/**
	 * Dump instruction set (as ordered for the disassembler).
	 * 
	 * @param file
	 */
	public static void dumpDisassemblerOrdered( String file )
	{
		try( PrintStream out = new PrintStream( file ) )
		{
			dumpDisassemblerOrdered( out );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Dump instruction set (as ordered for the disassembler).
	 */
	public static void dumpDisassemblerOrdered()
	{
		dumpDisassemblerOrdered( System.out );
	}
	
	// ------------------------------------------------------------------------

	/**
	 * Generate all possible instructions (opcode 0x0000 -> 0xFFFF).
	 * 
	 * @param out
	 */
	public static void fullTest( PrintStream out )
	{
		dumpDeclarationOrdered( out );

		dumpDisassemblerOrdered( out );

		for( int opcode = 0 ; opcode <= 0xFFFF ; opcode++ )
		{
			InstructionSet entry = InstructionSet.lookup( opcode );
			if( entry != null )
			{
				Instruction instruction = entry.newInstance( opcode, 0 );

				if( instruction.getOpcode() != opcode )
				{
					out.println( "Invalid opcode : " + instruction + "   - "
						+ String.format( "%04X != %04X", opcode, instruction.getOpcode() ));
				}

				if( instruction.is32bits() )
				{
					if( instruction.getSecondWord().getOpcode() != 0 )
					{
						out.println( "Invalid second opcode : " + instruction + "   - "
							+ String.format( "%04X != %04X", opcode, instruction.getOpcode() ));
					}
				}
			}
		}
	}

	/**
	 * Generate all possible instructions (opcode 0x0000 -> 0xFFFF).
	 * 
	 * @param file
	 */
	public static void fullTest( String file )
	{
		try( PrintStream out = new PrintStream( file ) )
		{
			fullTest( out );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Generate all possible instructions (opcode 0x0000 -> 0xFFFF).
	 */
	public static void fullTest()
	{
		fullTest( System.out );
	}
}
