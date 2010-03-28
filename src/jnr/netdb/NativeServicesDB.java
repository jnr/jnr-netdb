/*
 * Copyright (C) 2010 Wayne Meissner
 *
 * This file is part of jnr.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package jnr.netdb;

import com.kenai.jaffl.CallingConvention;
import com.kenai.jaffl.Library;
import com.kenai.jaffl.LibraryOption;
import com.kenai.jaffl.Platform;
import com.kenai.jaffl.Pointer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kenai.jaffl.Platform.OS.*;

/**
 *
 */
final class NativeServicesDB implements ServicesDB {

    private final LibServices lib;

    public NativeServicesDB(LibServices lib) {
        this.lib = lib;
    }

    public static final NativeServicesDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final NativeServicesDB INSTANCE = load();
    }

    static final NativeServicesDB load() {
        try {
            Platform.OS os = Platform.getPlatform().getOS();

            // The ServiceEntry struct is only known to match on Windows, MacOSX, Linux, Solaris.
            // We assume FreeBSD and NetBSD also match.
            if (!(os.equals(DARWIN) || (os.equals(WINDOWS) && Platform.getPlatform().getCPU() == Platform.CPU.I386)
                    || os.equals(LINUX) || os.equals(SOLARIS)
                    || os.equals(FREEBSD) || os.equals(NETBSD))) {
                return null;
            }

            LibServices lib;
            if (os.equals(WINDOWS)) {
                Map<LibraryOption, Object> options = new HashMap<LibraryOption, Object>();
                options.put(LibraryOption.CallingConvention, CallingConvention.STDCALL);
                lib = Library.loadLibrary(LibServices.class, options, "Ws2_32");
            } else {
                String[] libnames = os.equals(SOLARIS)
                ? new String[] { "socket", "nsl", "c" }
                : new String[] { "c" };
                lib = Library.loadLibrary(LibServices.class, libnames);
            }

            // Try to lookup a service to make sure the library loaded and found the functions
            lib.getservbyname("bootps", "udp");
            lib.getservbyport(67, "udp");

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
        return Collections.emptyList();
    }

    private final Service serviceFromNative(UnixServent s) {
        if (s == null) {
            return null;
        }

        // servent#port is in network byte order - but jaffl will assume it is in host byte order
        // so it needs to be reversed again to be correct.
        int port = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes((short) s.port.get()) : s.port.get();
        if (port < 0) {
            // The s_port field is really an unsigned 16 bit quantity, but the
            // byte flipping above will return numbers >= 32768 as a negative value,
            // so they need to be converted back to a unsigned 16 bit value.
            port = (int) ((port & 0x7FFF) + 0x8000);
        }

        List<String> emptyAliases = Collections.emptyList();
        Pointer ptr;
        final Collection<String> aliases = ((ptr = s.aliases.get()) != null)
                ? StringUtil.getNullTerminatedStringArray(ptr) : emptyAliases;

        return s != null ? new Service(s.name.get(), port, s.proto.get(), aliases) : null;
    }

    public Service getServiceByName(String name, String proto) {
        return serviceFromNative(lib.getservbyname(name, proto));
    }

    public Service getServiceByPort(Integer port, String proto) {
        int nport = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes(port.shortValue()) : port.shortValue();
        
        return serviceFromNative(lib.getservbyport(nport, proto));
    }


}
