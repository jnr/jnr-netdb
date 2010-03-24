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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Parses /etc/services
 */
final class FileServicesDB implements ServicesDB {

    static final ServicesDB load() {
        try {
            // Fail unless /etc/services can be read and contains at least one valid entry
            Service s = openServicesDB().next();
            
            return s != null ? new FileServicesDB() : null;

        } catch (Throwable t) {
            return null;
        }
    }

    static final ServicesIterator openServicesDB() {
        try {
            return new ServicesIterator(new FileReader(new File("/etc/services")));
        } catch (FileNotFoundException ex) {
            return new ServicesIterator(new StringReader(""));
        }
    }

    private static final class ServicesIterator implements java.util.Iterator<Service>, Closeable {
        private final BufferedReader reader;
        private Service next = null;

        public ServicesIterator(Reader r) {
            this.reader = new BufferedReader(r);
        }

        Service readNextEntry() {
            try {

                while (true) {
                    String s = reader.readLine();
                    if (s == null) {
                        break;
                    }

                    String[] line = s.split("#", 2);
                    // Skip empty lines, or lines that are all comment
                    if (line.length < 0 || line[0].isEmpty()) {
                        continue;
                    }
                    
                    String[] fields = line[0].split("\\s+");
                    
                    if (fields.length < 2 || fields[0] == null || fields[1] == null) {
                        continue;
                    }

                    String serviceName = fields[0];
                    String[] portproto = fields[1].split("/");
                    if (portproto.length < 2) {
                        continue;
                    }
                    
                    int port;
                    try {
                        port = Integer.parseInt(portproto[0], 10);
                    } catch (NumberFormatException ex) {
                        continue;
                    }

                    String proto = portproto[1];

                    List<String> aliases;
                    if (fields.length > 2) {
                        aliases = new ArrayList<String>(fields.length - 2);
                        for (int i = 2; i < fields.length; ++i) {
                            if (fields[i] != null) {
                                aliases.add(fields[i]);
                            }
                        }
                    } else {
                        aliases = Collections.EMPTY_LIST;
                    }

                    return new Service(serviceName, port, proto, aliases);
                }
            } catch (IOException ex) {
                throw new NoSuchElementException(ex.getMessage());
            }
            
            return null;
        }

        public boolean hasNext() {
            try {
                return next != null || (next = readNextEntry()) != null;
            } catch (NoSuchElementException ex) {
                return false;
            }
        }

        public Service next() {
            Service s = next != null ? next : readNextEntry();
            next = null;
            return s;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void close() throws IOException {
            reader.close();
        }

    }

    public Service getServiceByName(String name, String proto) {
        ServicesIterator it = openServicesDB();
        
        while (it.hasNext()) {
            Service s = it.next();

            if (!s.proto.equals(proto) && proto != null) {
                continue;
            }

            if (s.getName().equals(name)) {
                return s;
            }

            for (String alias : s.getAliases()) {
                if (alias.equals(name)) {
                    return s;
                }
            }
        }
        
        try {
            it.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    public Service getServiceByPort(Integer port, String proto) {
        ServicesIterator it = openServicesDB();

        while (it.hasNext()) {
            Service s = it.next();
            if (s.getPort() == port.intValue() && (s.proto.equals(proto) || proto == null)) {
                return s;
            }
        }

        try {
            it.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return null;
    }

    public Collection<Service> getAllServices() {

        ServicesIterator it = openServicesDB();
        List<Service> allServices = new LinkedList<Service>();

        while (it.hasNext()) {
            allServices.add(it.next());
        }

        return Collections.unmodifiableList(allServices);
    }
}
