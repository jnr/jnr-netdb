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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Parses /etc/services
 */
final class FileServicesDB implements ServicesDB {
    public static String fileName = "/etc/services";

    public static final ServicesDB getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    private static final class SingletonHolder {
        public static final ServicesDB INSTANCE = load();
    }

    private static final ServicesDB load() {
        try {
            // Fail unless /etc/services can be read and contains at least one valid entry
            NetDBParser parser = parseServicesFile();
            try {
                parser.iterator().next();
            } finally {
                parser.close();
            }
            
            return new FileServicesDB();

        } catch (Throwable t) {
            return null;
        }
    }

    static final NetDBParser parseServicesFile() {
        try {
            return new NetDBParser(new FileReader(new File(fileName)));
        } catch (FileNotFoundException ex) {
            return new NetDBParser(new StringReader(""));
        }
    }

    private static final Service parseServicesEntry(NetDBEntry e) {
        
        String[] portproto = e.data.split("/");
        if (portproto.length < 2) {
            return null;
        }

        int port;
        try {
            port = Integer.parseInt(portproto[0], 10);
        } catch (NumberFormatException ex) {
            return null;
        }

        return new Service(e.name, port, portproto[1], e.aliases);
    }

    private static interface Filter {
        boolean filter(Service s);
    }

    private final Service parse(Filter filter) {
        NetDBParser parser = parseServicesFile();

        try {
            for (NetDBEntry e : parser) {
                Service s = parseServicesEntry(e);
                if (s != null && filter.filter(s)) {
                    return s;
                }
            }

        } finally {
            try {
                parser.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return null;
    }

    public Service getServiceByName(final String name, final String proto) {
        return parse(new Filter() {

            public boolean filter(Service s) {
                if (!s.proto.equals(proto) && proto != null) {
                    return false;
                }

                if (s.getName().equals(name)) {
                    return true;
                }

                for (String alias : s.getAliases()) {
                    if (alias.equals(name)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    public Service getServiceByPort(final Integer port, final String proto) {
        return parse(new Filter() {

            public boolean filter(Service s) {
                return s.getPort() == port.intValue() && (s.proto.equals(proto) || proto == null);
            }
        });
    }

    public Collection<Service> getAllServices() {

        final List<Service> allServices = new LinkedList<Service>();

        parse(new Filter() {

            public boolean filter(Service s) {
                allServices.add(s);
                return false;
            }
        });
        
        return Collections.unmodifiableList(allServices);
    }
}
