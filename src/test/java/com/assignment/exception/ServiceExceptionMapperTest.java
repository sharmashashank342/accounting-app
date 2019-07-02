package com.assignment.exception;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceExceptionMapperTest {

    @Test
    public void test_toResponse_for_404() {

        BaseException baseException = new BaseException("Some Message", 404);

        Response response = new ServiceExceptionMapper().toResponse(baseException);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isEqualTo(new ExceptionResult("Some Message"));
    }

    @Test
    public void test_toResponse_for_400() {

        BaseException baseException = new BaseException("Some Message", 400);

        Response response = new ServiceExceptionMapper().toResponse(baseException);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getEntity()).isEqualTo(new ExceptionResult("Some Message"));
    }
}
