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
import java.util.Collections;

/**
 * Holds information about TCP and UDP services on a host.
 */
public final class Service {
    /** Official service name */
    private final String serviceName;
    /** TCP or UDP port number */
    private final int port;

    /** Protocol to use */
    final String proto;

    /** All the aliases for this service */
    private final Collection<String> aliases;

    Service(String name, int port, String proto, Collection<String> aliases) {
        this.serviceName = name;
        this.port = port;
        this.proto = proto;
        this.aliases = aliases;
    }

    /**
     * Locates a service by name and protocol.
     *
     * @param name the official IANA name for the service, or an alias.
     * @param proto the protocol for the service.  Usually "tcp" or "udp".
     * @return a {@code Service} instance, or {@code null} if the service could not be found.
     */
    public static final Service getServiceByName(String name, String proto) {
        return getServicesDB().getServiceByName(name, proto);
    }


    /**
     * Locates a service by port and protocol.
     *
     * @param port the TCP or UDP port of the service.
     * @param proto the protocol for the service.  Usually "tcp" or "udp".
     * @return a {@code Service} instance, or {@code null} if the service could not be found.
     */
    public static final Service getServiceByPort(int port, String proto) {
        return getServicesDB().getServiceByPort(port, proto);
    }

    /**
     * Returns a list of all services.
     *
     * @return a {@code Collection} of {@code Service} instances.
     */
    public static final Collection<Service> getAllServices() {
        return Collections.emptyList();
    }

    /**
     * Returns the official IANA name of this {@code Service}
     *
     * @return The name of this {@code Service} as a {@code String}
     */
    public final String getName() {
        return serviceName;
    }

    /**
     * Returns the official IANA port for this {@code Service}
     *
     * @return The port for this {@code Service} as an {@code int}
     */
    public final int getPort() {
        return port;
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
    private static final ServicesDB getServicesDB() {
        return ServicesDBSingletonHolder.INSTANCE;
    }

    /**
     * Holds the global lazily-loaded instance of the ServicesDB
     */
    private static final class ServicesDBSingletonHolder {
        static final ServicesDB INSTANCE = load();
        
        private static final ServicesDB load() {
            // Try to use the native functions if possible
            ServicesDB db = NativeServicesDB.load();

            // Fall back to parsing /etc/services directly.
            if (db == null) {
                db = FileServicesDB.getInstance();
            }

            // As a last resort, fall back to the hard coded table
            return db != null ? db : IANAServicesDB.getInstance();
        }
    }

    @Override
    public String toString() {
        return String.format("<Service: Name: %s, Port: %d, Proto: %s, Aliases: %s>",
                serviceName, port, proto, aliases);
    };
}
