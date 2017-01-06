
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
        Protocol p = Protocol.getProtocolByName("ipv4");
        assertNotNull("could not lookup ipv4 protocol", p);
        assertEquals("incorrect proto number", 4, p.getProto());
        assertEquals("incorrect name", "ipv4", p.getName());
    }

    @Test public void returnsNullOnUnknownProtocol() {
        Protocol p = Protocol.getProtocolByName("foo-bar-baz");
        assertNull("could not handle unknown protocol", p);
    }

    @Test public void canLookupIpProtocolByNumber() {
        Protocol p = Protocol.getProtocolByNumber(4);
        assertNotNull("could not lookup ip protocol", p);
        assertEquals("incorrect proto number", 4, p.getProto());
        assertEquals("incorrect name", "ipv4", p.getName());
    }

    @Test public void returnsNullOnInvalidNumber() {
        Protocol p = Protocol.getProtocolByNumber(-1);
        assertNull("could not handle invalid number ", p);
    }
}
