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
abstract class NativeProtocolsDB implements ProtocolsDB {

    public static final NativeProtocolsDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final NativeProtocolsDB INSTANCE = load();
    }

    private static final NativeProtocolsDB load() {
        try {
            Platform.OS os = Platform.getNativePlatform().getOS();

            // The protoent struct is only known to match on Windows, MacOSX, Linux, Solaris.
            // We assume FreeBSD and NetBSD also match.
            if (!(os.equals(DARWIN) || (os.equals(WINDOWS) && Platform.getNativePlatform().getCPU() == Platform.CPU.I386)
                    || os.equals(LINUX) || os.equals(SOLARIS)
                    || os.equals(FREEBSD) || os.equals(NETBSD))) {
                return null;
            }

            LibProto lib;
            if (os.equals(WINDOWS)) {
                Map<LibraryOption, Object> options = new HashMap<LibraryOption, Object>();
                options.put(LibraryOption.CallingConvention, CallingConvention.STDCALL);
                lib = Library.loadLibrary(LibProto.class, options, "Ws2_32");
            } else {
                String[] libnames = os.equals(SOLARIS)
                        ? new String[]{"socket", "nsl", "c"}
                        : new String[]{"c"};
                lib = os.equals(LINUX)
                    ? Library.loadLibrary(LinuxLibProto.class, libnames)
                    : Library.loadLibrary(LibProto.class, libnames);
            }

            NativeProtocolsDB protocolsDB = os.equals(LINUX)
                    ? new LinuxNativeProtocolsDB((LinuxLibProto) lib)
                    : new DefaultNativeProtocolsDB(lib);
            // Try to lookup a protocol to make sure the library loaded and found the functions
            protocolsDB.getProtocolByName("ip");
            protocolsDB.getProtocolByNumber(0);
            
            return protocolsDB;
        } catch (Throwable t) {
            Logger.getLogger(NativeProtocolsDB.class.getName()).log(Level.WARNING, "Failed to load native protocols db", t);
            return null;
        }
    }

    public static class UnixProtoent extends jnr.ffi.Struct {
        public final String name = new UTF8StringRef();
        public final Pointer aliases = new Pointer();
        public final Signed32 proto = new Signed32();

        public UnixProtoent(jnr.ffi.Runtime runtime) {
            super(runtime);
        }
    }

    public static interface LibProto {
        UnixProtoent getprotobyname(String name);
        UnixProtoent getprotobynumber(int proto);
        UnixProtoent getprotoent();
        void setprotoent(int stayopen);
        void endprotoent();
    }

    public static interface LinuxLibProto extends LibProto{
        int getprotobyname_r(String proto, @Direct UnixProtoent protoent, Pointer buf, NativeLong buflen, Pointer result);
        int getprotobynumber_r(int proto, @Direct UnixProtoent protoent, Pointer buf, NativeLong buflen, Pointer result);
        int getprotoent_r(@Direct UnixProtoent protoent, Pointer buf, NativeLong buflen, Pointer result);
    }

    static Protocol protocolFromNative(UnixProtoent p) {
        if (p == null) {
            return null;
        }

        List<String> emptyAliases = Collections.emptyList();

        Pointer ptr;
        final Collection<String> aliases = ((ptr = p.aliases.get()) != null)
                ? StringUtil.getNullTerminatedStringArray(ptr) : emptyAliases;

        return new Protocol(p.name.get(), (short) p.proto.get(), aliases);
    }

    static final class DefaultNativeProtocolsDB extends NativeProtocolsDB {
        private final LibProto lib;

        DefaultNativeProtocolsDB(LibProto lib) {
            this.lib = lib;
        }

        public synchronized Protocol getProtocolByName(String name) {
            return protocolFromNative(lib.getprotobyname(name));
        }

        public synchronized Protocol getProtocolByNumber(Integer proto) {
            return protocolFromNative(lib.getprotobynumber(proto));
        }

        public synchronized Collection<Protocol> getAllProtocols() {
            UnixProtoent p;
            List<Protocol> allProtocols = new ArrayList<Protocol>();

            lib.setprotoent(0);
            try {
                while ((p = lib.getprotoent()) != null) {
                    allProtocols.add(protocolFromNative(p));
                }
            } finally {
                lib.endprotoent();
            }

            return allProtocols;
        }
    }

    static final class LinuxNativeProtocolsDB extends NativeProtocolsDB {
        private static final int BUFLEN = 4096;
        private final Runtime runtime;
        private final Pointer buf;
        private final LinuxLibProto lib;


        LinuxNativeProtocolsDB(LinuxLibProto lib) {
            this.lib = lib;
            this.runtime = Library.getRuntime(lib);
            this.buf = Memory.allocateDirect(runtime, BUFLEN);
        }

        public synchronized Protocol getProtocolByName(String name) {
            UnixProtoent protoent = new UnixProtoent(runtime);
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            if (lib.getprotobyname_r(name, protoent, buf, new NativeLong(BUFLEN), result) == 0) {
                return result.getPointer(0) != null ? protocolFromNative(protoent) : null;
            }

            throw new RuntimeException("getprotobyname_r failed");
        }

        public synchronized Protocol getProtocolByNumber(Integer number) {
            UnixProtoent protoent = new UnixProtoent(runtime);
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            if (lib.getprotobynumber_r(number, protoent, buf, new NativeLong(BUFLEN), result) == 0) {
                return result.getPointer(0) != null ? protocolFromNative(protoent) : null;
            }

            throw new RuntimeException("getprotobynumber_r failed");
        }

        public synchronized Collection<Protocol> getAllProtocols() {
            UnixProtoent p = new UnixProtoent(runtime);
            List<Protocol> allProtocols = new ArrayList<Protocol>();
            Pointer result = Memory.allocateDirect(runtime, runtime.addressSize());
            NativeLong buflen = new NativeLong(BUFLEN);

            lib.setprotoent(0);
            try {
                while (lib.getprotoent_r(p, buf, buflen, result) == 0 && result.getPointer(0) != null) {
                    allProtocols.add(protocolFromNative(p));
                }
            } finally {
                lib.endprotoent();
            }

            return allProtocols;
        }
    }
}
