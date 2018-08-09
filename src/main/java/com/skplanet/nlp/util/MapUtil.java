package com.skplanet.nlp.util;

import java.util.*;

/**
 * @author Donghun Shin, donghun.shin@sk.com
 * @date 8/5/14.
 */
public final class MapUtil {

    public static final int SORT_DESCENDING = 0;
    public static final int SORT_ASCENDING = 1;

    /**
     * Sort the given {@link java.util.Map} by its value
     *
     * @param map map instance to be sorted
     * @param <K> key type
     * @param <V> value type
     * @return map object sorted by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final int order) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

                if (order == SORT_DESCENDING) {
                    /* for descending order */
                    return (o1.getValue()).compareTo(o2.getValue()) * -1;
                } else {
                    /* for ascending order */
                    return (o1.getValue()).compareTo(o2.getValue());
                }


            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * private constructor
     */
    private MapUtil() {

    }
}
