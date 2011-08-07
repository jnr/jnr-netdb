/*
 * Copyright (C) 2010 Wayne Meissner
 *
 * This file is part of jnr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jnr.netdb;

import jnr.ffi.*;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jnr.ffi.Platform.OS.*;

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
            Platform.OS os = Platform.getNativePlatform().getOS();

            // The ServiceEntry struct is only known to match on Windows, MacOSX, Linux, Solaris.
            // We assume FreeBSD and NetBSD also match.
            if (!(os.equals(DARWIN) || (os.equals(WINDOWS) && Platform.getNativePlatform().getCPU() == Platform.CPU.I386)
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
            Logger.getLogger(NativeServicesDB.class.getName()).log(Level.WARNING, "Failed to load native services db", t);
            return null;
        }
    }

    public static class UnixServent extends jnr.ffi.Struct {
        public final String name = new UTF8StringRef();
        public final Pointer aliases = new Pointer();
        public final Signed32 port = new Signed32();
        public final String proto = new UTF8StringRef();

        public UnixServent(jnr.ffi.Runtime runtime) {
            super(runtime);
        }
    }

    public static interface LibServices {
        UnixServent getservbyname(String name, String proto);
        UnixServent getservbyport(Integer port, String proto);
        UnixServent getservent();
        void endservent();
    }
    
    public Collection<Service> getAllServices() {
        UnixServent s;
        List<Service> allServices = new ArrayList<Service>();

        try {
            while ((s = lib.getservent()) != null) {
                allServices.add(serviceFromNative(s));
            }
        } finally {
            lib.endservent();
        }

        return allServices;
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
