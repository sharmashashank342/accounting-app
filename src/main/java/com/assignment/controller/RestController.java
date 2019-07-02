package com.assignment.controller;

import com.assignment.configuration.Context;
import com.assignment.events.service.EventService;
import com.assignment.exception.BaseException;
import com.assignment.exception.InvalidAccountIdException;
import com.assignment.exception.InvalidRequestException;
import com.assignment.exception.InvalidUserIdException;
import com.assignment.model.Account;
import com.assignment.model.CreateTransactionRequest;
import com.assignment.model.Transactions;
import com.assignment.model.User;
import com.assignment.service.AccountService;
import com.assignment.service.UserService;
import com.assignment.utils.AmountUtil;
import com.assignment.utils.Utils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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


    // TODO: think of any other alternative to disallow pending deactivation user to create txn
    private static Set<Long> inactiveUsers;

    static {
        inactiveUsers = new HashSet<>();
    }

    @GET
    @Path("/accounts/{accountId}")
    public Account getAccount(@PathParam("accountId") long accountId) {
        if (accountId == 0)
            throw new InvalidAccountIdException(accountId);
        return accountService.getAccountById(accountId);
    }

//    TODO: Check if we can return 201 as success response.
    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Account createAccount(Account account) {

        validateAccountCreationParam(account);

        // Verify User Status to be Active in System
        if (inactiveUsers.contains(account.getUserId())) {
            throw new InvalidRequestException("User is Deactivated "+account.getUserId());
        }
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
        if (accountId == 0)
            throw new InvalidAccountIdException(accountId);
        accountService.deleteAccount(accountId);

        return Response.ok().build();
    }

    @GET
    @Path("/accounts/users/{userId}")
    public Account getAccountByUserId(@PathParam("userId") long userId) {
        if (userId == 0)
            throw new InvalidUserIdException(userId);
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
        if (userId == 0)
            throw new InvalidUserIdException(userId);
        userService.updateUser(userId, user);

        return Response.ok().build();
    }

    // Technically its Deactivate User
    // Never Hard Delete any Data from Database
    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) {
        if (userId == 0)
            throw new InvalidUserIdException(userId);

        // Set User as Pending Deactivation partially
        // so that user can not perform any Transaction in system
        inactiveUsers.add(userId);

        try {
            userService.deleteUser(userId);
            eventService.raiseUserDeactivatedEvent(userId);
            return Response.ok().build();
        }catch (BaseException e) {
            removeInactiveUser(userId);
            throw e;
        }
    }

    @POST
    @Path("/transactions")
    public Transactions createAccountTransfer(CreateTransactionRequest transactionRequest) {

        Account fromAccount = accountService.getAccountById(transactionRequest.getSenderAccountId());

        Account toAccount = accountService.getAccountById(transactionRequest.getReceiverAccountId());

        // Verifying Sender and Receiver to be Active
        Stream.of(fromAccount.getUserId(), toAccount.getUserId())
                .forEach(userService::getUserById);

        // Also verify if any user is pending deactivation
        if (inactiveUsers.contains(fromAccount.getUserId())) {
            throw new InvalidRequestException("User is Deactivated "+fromAccount.getUserId());
        } else if (inactiveUsers.contains(toAccount.getUserId())) {
            throw new InvalidRequestException("User is Deactivated "+toAccount.getUserId());
        }

        // TODO: Somehow handle Currency Conversion.
        if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
            throw new InvalidRequestException(
                    "Fail to transfer Fund, the source and destination account are in different currency");
        }

        return accountService.createAccountTransfer(transactionRequest);
    }

    public static boolean removeInactiveUser(Long userId) {
        return inactiveUsers.remove(userId);
    }
}
