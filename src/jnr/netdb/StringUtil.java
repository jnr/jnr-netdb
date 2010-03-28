
package jnr.netdb;

import com.kenai.jaffl.Platform;
import com.kenai.jaffl.Pointer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for native strings
 */
class StringUtil {
    public static final int POINTER_SIZE = Platform.getPlatform().addressSize() / 8;

    public static final List<String> getNullTerminatedStringArray(Pointer ptr) {
        Pointer p;
        
        // If it is an empty list, do not allocate an empty ArrayList for it
        if ((p = ptr.getPointer(0)) == null) {
            return Collections.emptyList();
        }

        List<String> array = new ArrayList<String>();
        array.add(p.getString(0));
        
        for (int off = POINTER_SIZE; (p = ptr.getPointer(off)) != null; off += POINTER_SIZE) {
            array.add(p.getString(0));
        }

        return array;
    }
}
