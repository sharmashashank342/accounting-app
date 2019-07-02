package com.assignment.service;

import com.assignment.data.DBManager;
import com.assignment.exception.BaseException;
import com.assignment.model.CreateTransactionRequest;
import com.assignment.utils.AmountUtil;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionService {

	private final DBManager dBManager = DBManager.getInstance();
	
	/**
	 * Transfer fund between two accounts.
	 * @param transaction
	 * @return
	 * @throws BaseException
	 */
	@POST
	public Response transferFund(CreateTransactionRequest transaction) throws BaseException {

//		String currency = transaction.getCurrencyCode();
//		if (AmountUtil.validateCurrencyCode(currency)) {
//			int updateCount = dBManager.getAccountsManager().createAccountTransfer(transaction);
//			if (updateCount == 2) {
//				return Response.status(Response.Status.OK).build();
//			} else {
//				// transaction failed
//				throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
//			}
//
//		} else {
//			throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
//		}

		return null;

	}

}
