package torch.route.container;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticRouteContainer {

    private static final ArrayList<Integer> EMPTY_ARRAY_LIST = new ArrayList<>();
    private final HashMap<String, ArrayList<Integer>> staticRouteParts = new HashMap<>();

    public void addRoutePart(String part, int level, int urlTargetId) {
        if (!staticRouteParts.containsKey(part + "_" + level)) {
            staticRouteParts.put(part + "_" + level, new ArrayList<Integer>());
        }

        staticRouteParts.get(part + "_" + level).add(urlTargetId);
    }

    public ArrayList<Integer> getRoutePartPossibleTargets(String part, int level) {
        if (containsRoutePart(part, level)) {
            return staticRouteParts.get(part + "_" + level);
        }

        return EMPTY_ARRAY_LIST;
    }

    private boolean containsRoutePart(String part, int level) {
        return staticRouteParts.containsKey(part + "_" + level);
    }
}
