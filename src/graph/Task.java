package graph;

import java.util.ArrayList;
import java.util.List;

public class Task implements Comparable<Task> {
    String taskName;
    int estimateRunTime;
    List<String> dependencies = new ArrayList<>();

    @Override
    public int compareTo(Task o) {
        if (estimateRunTime > o.estimateRunTime) {
            return 1;
        } else if (estimateRunTime < o.estimateRunTime) {
            return -1;
        }
        return 0;
    }
}
