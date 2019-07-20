package com.accountingapp.server;

import com.accountingapp.controller.RestController;
import com.accountingapp.exception.ServiceExceptionMapper;
import com.accountingapp.service.UserServiceImpl;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class HttpServer {

    private static final int port = 8888;

    private HttpServer() {
    }

    public static void start() throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(port);

        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", RestController.class.getCanonicalName() + "," +
                        UserServiceImpl.class.getCanonicalName()  + "," +
                ServiceExceptionMapper.class.getCanonicalName());

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
