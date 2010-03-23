
package jnr.netdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Holds information about TCP and UDP services on a host.
 */
public final class Service {
    /** Official service name */
    private final String serviceName;
    /** TCP or UDP port number */
    private final int port;

    /** Protocol to use */
    private final String proto;

    /** All the aliases for this service */
    private final List<String> aliases;

    Service(String name, int port, String proto, Collection<String> aliases) {
        this.serviceName = name;
        this.port = port;
        this.proto = proto;
        this.aliases = Collections.unmodifiableList(new ArrayList<String>(aliases));
    }

    /**
     * Looks up a service by name and protocol.
     *
     * @param name the official IANA name for the service, or an alias.
     * @param proto the protocol for the service.  Usually "tcp" or "udp".
     * @return a {@code Service} instance, or {@code null} if the service could not be found.
     */
    public static final Service getServiceByName(String name, String proto) {
        return getServicesDB().getServiceByName(name, proto);
    }


    /**
     * Looks up a service by port and protocol.
     *
     * @param name the TCP or UDP port of the service.
     * @param proto the protocol for the service.  Usually "tcp" or "udp".
     * @return a {@code Service} instance, or {@code null} if the service could not be found.
     */
    public static final Service getServiceByPort(int port, String proto) {
        return getServicesDB().getServiceByPort(port, proto);
    }

    /**
     * Gets a list of all services.
     *
     * @return a {@code Collection} of {@code Service} instances.
     */
    public static final Collection<Service> getAllServices() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Gets the official IANA name of this {@code Service}
     *
     * @return The name of this {@code Service} as a {@code String}
     */
    public final String getName() {
        return serviceName;
    }

    /**
     * Gets the official IANA port for this {@code Service}
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
            ServicesDB db = NativeServicesDB.load();

            return db != null ? db : IANAServices.getInstance();
        }
    }
}
