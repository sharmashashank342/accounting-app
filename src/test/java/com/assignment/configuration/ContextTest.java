package com.assignment.configuration;

import com.assignment.BaseClass;
import com.assignment.TestContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTest extends BaseClass {

    @Test
    public void test_contextInstance() {

        assertThat(Context.getContext()).isInstanceOf(TestContext.class);
    }
}
