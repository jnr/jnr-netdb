
package jnr.netdb;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ProtocolTest {

    public ProtocolTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
   
    @Test public void canLookupIpProtocolByName() {
        Protocol p = Protocol.getProtocolByName("ip");
        assertNotNull("could not lookup ip protocol", p);
        assertEquals("incorrect proto number", 0, p.getProto());
        assertEquals("incorrect name", "ip", p.getName());
    }

    @Test public void canLookupIpProtocolByNumber() {
        Protocol p = Protocol.getProtocolByNumber(0);
        assertNotNull("could not lookup ip protocol", p);
        assertEquals("incorrect proto number", 0, p.getProto());
        assertEquals("incorrect name", "ip", p.getName());
    }
}