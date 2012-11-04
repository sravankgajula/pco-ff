package net.udsholt.peytz.firmafon.domain;

public class Reception 
{
	public int id          = 0;
	public String name     = "";
	public String number   = "";
	public boolean isCloak = false;
	
	public Reception()
	{
	}
	
	public Reception(int id, String name, String number)
	{
		this.id = id;
		this.name = name;
		this.number = number;
	}
}
