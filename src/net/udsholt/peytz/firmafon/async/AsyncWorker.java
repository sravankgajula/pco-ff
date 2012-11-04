package net.udsholt.peytz.firmafon.async;

import android.os.AsyncTask;

public class AsyncWorker extends AsyncTask<IAsyncTask, Integer, Boolean> 
{
    protected IAsyncTask[] mParams;
    
    @Override
    protected Boolean doInBackground(IAsyncTask... pParams) 
    {
    	mParams = pParams;
    	
        for(int i = 0; i < mParams.length; i++) {
        	mParams[i].onProcess();
        }
        
        return true;
        
        
    }
 
    @Override
    protected void onPostExecute(Boolean result) 
    {
        for(int i = 0; i < mParams.length; i++) {
        	mParams[i].onComplete();
        }
    }

}
