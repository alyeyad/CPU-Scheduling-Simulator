/*
* Assignment 2:
* Group: 3AI - S5/S6
    David Magdy Sayed              20200171  (NOTE: GROUP 3AI-S2)
    Aly Eyad Aly Sayed Ahmed       20200331
    John Gamil Samir Hakim         20200132
    Silvana Yackoub GabAllah       20201091
    Mira Ehab Mikhail              20201234
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int nProcesses, RRQuantum, contextSwitch;
        File input = new File("input.txt");
        Scanner in = new Scanner(input);

        nProcesses = in.nextInt();
        RRQuantum = in.nextInt();
        contextSwitch = in.nextInt();

        ArrayList<Process> processList = new ArrayList<>(nProcesses);
        for (int i = 0; i < nProcesses; i++) {
            Process P = new Process(in.next(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
            processList.add(i, P);
        }

        tableView(processList);
        // comment all but one scheduling algorithm
//        scheduleRoundRobin(processList, contextSwitch, RRQuantum); // Round Robin with context switch
//        schedulePriority(processList);                             // Preemptive Priority
//        scheduleShortestJob(processList, contextSwitch);           // Preemptive Shortest Job First with context switch
        scheduleAG(processList);                                   // AG
    }

    public static void tableView(ArrayList<Process> a) {
        System.out.print("PN\tBT\tAT\tP\tQ\tCT\tTAT\tWT\n------------------------------\n");
        for (Process process : a) {
            process.display();
        }
    }

    public static void Gantt(ArrayList<Process> a) {
        ArrayList<Integer> time = new ArrayList<>();
        System.out.print("\n↓\t");
        String lastPrinted="";
        for (int i = 0; i < a.size() - 1; i++) {

            if (!Objects.equals(a.get(i).getName(), a.get(i + 1).getName())) {
                time.add(i + 1);
                System.out.print(a.get(i).getName() + "\t");
                lastPrinted=a.get(i).getName();
            }
        }
        //P4
        if (!Objects.equals(a.get(a.size() - 1).getName(), lastPrinted)) {
            System.out.print(a.get(a.size() - 1).getName());
            time.add(a.size());
        }
        System.out.print("\n0\t");
        for (int t : time) System.out.print(t + "\t");
    }

    public static void scheduleRoundRobin(ArrayList<Process> a, int c, int q) {
        System.out.println("\n---Started RR scheduling process---");
        RoundRobin example = new RoundRobin(new ArrayList<>(a), c, q);
        example.schedule();
        tableView(a);              // viewing a table format of the result
        System.out.println("\n==> Average turnAround: " + example.avgTurnAround());
        System.out.println("==> Average wait: " + example.avgWait());
        Gantt(example.getChart()); // viewing the Gantt chart
        System.out.println("\n---Ended RR scheduling process---\n");
    }

    public static void schedulePriority(ArrayList<Process> a) {
        System.out.println("\n---Started Priority scheduling process---");
        Priority example = new Priority(new ArrayList<>(a));
        example.schedule();
        tableView(a);
        System.out.println("\n==> Average turnAround: " + example.avgTurnAround());
        System.out.println("==> Average wait: " + example.avgWait());
        Gantt(example.getChart()); // viewing the Gantt chart
        System.out.println("\n---Ended Priority scheduling process---\n");
    }

    public static void scheduleShortestJob(ArrayList<Process> a, int c) {
        System.out.println("\n---Started SJF scheduling process---");
        SJF example = new SJF(a, c);
        example.schedule();
        tableView(a);
        System.out.println("==> Average turnAround: " + SJF.getAvgTurnTime(a));
        System.out.println("==> Average wait: " + SJF.getAvgWaitTime(a));

        Gantt(example.getChart()); // viewing the Gantt chart
        System.out.println("\n---Ended SJF scheduling process---\n");
    }

    public static void scheduleAG(ArrayList<Process> a) {

        System.out.println("\n---Started AG scheduling process---");
        AG example = new AG(a);
        example.schedule();
        tableView(a);
        System.out.println("==> Average turnAround: " + SJF.getAvgTurnTime(a));
        System.out.println("==> Average wait: " + SJF.getAvgWaitTime(a));
        Gantt(example.getChart()); // viewing the Gantt chart
        System.out.println("\n---Ended AG scheduling process---\n");
    }
}