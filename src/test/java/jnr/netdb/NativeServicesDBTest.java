
package jnr.netdb;

import jnr.ffi.Platform;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class NativeServicesDBTest {

    public NativeServicesDBTest() {
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

    @Test public void canLookupServiceThan32768ByName() {
        if (Platform.getNativePlatform().getOS().equals(Platform.OS.DARWIN)) {
            ServicesDB db = NativeServicesDB.load();
            Service s = db.getServiceByName("blp5", "udp");
            assertNotNull("could not lookup blp5 service", s);
            assertEquals("incorrect port", 48129, s.getPort());
            assertEquals("incorrect name", "blp5", s.getName());
        }
    }

    @Test public void canLookupServiceLargerThan32768ByPort() {
        if (Platform.getNativePlatform().getOS().equals(Platform.OS.DARWIN)) {
            ServicesDB db = NativeServicesDB.load();
            Service s = db.getServiceByPort(48129, "udp");
            assertNotNull("could not lookup blp5 service", s);
            assertEquals("incorrect port", 48129, s.getPort());
            assertEquals("incorrect name", "blp5", s.getName());
        }
    }
    
    @Test public void canLookupServiceWithAliasByName() {
        ServicesDB db = NativeServicesDB.load();
        Service s = db.getServiceByName("comsat", "udp");
        assertNotNull("could not lookup comsat/biff service", s);
        assertEquals("incorrect port", 512, s.getPort());
        assertTrue(s.getName().equals("biff") || s.getName().equals("comsat"));
        assertTrue(s.getAliases().contains("biff") || s.getAliases().contains("comsat"));
    }

    @Test public void canLookupServiceWithAliasByPort() {
        ServicesDB db = NativeServicesDB.load();
        Service s = db.getServiceByPort(512, "udp");
        assertNotNull("could not lookup comsat/biff service", s);
        assertEquals("incorrect port", 512, s.getPort());
        assertTrue(s.getName().equals("biff") || s.getName().equals("comsat"));
        assertTrue(s.getAliases().contains("biff") || s.getAliases().contains("comsat"));
    }

    @Test public void getAllServicesReturnsNonEmptyList() {
        ServicesDB db = NativeServicesDB.load();
        assertFalse(db.getAllServices().isEmpty());
    }

    @Test public void getAllServicesContainsFtp() {
        ServicesDB db = NativeServicesDB.load();
        boolean ftpFound = false;
        for (Service s : db.getAllServices()) {
            if (s.getName().equals("ftp") || s.getAliases().contains("ftp")) {
                ftpFound = true;
                break;
            }
        }
        assertTrue(ftpFound);
    }
}