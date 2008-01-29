package org.objectstyle.wolips.womodeler.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.objectstyle.wolips.preferences.PreferencesPlugin;
import org.objectstyle.wolips.womodeler.preferences.PreferenceConstants;

public class Request implements Runnable {
  public static final String METHOD_GET = "GET";
  public static final String METHOD_POST = "POST";

  public static final String CONTENT_LENGTH = "content-length";

  private Webserver _server;
  private Socket _socket;
  private OutputStream _outputStream;
  private String _method;
  private String _pathAndQueryString;
  private String _path;
  private String _queryString;
  private String _content;
  private Map<String, String> _queryParameters;
  private Map<String, String> _requestHeaders;
  private Map<String, String> _responseHeaders;
  private int _responseCode;
  private ByteArrayOutputStream _responseStream;

  public Request(Webserver server, Socket socket) {
    _server = server;
    _socket = socket;
    _responseStream = new ByteArrayOutputStream();
    _responseCode = 200;
  }

  public void run() {
    //System.out.println("Request.run: request " + hashCode() + " begin");
    try {
      _responseHeaders = new HashMap<String, String>();
      InputStream is = _socket.getInputStream();
      _outputStream = _socket.getOutputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String request = br.readLine();

      boolean inputDone = false;
      while (!inputDone) {
        String line = br.readLine();
        if (line == null) {
          inputDone = true;
        }
        else if ("".equals(line)) {
          inputDone = true;
        }
        else {
          int colonIndex = line.indexOf(':');
          if (colonIndex != -1) {
            if (_requestHeaders == null) {
              _requestHeaders = new HashMap<String, String>();
            }
            String key = line.substring(0, colonIndex).trim().toLowerCase();
            String value = line.substring(colonIndex + 1).trim().toLowerCase();
            _requestHeaders.put(key, value);
          }
        }
      }

      String[] requestElements = request.split(" ");
      _method = requestElements[0].toUpperCase();
      if (Request.METHOD_GET.equalsIgnoreCase(_method)) {
        _pathAndQueryString = (requestElements[1].startsWith("/") ? "" : "/") + requestElements[1];
        parsePathAndQuery();
        
        String womodelerPassword = PreferencesPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.WOMODELER_SERVER_PASSWORD);
        if (_queryParameters != null && womodelerPassword != null && womodelerPassword.equals(_queryParameters.get("pw"))) {
          IRequestHandler handler = _server.getHandler(this);
          if (handler != null) {
            handler.handle(this);
          }
          else {
            _responseCode = 404;
          }
        }
        else {
          _responseCode = 401;
        }
      }
      else if (Request.METHOD_POST.equalsIgnoreCase(_method)) {
        StringBuffer contentBuffer = new StringBuffer();
        String contentLengthStr = _requestHeaders.get(Request.CONTENT_LENGTH);
        if (contentLengthStr == null) {
          throw new IOException("Missing Content-Length header.");
        }
        int contentLength = Integer.parseInt(contentLengthStr);
        if (contentLength > 1024 * 1024) {
          throw new IOException("Illegal Content-Length: " + contentLength);
        }
        char[] buf = new char[2048];
        while (contentLength > 0 && br.ready()) {
          int charsRead = br.read(buf, 0, Math.min(contentLength, buf.length));
          if (charsRead == -1) {
            throw new IOException("Request stopped by client.");
          }
          else if (charsRead > 0) {
            contentLength -= charsRead;
            contentBuffer.append(buf, 0, charsRead);
          }
        }
        _content = contentBuffer.toString();

        _pathAndQueryString = (requestElements[1].startsWith("/") ? "" : "/") + requestElements[1];
        parsePathAndQuery();

        try {
          IRequestHandler handler = _server.getHandler(this);
          handler.handle(this);
        }
        catch (FileNotFoundException e) {
          _responseStream.reset();
          _responseCode = 404;
          getWriter().println("File Not Found: " + e.getMessage());
        }
      }

      PrintWriter pw = new PrintWriter(new OutputStreamWriter(_outputStream), true);
      pw.print("HTTP/1.1 ");
      pw.print(_responseCode);
      pw.print(" ");
      if (_responseCode == 200) {
        pw.println("OK");
      }
      else if (_responseCode == 404) {
        pw.println("Not Found");
      }
      else if (_responseCode == 401) {
        pw.println("Authorization Required");
      }
      else {
        pw.println("Unknown");
      }
      pw.println("Connection: close");
      for (Map.Entry<String, String> responseHeader : _responseHeaders.entrySet()) {
        pw.println(responseHeader.getKey() + ": " + responseHeader.getValue());
      }
      pw.println();
      pw.flush();

      _outputStream.write(_responseStream.toByteArray(), 0, _responseStream.size());
      _outputStream.flush();
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    finally {
      try {
        if (_outputStream != null) {
          _outputStream.flush();
        }
      }
      catch (Throwable t) {
        t.printStackTrace();
      }

      try {
        _socket.close();
      }
      catch (Throwable t) {
        t.printStackTrace();
      }

      //System.out.println("Request.run: request " + hashCode() + " end -- " + getPath());
    }
  }

  public String getMethod() {
    return _method;
  }

  public String getPath() {
    return _path;
  }

  public String getContent() {
    return _content;
  }

  public void setResponseCode(int responseCode) {
    _responseCode = responseCode;
  }

  public int getResponseCode() {
    return _responseCode;
  }

  public String getQueryString() {
    return _queryString;
  }

  public String getPathAndQueryString() {
    return _pathAndQueryString;
  }

  public String getQueryParameter(String name) {
    return (_queryParameters == null) ? null : _queryParameters.get(name);
  }

  public Map<String, String> getResponseHeaders() {
    return _responseHeaders;
  }

  public Map<String, String> getQueryParameters() {
    return _queryParameters;
  }

  public Map<String, String> getRequestHeaders() {
    return _requestHeaders;
  }

  public String getRequestHeader(String name) {
    return (_requestHeaders == null) ? null : _requestHeaders.get(name);
  }

  public OutputStream getOutputStream() {
    return _responseStream;
  }

  public PrintWriter getWriter() {
    return new PrintWriter(new OutputStreamWriter(_responseStream), true);
  }

  public Webserver getServer() {
    return _server;
  }

  protected void parsePathAndQuery() throws UnsupportedEncodingException {
    int queryStringIndex = _pathAndQueryString.indexOf("?");
    if (queryStringIndex != -1) {
      _queryParameters = new HashMap<String, String>();
      _path = _pathAndQueryString.substring(0, queryStringIndex);
      _queryString = _pathAndQueryString.substring(queryStringIndex + 1);
      String[] nvPairs = _queryString.split("&");
      for (int i = 0; i < nvPairs.length; i++) {
        int equalsIndex = nvPairs[i].indexOf('=');
        if (equalsIndex == -1) {
          _queryParameters.put(URLDecoder.decode(nvPairs[i], "UTF-8"), "");
        }
        else {
          _queryParameters.put(URLDecoder.decode(nvPairs[i].substring(0, equalsIndex), "UTF-8"), URLDecoder.decode(nvPairs[i].substring(equalsIndex + 1), "UTF-8"));
        }
      }
    }
    else {
      _path = _pathAndQueryString;
      _queryString = null;
    }
  }
}
