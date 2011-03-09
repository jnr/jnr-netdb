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

import jnr.ffi.CallingConvention;
import jnr.ffi.Library;
import jnr.ffi.LibraryOption;
import jnr.ffi.Platform;
import jnr.ffi.Pointer;
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
final class NativeProtocolsDB implements ProtocolsDB {

    private final LibProto lib;

    public static final NativeProtocolsDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final NativeProtocolsDB INSTANCE = load();
    }

    NativeProtocolsDB(LibProto lib) {
        this.lib = lib;
    }

    private static final NativeProtocolsDB load() {
        try {
            Platform.OS os = Platform.getPlatform().getOS();

            // The protoent struct is only known to match on Windows, MacOSX, Linux, Solaris.
            // We assume FreeBSD and NetBSD also match.
            if (!(os.equals(DARWIN) || (os.equals(WINDOWS) && Platform.getPlatform().getCPU() == Platform.CPU.I386)
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
                ? new String[] { "socket", "nsl", "c" }
                : new String[] { "c" };
                lib = Library.loadLibrary(LibProto.class, libnames);
            }
                        
            // Try to lookup a protocol to make sure the library loaded and found the functions
            lib.getprotobyname("ip");
            lib.getprotobynumber(0);
            
            return new NativeProtocolsDB(lib);
        } catch (Throwable t) {
            Logger.getLogger(NativeProtocolsDB.class.getName()).log(Level.WARNING, "Failed to load native protocols db", t);
            return null;
        }
    }

    public static class UnixProtoent extends jnr.ffi.struct.Struct {
        public final String name = new UTF8StringRef();
        public final Pointer aliases = new Pointer();
        public final Signed32 proto = new Signed32();
    }

    public static interface LibProto {
        UnixProtoent getprotobyname(String name);
        UnixProtoent getprotobynumber(int proto);
        UnixProtoent getprotoent();
        void endprotoent();
    }
    
    private final Protocol protocolFromNative(UnixProtoent p) {
        if (p == null) {
            return null;
        }

        List<String> emptyAliases = Collections.emptyList();

        Pointer ptr;
        final Collection<String> aliases = ((ptr = p.aliases.get()) != null)
                ? StringUtil.getNullTerminatedStringArray(ptr) : emptyAliases;

        return new Protocol(p.name.get(), (short) p.proto.get(), aliases);
    }

    public Protocol getProtocolByName(String name) {
        return protocolFromNative(lib.getprotobyname(name));
    }

    public Protocol getProtocolByNumber(Integer proto) {
        return protocolFromNative(lib.getprotobynumber(proto));
    }

    public Collection<Protocol> getAllProtocols() {
        UnixProtoent p;
        List<Protocol> allProtocols = new ArrayList<Protocol>();

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
