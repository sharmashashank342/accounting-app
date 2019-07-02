package com.assignment.integration;

import com.assignment.BaseClass;
import com.assignment.controller.RestController;
import com.assignment.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.assignment.factory.UserFactory.getAllPopulatedUsers;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration testing for User RestAPI
 */
public class UserApiIntegration extends BaseClass {

    @Test
    public void testGetUsers() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        List<User> users = mapper.readValue(jsonString, new TypeReference<List<User>>(){});

        assertThat(users).hasSize(7);

        List<User> testActiveUsers = new ArrayList<>(getAllPopulatedUsers());

//      Ignored Inactive User from Response
        testActiveUsers.remove(7);

        assertThat(users).extracting("userId").containsExactly(testActiveUsers.stream().map(User::getUserId).toArray());
        assertThat(users).extracting("userName").containsExactly(testActiveUsers.stream().map(User::getUserName).toArray());
        assertThat(users).extracting("emailAddress").containsExactly(testActiveUsers.stream().map(User::getEmailAddress).toArray());
        assertThat(users).extracting("modifiedOn").containsOnlyNulls();

        Optional<Timestamp> timestamp = users.stream().map(User::getCreatedOn).sorted().findFirst();
        timestamp.ifPresent(timestamp1 ->  assertThat(timestamp1).isToday());

        assertThat(users).extracting("modifiedOn").containsOnlyNulls();
    }

    @Test
    public void testGetUserById_Fail_400() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/0").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testGetUserById_Fail_404() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/100").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testGetUserById() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        User user = mapper.readValue(jsonString, User.class);
        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getUserName()).isEqualTo("shashank");
        assertThat(user.getEmailAddress()).isEqualTo("shashank@gmail.com");
        assertThat(user.getCreatedOn()).isNotNull().isToday();
        assertThat(user.getModifiedOn()).isNull();
    }

    @Test
    public void testCreateUser_Fail_Blank_UserName() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "",
//            "emailAddress": "validEmail@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"\",\n" +
                "\t\"emailAddress\": \"validEmail@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testCreateUser_Fail_Blank_Email() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "someUserName",
//            "emailAddress": ""
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"someUserName\",\n" +
                "\t\"emailAddress\": \"\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testCreateUser_Fail_Invalid_Email_Regex() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "someUserName",
//            "emailAddress": "usdhfuhfsdu"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"someUserName\",\n" +
                "\t\"emailAddress\": \"usdhfuhfsdu\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testCreateUser_Fail_Username_Already_Exists() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "shashank",
//            "emailAddress": "test@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"shashank\",\n" +
                "\t\"emailAddress\": \"test@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        // TODO: Somehow Differentiate Buisness Exceptions and Actual DB Exceptions and assert Actual status Codes
        assertThat(response.getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(400);
    }

    @Test
    public void testCreateUser_Fail_Email_Already_Exists() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON (Invalid Email Exception)
//        {
//            "userName": "newUser",
//            "emailAddress": "shashank@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"newUser\",\n" +
                "\t\"emailAddress\": \"shashank@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        // TODO: Somehow Differentiate Buisness Exceptions and Actual DB Exceptions and assert Actual status Codes
        assertThat(response.getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(400);
    }

    @Test
    public void testCreateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "newUser",
//            "emailAddress": "newUser@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"newUser\",\n" +
                "\t\"emailAddress\": \"newUser@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        User user = mapper.readValue(jsonString, User.class);

        assertThat(user.getUserId()).isEqualTo(9L);
        assertThat(user.getUserName()).isEqualTo("newUser");
        assertThat(user.getEmailAddress()).isEqualTo("newUser@gmail.com");
        assertThat(user.getCreatedOn()).isToday();
    }

    @Test
    public void testUpdateUser_Fails_Invalid_Id() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/0").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "newUser",
//            "emailAddress": "newUser@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"newUser\",\n" +
                "\t\"emailAddress\": \"newUser@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testUpdateUser_Fails_Not_Found_Id() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/100").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "newUser",
//            "emailAddress": "newUser@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"newUser\",\n" +
                "\t\"emailAddress\": \"newUser@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testUpdateUser() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/7").build();
        HttpPut request = new HttpPut(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userName": "non_account_user",
//            "emailAddress": "non_account_user@gmail.com"
//        }

        String bodyJson = "{\n" +
                "\t\"userName\": \"non_account_user_new_name\",\n" +
                "\t\"emailAddress\": \"non_account_user_new@gmail.com\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);


        uri = builder.setPath("/users/7").build();
        HttpGet getRequest = new HttpGet(uri);
        HttpResponse getResponse = client.execute(getRequest);

        assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(200);
        //check the content
        String jsonString = EntityUtils.toString(getResponse.getEntity());
        User user = mapper.readValue(jsonString, User.class);
        assertThat(user.getUserId()).isEqualTo(7L);
        assertThat(user.getUserName()).isEqualTo("non_account_user_new_name");
        assertThat(user.getEmailAddress()).isEqualTo("non_account_user_new@gmail.com");
        assertThat(user.getCreatedOn()).isNotNull().isToday();
        assertThat(user.getModifiedOn()).isToday();

    }

    @Test
    public void testDeleteUser_Fails_non_valid_id() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/0").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testDeleteUser_Fails_not_found() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/users/100").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testDeleteUser() throws Exception {
        URI uri = builder.setPath("/users/4").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // Verify Same User's Account Get should fail
        uri = builder.setPath("/accounts/4").build();
        HttpGet getAccount = new HttpGet(uri);
        response = client.execute(getAccount);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);

        // Wait for some time for Account to Deactivate
        TimeUnit.SECONDS.sleep(2L);

        // Now Assert User Account Status Deactivation
        uri = builder.setPath("/accounts/4").build();
        HttpGet getRequest = new HttpGet(uri);

        HttpResponse getResponse = client.execute(getRequest);

        assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(400);
        // As User has been inactivated via Event
        assertThat(RestController.removeInactiveUser(4L)).isFalse();
    }

}
