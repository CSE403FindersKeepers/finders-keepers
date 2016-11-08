/*Unit tests for the backend data/API/push notifications service. */
package cse403.finderskeepers;


import android.content.Intent;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cse403.finderskeepers.UserAPIService;

import static org.junit.Assert.*;

/**
 * Created by Jared on 10/27/2016.
 * This set of tests targets the API service. Make sure to have a test here for any API functions
 * that are added to it, as well as for autonomous functionality (though some autonomous
 * functions may be more appropriately included in the instrumented tests.)
 */

public class ServiceTestUnitTest {

    private UserAPIService UServ;

    //Set up for testing. To test our API service, we first need to instantiate a service
    //that we can bind to for testing.
    @Before
    public void setUp() throws Exception {
        UServ = new UserAPIService();

    }

    //Dummy test to make sure that unit testing runs.
    @Test
    public void checkUnitTestsWorkTest() throws Exception {
        assertTrue("yay.", true);
    }

    //Ensure that onBind returns a valid IBinder.
    //TODO: When onBind is finished, update to check actual validity of IBinder.
    @Test
    public void onBindSuccessCheck() throws Exception {
        assertTrue("onBind method failed to return a valid IBinder!", (UServ.onBind(new Intent())) != null);
    }

    //Use for teardown if we need it -- probably shouldn't here.
    @After
    public void tearDown() throws Exception {


    }
}
