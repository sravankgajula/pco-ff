package net.udsholt.peytz.firmafon.async;

public interface IAsyncTask 
{
	public abstract void onProcess();
	 
    public abstract void onComplete();
}
