
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
public class NativeProtocolsDBTest {

    public NativeProtocolsDBTest() {
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
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        // we try ip first and then ipv4 due to jnr/jnr-netdb#4
        Protocol p = Protocol.getProtocolByName("ip");
        if (p != null) {
            assertEquals("incorrect proto number", 0, p.getProto());
            assertEquals("incorrect name", "ip", p.getName());
        } else {
            p = Protocol.getProtocolByName("ipv4");
            assertNotNull("could not lookup ipv4 protocol", p);
            assertEquals("incorrect proto number", 4, p.getProto());
            assertEquals("incorrect name", "ipv4", p.getName());
        }
    }

    @Test public void canLookupIpProtocolByNumber() {
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        // we try ip first and then ipv4 due to jnr/jnr-netdb#4
        Protocol p = Protocol.getProtocolByName("ip");
        if (p != null) {
            p = Protocol.getProtocolByNumber(0);
            assertEquals("incorrect proto number", 0, p.getProto());
            assertEquals("incorrect name", "ip", p.getName());
        } else {
            p = Protocol.getProtocolByNumber(4);
            assertNotNull("could not lookup ip protocol", p);
            assertEquals("incorrect proto number", 4, p.getProto());
            assertEquals("incorrect name", "ipv4", p.getName());
        }
    }

    @Test public void canLookupTcpProtocolByName() {
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        Protocol p = db.getProtocolByName("tcp");
        assertNotNull("could not lookup tcp protocol", p);
        assertEquals("incorrect proto number", 6, p.getProto());
        assertEquals("incorrect name", "tcp", p.getName());
    }

    @Test public void canLookupTcpProtocolByNumber() {
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        Protocol p = db.getProtocolByNumber(6);
        assertNotNull("could not lookup tcp protocol", p);
        assertEquals("incorrect proto number", 6, p.getProto());
        assertEquals("incorrect name", "tcp", p.getName());
    }

     @Test public void getAllProtocolsReturnsNonEmptyList() {
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        assertFalse(db.getAllProtocols().isEmpty());
    }

    @Test public void getAllProtocolsContainsTcp() {
        ProtocolsDB db = NativeProtocolsDB.getInstance();
        boolean tcpFound = false;
        for (Protocol p : db.getAllProtocols()) {
            if (p.getName().equals("tcp") || p.getAliases().contains("TCP")) {
                tcpFound = true;
                break;
            }
        }
        assertTrue(tcpFound);
    }
}