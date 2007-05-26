/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 - 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowlegement: "This product includes software developed by the ObjectStyle
 * Group (http://objectstyle.org/)." Alternately, this acknowlegement may
 * appear in the software itself, if and wherever such third-party
 * acknowlegements normally appear. 4. The names "ObjectStyle Group" and
 * "Cayenne" must not be used to endorse or promote products derived from this
 * software without prior written permission. For written permission, please
 * contact andrus@objectstyle.org. 5. Products derived from this software may
 * not be called "ObjectStyle" nor may "ObjectStyle" appear in their names
 * without prior written permission of the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/> .
 *  
 */
package org.objectstyle.wolips.baseforplugins.util;

/**
 * Throttle provides a mechanism for throttling back the execution of 
 * recurring events.  Construct a Throttle with a Runnble to execute, .start() 
 * it, and .ping() it when events occur. After the specified sleepTime after
 * the last .ping() that has occurred, the throttle will execute  
 * 
 * @author mschrag
 *
 */
public class Throttle implements Runnable {
	private Thread _throttleThread;

	private boolean _running;

	private boolean _stopRequested;

	private int _pingCount;

	private long _sleepTime;

	private Runnable _runnable;

	public Throttle(long sleepTime, Runnable runnable) {
		_sleepTime = sleepTime;
		_runnable = runnable;
	}

	public synchronized int pingCount() {
		return _pingCount;
	}
	
	public synchronized void ping() {
		_pingCount++;
		notifyAll();
	}

	public synchronized void start() {
		if (_throttleThread == null) {
			_throttleThread = new Thread(this);
			_throttleThread.start();
			while (!_running) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized void stop() {
		if (_throttleThread != null) {
			_stopRequested = true;
			while (_running) {
				notifyAll();
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			_stopRequested = false;
			_throttleThread = null;
		}
	}

	public void run() {
		synchronized (this) {
			_running = true;
			notifyAll();
		}
		boolean running = true;
		while (running) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (_pingCount > 0) {
				int pingCount;
				synchronized (this) {
					pingCount = _pingCount;
				}
				try {
					Thread.sleep(_sleepTime);
				} catch (InterruptedException e) {
					// ignore
				}
				synchronized (this) {
					if (pingCount == _pingCount) {
						try {
							_runnable.run();
						} catch (Throwable t) {
							t.printStackTrace();
						}
						_pingCount = 0;
					}
				}
			}
			synchronized (this) {
				running = !_stopRequested;
			}
		}
		synchronized (this) {
			_running = false;
			notifyAll();
		}
	}
}