package com.assignment.exception;

import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<BaseException> {

	private static Logger log = Logger.getLogger(ServiceExceptionMapper.class);

	@Override
	public Response toResponse(BaseException e) {
		log.error("Exception Occured in Account Service ",e);
		return Response.status(e.getStatusCode())
				.entity(new ExceptionResult(e.getMessage()))
				.build();
	}

}
