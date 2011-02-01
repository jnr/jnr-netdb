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

    @Override
    public String toString() {
        return String.format("<Protocol: Name: %s, Proto: %d, Aliases: %s>",
                protocolName, proto, aliases);
    };
}
