package com.assignment.integration;

import com.assignment.BaseClass;
import com.assignment.model.Account;
import com.assignment.model.CreateTransactionRequest;
import com.assignment.model.Transactions;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static com.assignment.utils.AmountUtil.setDisplayAmount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Integration testing for Accounts RestAPI
 */
public class AccountsApiIntegration extends BaseClass {

    @Test
    public void testGetAccountById_Fail_400() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/0").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testGetAccountById_Fail_404() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/100").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testGetAccountById() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertThat(account.getUserId()).isEqualTo(1L);
        assertThat(setDisplayAmount(account.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(100)));
        assertThat(account.getCurrencyCode()).isEqualTo("USD");
        assertThat(account.getCreatedOn()).isNotNull().isToday();
        assertThat(account.getModifiedOn()).isNull();
    }

    @Test
    public void testCreateAccount_Fail() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON (Invalid User Id Exception)
//        {
//            "userId": 10,
//                "currencyCode": "INR"
//        }

        String bodyJson = "{\n" +
                "\t\"userId\": 10,\n" +
                "\t\"currencyCode\": \"INR\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testCreateAccount_Fail_Currency_NotValid() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON (Invalid User Id Exception)
//        {
//            "userId": 10,
//                "currencyCode": "INR"
//        }

        String bodyJson = "{\n" +
                "\t\"userId\": 2,\n" +
                "\t\"currencyCode\": \"some-non-valid-currency\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testCreateAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts").build();
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

//        Request JSON
//        {
//            "userId": 7,
//                "currencyCode": "INR"
//        }

        String bodyJson = "{\n" +
                            "\t\"userId\": 7,\n" +
                "\t\"currencyCode\": \"INR\"\n" +
                "}";

        request.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertThat(account.getUserId()).isEqualTo(7L);
        assertThat(setDisplayAmount(account.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.ZERO));
        assertThat(account.getCurrencyCode()).isEqualTo("INR");
        assertThat(account.getCreatedOn()).isNotNull().isToday();
        assertThat(account.getModifiedOn()).isNull();
    }

    @Test
    public void testDeleteAccount_Fails_non_valid_id() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/0").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testDeleteAccount_Fails_not_found() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/100").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testDeleteAccount() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/6").build();
        HttpDelete request = new HttpDelete(uri);

        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        // Verify Same Account Get
        uri = builder.setPath("/accounts/6").build();
        HttpGet getAccount = new HttpGet(uri);
        response = client.execute(getAccount);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testGetAccountByUserId_Fail_400() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/users/0").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testGetAccountUserId_Fail_404() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/users/100").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    }

    @Test
    public void testGetAccountByUserId() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/accounts/users/1").build();
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        //check the content
        String jsonString = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(jsonString, Account.class);
        assertThat(account.getUserId()).isEqualTo(1L);
        assertThat(setDisplayAmount(account.getBalance())).isEqualTo(setDisplayAmount(BigDecimal.valueOf(100)));
        assertThat(account.getCurrencyCode()).isEqualTo("USD");
        assertThat(account.getCreatedOn()).isNotNull().isToday();
        assertThat(account.getModifiedOn()).isNull();
    }

    @Test
    public void testTransactions_Success() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = BigDecimal.TEN;
        CreateTransactionRequest transaction = new CreateTransactionRequest("INR", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString, ContentType.APPLICATION_JSON);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);

        //check the content
        Transactions transactions = mapper.readValue(EntityUtils.toString(response.getEntity()), Transactions.class);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);

        assertThat(transactions.getSenderAccountId()).isEqualTo(3L);
        assertThat(transactions.getReceiverAccountId()).isEqualTo(4L);
        assertThat(setDisplayAmount(transactions.getAmount())).isEqualTo(setDisplayAmount(BigDecimal.TEN));
        assertThat(transactions.getCreatedOn()).isToday();
    }

    @Test
    public void testTransactions_Fail_NotEnoughFund() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = BigDecimal.valueOf(100000L);
        CreateTransactionRequest transaction = new CreateTransactionRequest("INR", amount, 3L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString, ContentType.APPLICATION_JSON);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    }

    @Test
    public void testTransactions_Fails_DifferentCurrency() throws IOException, URISyntaxException {
        URI uri = builder.setPath("/transactions").build();
        BigDecimal amount = BigDecimal.ONE;
        CreateTransactionRequest transaction = new CreateTransactionRequest("USD", amount, 1L, 4L);

        String jsonInString = mapper.writeValueAsString(transaction);
        StringEntity entity = new StringEntity(jsonInString, ContentType.APPLICATION_JSON);
        HttpPost request = new HttpPost(uri);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);

    }
}
