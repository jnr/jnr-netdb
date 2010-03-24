package jnr.netdb;

import java.util.Collection;

final class NetDBEntry {

    final String name;
    final String data;
    final Collection<String> aliases;

    NetDBEntry(String name, String data, Collection<String> aliases) {
        this.name = name;
        this.data = data;
        this.aliases = aliases;
    }
}
