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
import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.kenai.jaffl.Platform.OS.*;

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

    public static class UnixProtoent extends com.kenai.jaffl.struct.Struct {
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
