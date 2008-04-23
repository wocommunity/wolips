package org.objectstyle.woproject.maven2.wobootstrap;

import org.apache.maven.plugin.logging.Log;

public class MockLog implements Log
{
	public int warningCount = 0;

	public void debug( CharSequence arg0 )
	{

	}

	public void debug( CharSequence arg0, Throwable arg1 )
	{

	}

	public void debug( Throwable arg0 )
	{

	}

	public void error( CharSequence arg0 )
	{

	}

	public void error( CharSequence arg0, Throwable arg1 )
	{

	}

	public void error( Throwable arg0 )
	{

	}

	public void info( CharSequence arg0 )
	{

	}

	public void info( CharSequence arg0, Throwable arg1 )
	{

	}

	public void info( Throwable arg0 )
	{

	}

	public boolean isDebugEnabled()
	{
		return false;
	}

	public boolean isErrorEnabled()
	{
		return false;
	}

	public boolean isInfoEnabled()
	{
		return false;
	}

	public boolean isWarnEnabled()
	{
		return false;
	}

	public void warn( CharSequence arg0 )
	{
		warningCount++;
	}

	public void warn( CharSequence arg0, Throwable arg1 )
	{

	}

	public void warn( Throwable arg0 )
	{

	}

}
