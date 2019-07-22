package com.accountingapp;

import com.accountingapp.configuration.Context;
import com.accountingapp.controller.RestController;
import com.accountingapp.data.DBManager;
import com.accountingapp.exception.ServiceExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;


public abstract class BaseClass {

    private static Server server = null;
    protected HttpClient client ;
    private static DBManager h2Dbmanager;
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8889");
    protected ObjectMapper mapper = new ObjectMapper();

    static {
        // Create Test Context
        TestContext.initilazeTestContext();
        h2Dbmanager = Context.getContext().getDbManager();
    }


    @Before
    public void setup() throws Exception {
        // Initilize Test Data and Server
        h2Dbmanager.populateData();
        startServer();
        client = HttpClients.custom().build();
    }

    @After
    public void closeClient() {
        HttpClientUtils.closeQuietly(client);
    }


    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8889);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    RestController.class.getCanonicalName() + "," +
                            ServiceExceptionMapper.class.getCanonicalName());
            server.start();
        }
    }
}
