package com.accountingapp.controller;

import com.accountingapp.configuration.Context;
import com.accountingapp.dto.AccountDTO;
import com.accountingapp.dto.CreateAccountParams;
import com.accountingapp.dto.UserDTO;
import com.accountingapp.events.service.EventService;
import com.accountingapp.exception.InvalidRequestException;
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

    private boolean useEventForAccountDeactivation = Boolean.parseBoolean(Utils.getProperty("deactivate_account_via_event"));

    @GET
    @Path("/accounts/{accountId}")
    public AccountDTO getAccount(@PathParam("accountId") long accountId) {
        return accountService.getAccountById(accountId);
    }

//    TODO: Check if we can return 201 as success response.
    @POST
    @Path("/accounts")
    @Consumes(MediaType.APPLICATION_JSON)
    public AccountDTO createAccount(CreateAccountParams accountParams) {

        validateAccountCreationParam(accountParams);

        // User Status active check in DB also
        userService.getUserById(accountParams.getUserId());

        return accountService.createAccount(buildAccountDTO(accountParams));
    }

    private AccountDTO buildAccountDTO(CreateAccountParams accountParams) {
        AccountDTO account = new AccountDTO();
        account.setUserId(accountParams.getUserId());
        account.setCurrencyCode(accountParams.getCurrencyCode());
        return account;
    }

    private void validateAccountCreationParam(CreateAccountParams account) {

        if (account.getUserId() == 0)
            throw new InvalidRequestException("Invalid User Id");

        if (Objects.isNull(account.getCurrencyCode()))
            account.setCurrencyCode(AmountUtil.DEFAULT_CURRENCY);
        else if (!AmountUtil.validateCurrencyCode(account.getCurrencyCode()))
            throw new InvalidRequestException("Invalid Currency Code");

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
    public AccountDTO getAccountByUserId(@PathParam("userId") long userId) {
        return accountService.getAccountByUserId(userId);
    }

    @GET
    @Path("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/users/{userId}")
    public User getUserById(@PathParam("userId") long userId) {
        return userService.getUserById(userId);
    }

    @POST
    @Path("/users")
    public UserDTO createUser(UserDTO user) {

        validateUserCreationParam(user);

        return userService.createUser(user);
    }

    private void validateUserCreationParam(UserDTO user) {

        if (Objects.isNull(user.getUserName()) || user.getUserName().trim().isEmpty())
            throw new InvalidRequestException("Username is Invalid");

        if (Objects.isNull(user.getEmailAddress()) || user.getEmailAddress().trim().isEmpty() ||
                Utils.isEmailInvalid(user.getEmailAddress()))
            throw new InvalidRequestException("Email is Invalid");
    }

    @PUT
    @Path("/users/{userId}")
    public Response updateUser(@PathParam("userId") long userId, UserDTO user) {

        userService.updateUser(userId, user);

        return Response.ok().build();
    }

    // Technically its Deactivate User
    // Never Hard Delete any Data from Database
    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) {

        userService.deleteUser(userId);

        if (useEventForAccountDeactivation) {
            eventService.raiseUserDeactivatedEvent(userId);
        }else {
            // Real Time Update
            AccountDTO accountDTO = accountService.getAccountByUserId(userId);
            accountService.deleteAccount(accountDTO.getAccountId());
        }

        return Response.ok().build();
    }

    @POST
    @Path("/transactions")
    public Transactions createAccountTransfer(CreateTransactionRequest transactionRequest) {

        if (transactionRequest.getAmount().signum() <= 0) {
            throw new InvalidRequestException("Amount should be non negitive");
        }

        if (transactionRequest.getReceiverAccountId().equals(transactionRequest.getSenderAccountId())) {
            throw new InvalidRequestException("Can't initiate Txn for same Accounts");
        }

        AccountDTO fromAccount = accountService.getAccountById(transactionRequest.getSenderAccountId());

        AccountDTO toAccount = accountService.getAccountById(transactionRequest.getReceiverAccountId());

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
