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
import jnr.ffi.Runtime;
import jnr.ffi.annotations.Direct;
import jnr.ffi.annotations.Out;

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
abstract class NativeServicesDB implements ServicesDB {

    protected final LibServices lib;

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

                if (os.equals(LINUX)) {
                    lib = Library.loadLibrary(LinuxLibServices.class, libnames);
                } else {
                    lib = Library.loadLibrary(LibServices.class, libnames);
                }
            }

            NativeServicesDB services = os.equals(LINUX)
                    ? new LinuxServicesDB(lib)
                    : new DefaultNativeServicesDB(lib);
            // Try to lookup a service to make sure the library loaded and found the functions
            if (services.getServiceByName("comsat", "udp") == null) {
                return null;
            }
            services.getServiceByName("bootps", "udp");
            services.getServiceByPort(67, "udp");

            return services;

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

    public static class LinuxServent extends UnixServent {
        public static final int BUFLEN = 4096;
        public final jnr.ffi.Pointer buf;

        public LinuxServent(jnr.ffi.Runtime runtime) {
            super(runtime);
            this.buf = Memory.allocateDirect(runtime, BUFLEN, true);
        }
    }

    public static interface LibServices {
        UnixServent getservbyname(String name, String proto);
        UnixServent getservbyport(Integer port, String proto);
        UnixServent getservent();
        void endservent();
    }

    public static interface LinuxLibServices extends LibServices {
        int getservbyname_r(String name, String proto, @Direct UnixServent servent,
                                    Pointer buf, NativeLong buflen, @Out Pointer result);
        int getservbyport_r(Integer port, String proto, @Direct UnixServent servent,
                                    Pointer buf, NativeLong buflen, @Out Pointer result);
        int getservent_r(@Direct UnixServent servent,
                         Pointer buf, NativeLong buflen, Pointer result);
    }

    static int ntohs(int value) {
        int hostValue =  ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes((short) value) : value;
        if (hostValue < 0) {
            // The byte flipping above will return numbers >= 32768 as a negative value,
            // so they need to be converted back to a unsigned 16 bit value.
            hostValue = ((hostValue & 0x7FFF) + 0x8000);
        }

        return hostValue;
    }

    static int htons(int value) {
        return ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes((short) value) : value;
    }

    static Service serviceFromNative(UnixServent s) {
        if (s == null) {
            return null;
        }

        List<String> emptyAliases = Collections.emptyList();
        Pointer ptr;
        final Collection<String> aliases = ((ptr = s.aliases.get()) != null)
                ? StringUtil.getNullTerminatedStringArray(ptr) : emptyAliases;

        return new Service(s.name.get(), ntohs(s.port.get()), s.proto.get(), aliases);
    }

    static final class DefaultNativeServicesDB extends NativeServicesDB {
        DefaultNativeServicesDB(LibServices lib) {
            super(lib);
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


        public Service getServiceByName(String name, String proto) {
            return serviceFromNative(lib.getservbyname(name, proto));
        }

        public Service getServiceByPort(Integer port, String proto) {
            return serviceFromNative(lib.getservbyport(htons(port), proto));
        }
    }

    static final class LinuxServicesDB extends NativeServicesDB {
        private static final int BUFLEN = 4096;
        private final LinuxLibServices lib;
        private final Runtime runtime;
        private final Pointer buf;

        LinuxServicesDB(LibServices lib) {
            super(lib);
            this.lib = (LinuxLibServices) lib;
            this.runtime = Library.getRuntime(lib);
            this.buf = Memory.allocateDirect(runtime, BUFLEN);
        }

        public synchronized Service getServiceByName(String name, String proto) {
            UnixServent servent = new UnixServent(runtime);
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            if (lib.getservbyname_r(name, proto, servent, buf, new NativeLong(BUFLEN), result) == 0) {
                return result.getPointer(0) != null ? serviceFromNative(servent) : null;
            }

            throw new RuntimeException("getservbyname_r failed");
        }

        public synchronized Service getServiceByPort(Integer port, String proto) {
            UnixServent servent = new UnixServent(runtime);
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            if (lib.getservbyport_r(htons(port), proto, servent, buf, new NativeLong(BUFLEN), result) == 0) {
                return result.getPointer(0) != null ? serviceFromNative(servent) : null;
            }

            throw new RuntimeException("getservbyport_r failed");
        }

        public synchronized Collection<Service> getAllServices() {
            UnixServent s = new UnixServent(runtime);
            List<Service> allServices = new ArrayList<Service>();
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            NativeLong buflen = new NativeLong(BUFLEN);

            try {
                while (lib.getservent_r(s, buf, buflen, result) == 0 && result.getPointer(0) != null) {
                    allServices.add(serviceFromNative(s));
                }
            } finally {
                lib.endservent();
            }

            return allServices;
        }
    }

}
