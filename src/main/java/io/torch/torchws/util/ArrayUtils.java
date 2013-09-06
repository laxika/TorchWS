package io.torch.torchws.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    /**
     * Calculate the intersection of two list.
     * 
     * @param <T>
     * @param list1
     * @param list2
     * @return 
     */
    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
}
