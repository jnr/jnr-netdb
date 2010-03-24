
package jnr.netdb;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 *
 */
class NetDBParser implements Iterable<NetDBEntry>, Closeable {
    private final Reader reader;

    public NetDBParser(Reader r) {
        this.reader = r;
    }

    public Iterator<NetDBEntry> iterator() {
        return new NetDBIterator(reader);
    }
    
    public void close() throws IOException {
        reader.close();
    }
}
