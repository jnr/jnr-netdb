
package jnr.netdb;

import com.kenai.jaffl.Platform;
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
        if (Platform.getPlatform().getOS().equals(Platform.OS.DARWIN)) {
            ServicesDB db = NativeServicesDB.load();
            Service s = db.getServiceByName("blp5", "udp");
            assertNotNull("could not lookup blp5 service", s);
            assertEquals("incorrect port", 48129, s.getPort());
            assertEquals("incorrect name", "blp5", s.getName());
        }
    }

    @Test public void canLookupServiceLargerThan32768ByPort() {
        if (Platform.getPlatform().getOS().equals(Platform.OS.DARWIN)) {
            ServicesDB db = NativeServicesDB.load();
            Service s = db.getServiceByPort(48129, "udp");
            assertNotNull("could not lookup blp5 service", s);
            assertEquals("incorrect port", 48129, s.getPort());
            assertEquals("incorrect name", "blp5", s.getName());
        }
    }

}