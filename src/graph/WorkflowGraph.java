package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkflowGraph {

    List<Task> tasks = new ArrayList<>();
    String criticalPath = "";
    int totalEstimatedRuntime = 0;

    /**
     * this function initialize Task node using the data contains in the DAG File
     * 
     * @param the name of the DAG file. DAG file must be plaintext file
     */
    public void readDagFile(String dagFile) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/" + dagFile + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("JOB")) {
                    String[] jobLine = line.split(" ");
                    String taskName = jobLine[1];
                    String subFileName = jobLine[2];
                    // System.out.print("TASKNAME: " + taskName);
                    // System.out.println(" SUBFILE: " + subFileName);
                    int est = getEstimateRunTime(subFileName);
                    Task task = new Task();
                    task.taskName = taskName;
                    task.estimateRunTime = est;
                    tasks.add(task);
                } else if (line.startsWith("PARENT")) {
                    String[] parentLine = line.split(" ");
                    List<String> parents = getParent(parentLine);
                    List<String> children = getChild(line);
                    createDependencies(parents, children);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This code sort using comparator and lambda expression
     */
    public void sortByEst() {
        tasks.sort((t1, t2) -> t1.compareTo(t2));
    }

    /**
     * This function create the graph edge depending on the parents and the children
     * 
     * @param parents
     * @param children
     */
    private void createDependencies(List<String> parents, List<String> children) {
        for (Task task : tasks) {
            for (String parent : parents) {
                if (task.taskName.equals(parent)) {
                    for (String child : children) {
                        task.dependencies.add(child);
                    }
                }
            }
            for (String child : children) {
                if (task.taskName.equals(child)) {
                    for (String parent : parents) {
                        task.dependencies.add(parent);
                    }
                }
            }
        }
    }

    /**
     * @param the array of the parent line in DAG file separated by space
     * @return return the CHILD
     */
    private List<String> getChild(String parentLine) {
        List<String> children = new ArrayList<>();
        int childInd = parentLine.indexOf("CHILD");
        String childSubstring = parentLine.substring(childInd);
        String[] childSplit = childSubstring.split(" ");
        for (String str : childSplit) {
            if (!str.equals("CHILD")) {
                children.add(str);
            }
        }
        return children;
    }

    /**
     * @param the array of the parent line in DAG file separated by space
     * @return return the parent
     */
    private List<String> getParent(String[] parentLine) {
        List<String> parent = new ArrayList<>();
        for (String str : parentLine) {
            if (str.equals("CHILD"))
                break;
            if (str.equals("PARENT"))
                continue;
            parent.add(str);
        }
        return parent;
    }

    /**
     * this function read through the sub file and specifically looks for
     * estimate time properties. it gets the value of the estimate time
     * and return it
     * 
     * @param the name of the sub file
     * @return the estimate time properties in specified sub file
     */
    public int getEstimateRunTime(String subFileName) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/" + subFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);
        String line;
        int estTime = 0;
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith("estimate_runtime")) {
                    String[] split = line.split(" ");
                    // System.out.println(split[2]);
                    estTime = Integer.parseInt(split[2]);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return estTime;
    }

    /**
     *
     * @param The name of the Task
     * @return Task by the name provided
     */
    private Task getTaskByName(String taskName) {
        Task targetTask = null;
        for (Task task : tasks) {
            if (task.taskName.equals(taskName)) {
                targetTask = task;
            }
        }
        return targetTask;
    }

    /**
     *
     * @param The estimate time of the task
     * @return Task by the est provided
     */
    private Task getTaskByEstimateTime(int est) {
        Task targetTask = null;
        for (Task task : tasks) {
            if (task.estimateRunTime == est) {
                targetTask = task;
            }
        }
        return targetTask;
    }

    /**
     * This function create a critical path
     * critical path is a path which takes the most
     * estimated run time from start to the end
     *
     * This case Start = A , End = D
     * A = 5, B = 5, C = 20, D = 15
     * 
     * @param the name of the first and last Task
     * @consider using this function before sorting
     *           Sorting by estimate function need to be adjust
     *           but for the sake of simplicity we just ignore it
     * @output A -> C -> D
     *
     */
    public void createCriticalPath(String startTaskName, String endTaskName) {
        int i = 0;
        String path = startTaskName + " -> ";
        totalEstimatedRuntime = getTaskByName(startTaskName).estimateRunTime;
        while (true) {
            Task currTask = tasks.get(i);
            List<String> dependencies = currTask.dependencies;
            int maxEstTime = Integer.MIN_VALUE;
            for (String dependency : dependencies) {
                Task task = getTaskByName(dependency);
                if (maxEstTime < task.estimateRunTime) {
                    maxEstTime = task.estimateRunTime;
                }
            }
            currTask = getTaskByEstimateTime(maxEstTime);
            if (currTask.taskName.equals(endTaskName)) {
                break;
            }
            totalEstimatedRuntime += currTask.estimateRunTime;
            path += currTask.taskName + " -> ";
            i++;
        }
        totalEstimatedRuntime += getTaskByName(endTaskName).estimateRunTime;
        criticalPath = path + endTaskName;
    }

    /*
     * This function print the task with their estimated run time in ascending order
     */
    public void displayTaskAndEst() {
        for (Task task : tasks) {
            System.out.println("Task " + task.taskName + " : " + task.estimateRunTime);
        }
    }

    /**
     * @consider calling this function after reading and initalize Tasks list
     * @return total estimate run time
     *
     */
    public int getTotalEstimatedRunTime() {
        return totalEstimatedRuntime;
    }

    /**
     * @return a string version about all the tasks data
     */
    @Override
    public String toString() {
        String data = "";
        for (Task task : tasks) {
            String taskName = task.taskName;
            int estTime = task.estimateRunTime;
            String dependencies = task.dependencies.toString();
            data += "[TaskName: " + taskName + " Est: " + estTime + " Dependencies: " + dependencies + "],";
        }
        return data;
    }

    public String getCriticalPath() {
        return criticalPath;
    }
}
