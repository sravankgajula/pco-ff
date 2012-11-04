package net.udsholt.peytz.firmafon.api;

public class ApiExpception extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public ApiExpception(final String message) 
	{
		super(message);
	}
}
