package org.objectstyle.wolips.womodeler.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Webserver implements Runnable {
  private static final String DEFAULT_REQUEST_HANDLER = "___DEFAULT___";

  private boolean _running;
  private ServerSocket _serverSocket;
  private int _port;
  private Map<String, IRequestHandler> _pathToRequestHandler;

  public Webserver(int port) {
    _port = port;
    _pathToRequestHandler = new HashMap<String, IRequestHandler>();
  }

  public void addRequestHandler(String path, IRequestHandler requestHandler) {
    _pathToRequestHandler.put(path, requestHandler);
  }

  public void removeRequestHandler(String path) {
    _pathToRequestHandler.remove(path);
  }

  public InetAddress getHostAddress() throws IOException {
    return InetAddress.getLocalHost();
  }

  public int getPort() {
    return _port;
  }

  public synchronized void start(boolean daemon) {
    if (!_running) {
      _running = true;
      Thread serverThread = new Thread(this);
      serverThread.setDaemon(daemon);
      serverThread.start();
    }
  }

  public synchronized void stop() {
    try {
      if (_serverSocket != null) {
        _serverSocket.close();
      }
      _running = false;
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void run() {
    try {
      Iterator<IRequestHandler> requestHandlers = _pathToRequestHandler.values().iterator();
      while (requestHandlers.hasNext()) {
        IRequestHandler requestHandler = requestHandlers.next();
        requestHandler.init(this);
      }

      _serverSocket = new ServerSocket(_port);
      while (_running) {
        Socket requestSock = _serverSocket.accept();
        Request req = new Request(this, requestSock);
        //				req.run();
        Thread requestThread = new Thread(req);
        requestThread.start();
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public IRequestHandler getHandler(Request request) {
    String path = request.getPath();
    IRequestHandler requestHandler = _pathToRequestHandler.get(path);
    if (requestHandler == null) {
      requestHandler = _pathToRequestHandler.get(Webserver.DEFAULT_REQUEST_HANDLER);
    }
    return requestHandler;
  }
}
