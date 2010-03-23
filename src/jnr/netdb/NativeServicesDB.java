
package jnr.netdb;

import com.kenai.jaffl.Library;
import com.kenai.jaffl.Platform;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;

import static com.kenai.jaffl.Platform.OS.*;

/**
 *
 */
final class NativeServicesDB implements ServicesDB {

    private final LibServices lib;

    public NativeServicesDB(LibServices lib) {
        this.lib = lib;
    }

    static final ServicesDB load() {
        try {
            Platform.OS os = Platform.getPlatform().getOS();
            
            

            // The ServiceEntry struct is only known to match on MacOSX, Linux, Solaris.
            // We assume FreeBSD and NetBSD also match.
            if (!(os.equals(DARWIN) || os.equals(LINUX) || os.equals(SOLARIS) || os.equals(FREEBSD) || os.equals(NETBSD))) {
                return null;
            }

            String[] libnames = os.equals(SOLARIS)
                        ? new String[] { "socket", "nsl", "c" }
                        : new String[] { "c" };

            LibServices lib = Library.loadLibrary(LibServices.class, libnames);
            
            return new NativeServicesDB(lib);
        } catch (Throwable t) {
            return null;
        }
    }

    public static class UnixServent extends com.kenai.jaffl.struct.Struct {

        public final String name = new UTF8StringRef();
        public final Pointer aliases = new Pointer();
        public final Signed32 port = new Signed32();
        public final String proto = new UTF8StringRef();
    }

    public static interface LibServices {
        UnixServent getservbyname(String name, String proto);
        UnixServent getservbyport(Integer port, String proto);
    }
    
    public Collection<Service> getAllServices() {
        return Collections.EMPTY_LIST;
    }

    private final Service serviceFromNative(UnixServent s) {
        // servent#port is in network byte order - but jaffl will assume it is in host byte order
        // so it needs to be reversed again to be correct.
        int port = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes((short) s.port.get()) : s.port.get();

        return s != null ? new Service(s.name.get(), port, s.proto.get(), Collections.EMPTY_LIST) : null;
    }

    public Service getServiceByName(String name, String proto) {
        return serviceFromNative(lib.getservbyname(name, proto));
    }

    public Service getServiceByPort(Integer port, String proto) {
        return serviceFromNative(lib.getservbyport(port, proto));
    }


}
