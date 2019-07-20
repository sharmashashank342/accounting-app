package com.accountingapp.controller;

import com.accountingapp.configuration.Context;
import com.accountingapp.events.service.EventService;
import com.accountingapp.exception.InvalidRequestException;
import com.accountingapp.exception.InvalidUserIdException;
import com.accountingapp.model.Account;
import com.accountingapp.model.CreateTransactionRequest;
import com.accountingapp.model.Transactions;
import com.accountingapp.model.User;
import com.accountingapp.service.AccountService;
import com.accountingapp.service.UserService;
import com.accountingapp.utils.AmountUtil;
import com.accountingapp.utils.Utils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

// TODO: Need to Pull this out to Seprate MicroService i.e API Layer (Orchestration Layer)
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RestController {

    // TODO: Pull This as Seprate MicroService i.e Account Service
    private final AccountService accountService = Context.getContext().getAccountService();

    // TODO: Pull This as Seprate MicroService i.e User Service
    private final UserService userService = Context.getContext().getUserService();

    private final EventService eventService = Context.getContext().getEventService();

    @GET
    @Path("/accounts/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) {
        return accountService.getAccountById(accountId);
    }

//    TODO: Check if we can return 201 as success response.
    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Account createAccount(Account account) {

        validateAccountCreationParam(account);

        // User Status active check in DB also
        userService.getUserById(account.getUserId());

        return accountService.createAccount(account);
    }

    private void validateAccountCreationParam(Account account) {

        if (account.getUserId() == 0)
            throw new InvalidRequestException("Invalid User Id");

        if (Objects.isNull(account.getCurrencyCode()))
            account.setCurrencyCode(AmountUtil.DEFAULT_CURRENCY);
        else if (!AmountUtil.validateCurrencyCode(account.getCurrencyCode()))
            throw new InvalidRequestException("Invalid Currency Code");

        if (Objects.nonNull(account.getBalance()) && account.getBalance().compareTo(BigDecimal.ZERO) != 0)
            throw new InvalidRequestException("Account Balance should be 0 for New Accounts");

    }

    // Technically its Deactivate Account
    // Never Hard Delete any Data from Database
    @DELETE
    @Path("/accounts/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) {
        accountService.deleteAccount(accountId);

        return Response.ok().build();
    }

    @GET
    @Path("/accounts/users/{userId}")
    public Account getAccountByUserId(@PathParam("userId") long userId) {
        return accountService.getAccountByUserId(userId);
    }

    @GET
    @Path("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/users/{userId}")
    public User getUserById(@PathParam("userId") long userId) {
        if (userId == 0)
            throw new InvalidUserIdException(userId);
        return userService.getUserById(userId);
    }

    @POST
    @Path("/users")
    public User createUser(User user) {

        validateUserCreationParam(user);

        return userService.createUser(user);
    }

    private void validateUserCreationParam(User user) {

        if (Objects.isNull(user.getUserName()) || user.getUserName().trim().isEmpty())
            throw new InvalidRequestException("Username is Invalid");

        if (Objects.isNull(user.getEmailAddress()) || user.getEmailAddress().trim().isEmpty() ||
                Utils.isNotValid(user.getEmailAddress()))
            throw new InvalidRequestException("Email is Invalid");
    }

    @PUT
    @Path("/users/{userId}")
    public Response updateUser(@PathParam("userId") long userId, User user) {

        userService.updateUser(userId, user);

        return Response.ok().build();
    }

    // Technically its Deactivate User
    // Never Hard Delete any Data from Database
    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) {

        userService.deleteUser(userId);
        eventService.raiseUserDeactivatedEvent(userId);

        return Response.ok().build();
    }

    @POST
    @Path("/transactions")
    public Transactions createAccountTransfer(CreateTransactionRequest transactionRequest) {

        Account fromAccount = accountService.getAccountById(transactionRequest.getSenderAccountId());

        Account toAccount = accountService.getAccountById(transactionRequest.getReceiverAccountId());

        // Verifying Sender and Receiver to be Active
        Stream.of(fromAccount.getUserId(), toAccount.getUserId())
                .forEach(userService::getUserById);

        // TODO: Somehow handle Currency Conversion.
        if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
            throw new InvalidRequestException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }

        return accountService.createAccountTransfer(transactionRequest);
    }
}
