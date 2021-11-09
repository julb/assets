package me.julb.springbootstarter.test.messaging.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

import me.julb.springbootstarter.test.base.AbstractBaseTest;

/**
 * Base unit test for messaging.
 * <br>
 * @author Julb.
 */
@Import(TestChannelBinderConfiguration.class)
public abstract class AbstractMessagingBaseTest extends AbstractBaseTest {

    /**
     * The output destination.
     */
    @Autowired(required = false)
    protected OutputDestination output;
}
