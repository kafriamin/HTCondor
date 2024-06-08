package main;

import graph.WorkflowGraph;

public class Main {
    public static void main(String[] args) {
        WorkflowGraph wg = new WorkflowGraph();
        wg.readDagFile("DAG");
        wg.createCriticalPath("A", "D");
        wg.sortByEst();
        wg.displayTaskAndEst();
        System.out.println("Critical Path: " + wg.getCriticalPath());
        System.out.println("Total Estimated Time: " + wg.getTotalEstimatedRunTime());
    }
}
