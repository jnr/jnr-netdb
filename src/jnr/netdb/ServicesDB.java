
package jnr.netdb;

import java.util.Collection;

/**
 *
 */
interface ServicesDB {
    public abstract Service getServiceByName(String name, String proto);
    public abstract Service getServiceByPort(Integer port, String proto);
    public abstract Collection<Service> getAllServices();
}
