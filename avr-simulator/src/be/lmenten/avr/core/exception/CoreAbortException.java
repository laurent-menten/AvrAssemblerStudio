package be.lmenten.avr.core.exception;

import be.lmenten.avr.core.event.CoreEvent;

public class CoreAbortException
	extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------

	private final CoreEvent ev;

	// ========================================================================
	// === CONSTRUTOR(s) ======================================================
	// ========================================================================

	public CoreAbortException( CoreEvent ev )
	{
		this( ev, null, null );
	}

	public CoreAbortException( CoreEvent ev, String message )
	{
		this( ev, message, null );
	}

	public CoreAbortException( CoreEvent ev, Throwable cause )
	{		
		this( ev, null, cause );
	}

	public CoreAbortException( CoreEvent ev, String message, Throwable cause )
	{		
		super( message, cause );

		this.ev = ev;
	}

	// ========================================================================
	// === CONSTRUTOR(s) ======================================================
	// ========================================================================

	public CoreEvent getCoreEvent()
	{
		return ev;
	}
}
