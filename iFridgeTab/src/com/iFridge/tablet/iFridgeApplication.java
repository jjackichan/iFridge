package com.iFridge.tablet;
import android.app.Application;

import com.parse.Parse;

public class iFridgeApplication extends Application {
	
	public void onCreate(){
		super.onCreate();
		
		Parse.initialize(this, "fXIMjogK4OsawEieTVPv4vjQ67xqoOa67henEG27", "plHI5I9bt5RJIgmEoWeOruc203uUOIJHtSrKRsIU"); 
	
	}

}