package torch.route.container;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicRouteContainer {

    private static final ArrayList<Integer> EMPTY_ARRAY_LIST = new ArrayList<>();
    private final HashMap<Integer, ArrayList<Integer>> dynamicRouteParts = new HashMap<>();

    public void addRoutePart(int level, int urlTargetId) {
        if (!containsRoutePart(level)) {
            dynamicRouteParts.put(level, new ArrayList<Integer>());
        }

        dynamicRouteParts.get(level).add(urlTargetId);
    }

    public ArrayList<Integer> getRoutePartPossibleTargets(int level) {
        if (containsRoutePart(level)) {
            return dynamicRouteParts.get(level);
        }

        return EMPTY_ARRAY_LIST;
    }

    private boolean containsRoutePart(int level) {
        return dynamicRouteParts.containsKey(level);
    }
}
