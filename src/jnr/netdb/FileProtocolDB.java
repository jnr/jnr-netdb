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
 *
 */
class FileProtocolDB implements ProtocolDB {

    public static final FileProtocolDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final FileProtocolDB INSTANCE = load();
    }

    FileProtocolDB() {
    }

    private static FileProtocolDB load() {
        try {
            // Fail unless /etc/protocols can be read and contains at least one valid entry
            NetDBParser parser = parseProtocolsFile();
            try {
                parser.iterator().next();
            } finally {
                parser.close();
            }

            return new FileProtocolDB();

        } catch (Throwable t) {
            return null;
        }
    }

    public Protocol getProtocolByName(final String name) {

        return parse(new Filter() {

            public boolean filter(Protocol p) {
                if (p.getName().equals(name)) {
                    return true;
                }

                for (String alias : p.getAliases()) {
                    if (alias.equals(name)) {
                        return true;
                    }
                }

                return false;
            }
        });
    }

    public Protocol getProtocolByNumber(final Integer proto) {
        return parse(new Filter() {

            public boolean filter(Protocol p) {
                return p.getProto() == proto.intValue();
            }
        });
    }

    public Collection<Protocol> getAllProtocols() {

        final List<Protocol> allProtocols = new LinkedList<Protocol>();

        parse(new Filter() {

            public boolean filter(Protocol s) {
                allProtocols.add(s);
                return false;
            }
        });

        return Collections.unmodifiableList(allProtocols);
    }

    static final NetDBParser parseProtocolsFile() {
        try {
            return new NetDBParser(new FileReader(new File("/etc/protocols")));
        } catch (FileNotFoundException ex) {
            return new NetDBParser(new StringReader(""));
        }
    }

    private static interface Filter {
        boolean filter(Protocol s);
    }

    private final Protocol parse(Filter filter) {
        NetDBParser parser = parseProtocolsFile();

        try {
            for (NetDBEntry e : parser) {
                try {
                    Protocol p = new Protocol(e.name, Integer.parseInt(e.data, 10), e.aliases);
                    if (filter.filter(p)) {
                        return p;
                    }
                } catch (NumberFormatException ex) {}
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
}
