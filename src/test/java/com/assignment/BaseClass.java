package com.assignment;

import com.assignment.configuration.Context;
import com.assignment.controller.RestController;
import com.assignment.data.DBManager;
import com.assignment.data.H2DBManager;
import com.assignment.exception.ServiceExceptionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class BaseClass {
    private static Server server = null;
    private static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    protected static HttpClient client ;
    private static DBManager h2Dbmanager;
    protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:8889");
    protected ObjectMapper mapper = new ObjectMapper();

    static {
        // Create Test Context
        TestContext.initilazeTestContext();
        h2Dbmanager = Context.getContext().getDbManager();
    }


    @BeforeClass
    public static void setup() throws Exception {
        // Initilize Server
        startServer();
        connManager.setDefaultMaxPerRoute(100);
        connManager.setMaxTotal(200);
        client= HttpClients.custom()
                .setConnectionManager(connManager)
                .setConnectionManagerShared(true)
                .build();

    }

    @Before
    public void setUp() {
        // Initilize Test Data
        h2Dbmanager.populateTestData();
    }

    @AfterClass
    public static void closeClient() throws Exception {
        HttpClientUtils.closeQuietly(client);
        connManager.close();
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
