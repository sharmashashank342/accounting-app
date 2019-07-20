package com.accountingapp.configuration;

import com.accountingapp.BaseClass;
import com.accountingapp.TestContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextTest extends BaseClass {

    @Test
    public void test_contextInstance() {

        assertThat(Context.getContext()).isInstanceOf(TestContext.class);
    }
}
