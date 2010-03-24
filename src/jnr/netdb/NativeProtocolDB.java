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

import com.kenai.jaffl.Library;
import com.kenai.jaffl.Platform;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.kenai.jaffl.Platform.OS.*;

/**
 *
 */
final class NativeProtocolDB implements ProtocolDB {

    private final LibProto lib;

    public static final NativeProtocolDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final NativeProtocolDB INSTANCE = load();
    }

    NativeProtocolDB(LibProto lib) {
        this.lib = lib;
    }

    private static final NativeProtocolDB load() {
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

            LibProto lib = Library.loadLibrary(LibProto.class, libnames);
            // Try to lookup a protocol to make sure the library loaded and found the functions
            lib.getprotobyname("ip");
            lib.getprotobynumber(0);
            
            return new NativeProtocolDB(lib);
        } catch (Throwable t) {
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
    }
    
    public Collection<Service> getAllServices() {
        return Collections.emptyList();
    }

    private final Protocol protocolFromNative(UnixProtoent s) {
        // servent#port is in network byte order - but jaffl will assume it is in host byte order
        // so it needs to be reversed to be correct.
        int proto = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes((short) s.proto.get()) : s.proto.get();

        List<String> aliases = Collections.emptyList();
        return s != null ? new Protocol(s.name.get(), proto, aliases) : null;
    }

    public Protocol getProtocolByName(String name) {
        return protocolFromNative(lib.getprotobyname(name));
    }

    public Protocol getProtocolByNumber(Integer proto) {
        int nproto = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? Short.reverseBytes(proto.shortValue()) : proto.shortValue();
        
        return protocolFromNative(lib.getprotobynumber(nproto));
    }

    public Collection<Protocol> getAllProtocols() {
        return Collections.EMPTY_LIST;
    }

}
