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

import com.kenai.jaffl.Platform;

import static com.kenai.jaffl.Platform.OS.WINDOWS;

/**
 *
 */
class FileProtocolsDB implements ProtocolsDB {
    private final File protocolsFile;

    public static final FileProtocolsDB getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        public static final FileProtocolsDB INSTANCE = load();
    }

    FileProtocolsDB(File protocolsFile) {
        this.protocolsFile = protocolsFile;
    }

    private static final File locateProtocolsFile() {
        if (Platform.getPlatform().getOS().equals(WINDOWS)) {
            String systemRoot;
            try {
                // FIXME: %SystemRoot% is typically *not* present in Java env,
                // so we need a better way to obtain the Windows location.
                // One possible solution: Win32API's SHGetFolderPath() with
                // parameter CSIDL_SYSTEM or CSIDL_WINDOWS.
                systemRoot = System.getProperty("SystemRoot", "C:\\windows");
            } catch (SecurityException se) {
                // whoops, try the most logical one
                systemRoot = "C:\\windows";
            }

            return new File(systemRoot + "\\system32\\drivers\\etc\\protocol");

        } else {
            return new File("/etc/protocols");
        }
    }

    private static FileProtocolsDB load() {
        try {
            File protocolsFile = locateProtocolsFile();
            // Fail unless /etc/protocols can be read and contains at least one valid entry
            NetDBParser parser = new NetDBParser(new FileReader(protocolsFile));
            try {
                parser.iterator().next();
            } finally {
                parser.close();
            }

            return new FileProtocolsDB(protocolsFile);

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

    private final NetDBParser loadProtocolsFile() {
        try {
            return new NetDBParser(new FileReader(protocolsFile));
        } catch (FileNotFoundException ex) {
            return new NetDBParser(new StringReader(""));
        }
    }

    private static interface Filter {
        boolean filter(Protocol s);
    }

    private final Protocol parse(Filter filter) {
        NetDBParser parser = loadProtocolsFile();

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
