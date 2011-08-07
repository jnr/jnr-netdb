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

import jnr.ffi.Platform;
import jnr.ffi.Pointer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for native strings
 */
class StringUtil {

    public static final List<String> getNullTerminatedStringArray(Pointer ptr) {
        // If it is an empty list, do not allocate an empty ArrayList for it
        if (ptr == null || ptr.getPointer(0) == null) {
            return Collections.emptyList();
        }

        final int pointerSize = ptr.getRuntime().addressSize();

        List<String> array = new ArrayList<String>();

        Pointer p;
        for (int off = 0; (p = ptr.getPointer(off)) != null; off += pointerSize) {
            array.add(p.getString(0));
        }

        return array;
    }
}
