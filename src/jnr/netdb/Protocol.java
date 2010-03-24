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
public final class Protocol {
    /** Official protocol name */
    private final String protocolName;

    private final int proto;

    /** All the aliases for this protocol */
    private final Collection<String> aliases;

    Protocol(String name, int proto, Collection<String> aliases) {
        this.protocolName = name;
        this.proto = proto;
        this.aliases = aliases;
    }

    /**
     * Locates a protocol by name.
     *
     * @param name the official IANA name for the protocol, or an alias.
     * @return a {@code Protocol} instance, or {@code null} if the protocol could not be found.
     */
    public static final Protocol getProtocolByName(String name) {
        return getProtocolDB().getProtocolByName(name);
    }


    /**
     * Locates a protocol by number.
     *
     * @param proto the internet protocol number of the protocol.
     * @return a {@code Protocol} instance, or {@code null} if the protocol could not be found.
     */
    public static final Protocol getProtocolByNumber(int proto) {
        return getProtocolDB().getProtocolByNumber(proto);
    }


    /**
     * Returns the official IANA name of this {@code Protocol}
     *
     * @return The name of this {@code Protocol} as a {@code String}
     */
    public final String getName() {
        return protocolName;
    }

    /**
     * Returns the official IANA protocol number for this {@code Protocol}
     *
     * @return The protocol number for this {@code Protocol} as an {@code int}
     */
    public final int getProto() {
        return proto;
    }

    /**
     * Returns a list of aliases this {@code Protocol} is also known by.
     *
     * @return A {@code Collection} of Strings for aliases this {@code Protocol}
     */
    public final Collection<String> getAliases() {
        return aliases;
    }

    /**
     * Returns the currently loaded ProtocolDB
     *
     * @return an instance of {@code ProtocolDB}
     */
    private static final ProtocolsDB getProtocolDB() {
        return ProtocolDBSingletonHolder.INSTANCE;
    }

    /**
     * Holds the global lazily-loaded instance of the ProtocolDB
     */
    private static final class ProtocolDBSingletonHolder {
        static final ProtocolsDB INSTANCE = load();

        private static final ProtocolsDB load() {
            // Try to use the native functions if possible
            ProtocolsDB db = NativeProtocolsDB.getInstance();

            // Fall back to parsing /etc/protocols directly.
            if (db == null) {
                db = FileProtocolsDB.getInstance();
            }

            // As a last resort, fall back to the hard coded table
            return db != null ? db : IANAProtocolsDB.getInstance();
        }
    }
}
