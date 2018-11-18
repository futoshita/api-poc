package com.futoshita.api.sdk;

import com.futoshita.api.sdk.service.GreetingServiceTest;
import com.futoshita.api.sdk.service.UserServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GreetingServiceTest.class,
        UserServiceTest.class
})
public final class IntegrationTestingFactory {
}
