package org.objectstyle.wolips.womodeler.server;


public interface IRequestHandler {
  public void init(Webserver server) throws Exception;

  public void handle(Request request) throws Exception;
}
