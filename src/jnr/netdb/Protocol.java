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

import java.util.Collection;

/**
 * An entry in the system protocol database
 */
public class Protocol {
    /** Official protocol name */
    private final String protocolName;

    private final int proto;

    /** All the aliases for this service */
    private final Collection<String> aliases;

    Protocol(String name, int proto, Collection<String> aliases) {
        this.protocolName = name;
        this.proto = proto;
        this.aliases = aliases;
    }

    /**
     * Looks up a protocol by name.
     *
     * @param name the official IANA name for the protocol, or an alias.
     * @return a {@code Protocol} instance, or {@code null} if the protocol could not be found.
     */
    public static final Protocol getProtocolByName(String name) {
        return getProtocolDB().getProtocolByName(name);
    }


    /**
     * Looks up a protocol by number.
     *
     * @param name the internet protocol number of the protocol.
     * @return a {@code Protocol} instance, or {@code null} if the protocol could not be found.
     */
    public static final Protocol getProtocolByNumber(int port) {
        return getProtocolDB().getProtocolByNumber(port);
    }


    /**
     * Gets the official IANA name of this {@code Protocol}
     *
     * @return The name of this {@code Protocol} as a {@code String}
     */
    public final String getName() {
        return protocolName;
    }

    /**
     * Gets the official IANA port for this {@code Service}
     *
     * @return The port for this {@code Service} as an {@code int}
     */
    public final int getProto() {
        return proto;
    }

    /**
     * Gets a list of aliases this {@code Service} is also known as.
     *
     * @return A {@code Collection} of Strings for aliases this {@code Service}
     */
    public final Collection<String> getAliases() {
        return aliases;
    }

    /**
     * Returns the currently loaded ServicesDB
     *
     * @return an instance of {@code ServicesDB}
     */
    private static final ProtocolDB getProtocolDB() {
        return ProtocolDBSingletonHolder.INSTANCE;
    }

    /**
     * Holds the global lazily-loaded instance of the ServicesDB
     */
    private static final class ProtocolDBSingletonHolder {
        static final ProtocolDB INSTANCE = load();

        private static final ProtocolDB load() {
            // Try to use the native functions if possible
            ProtocolDB db = NativeProtocolDB.getInstance();

            // Fall back to parsing /etc/services directly.
            if (db == null) {
                db = FileProtocolDB.getInstance();
            }

            // As a last resort, fall back to the hard coded table
            return db != null ? db : IANAProtocolDB.getInstance();
        }
    }
}
