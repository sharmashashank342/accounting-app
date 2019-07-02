package com.assignment;

import com.assignment.configuration.Context;

public class TestContext extends Context {

    private TestContext() {
        super();
    }


    static void initilazeTestContext() {

        if (getContext() == null)
             new TestContext();
    }
}
