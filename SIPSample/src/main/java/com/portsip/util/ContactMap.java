package com.portsip.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nigussie on 12.10.2015.
 */
public class ContactMap extends HashMap<String, String> {
    /*
         * (non-Javadoc)
         *
         * @see java.util.AbstractMap#toString()
         */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder buffer = new StringBuilder(size() * 28);
        Iterator<Map.Entry<String, String>> it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            Object key = entry.getKey();

            if (key == "Name") {
                Object value = entry.getValue();
                buffer.append(value);
            } else {
                if (key == "Phone")
                    buffer.append("<");
                Object value = entry.getValue();
                if (value != this) {
                    buffer.append(value);
                } else {
                    buffer.append("(this Map)");
                }
                if (key == "Phone")
                    buffer.append(">");

            }
        }

        return buffer.toString();
    }

}
