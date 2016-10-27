package cse403.finderskeepers;

import android.content.Intent;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Jared on 10/27/2016.
 */

public class ServiceTestUnitTest {

    private UserAPIService UServ;

    @Before
    public void setUp() throws Exception {
        UServ = new UserAPIService();

    }

    @Test
    public void checkUnitTestsWorkTest() throws Exception {
        assertTrue("yay.", true);
    }

    @Test
    public void onBindSuccessCheck() throws Exception {
        assertTrue("onBind method failed to return a valid iBinder!", (UServ.onBind(new Intent())) != null);
    }

    @After
    public void tearDown() throws Exception {


    }
}
