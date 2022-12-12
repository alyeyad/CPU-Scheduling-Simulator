import java.util.ArrayList;

import static java.lang.Math.max;

public class Priority {
    ArrayList<Process> Processes;
    ArrayList<Process> readyQueue;
    ArrayList<Process> chart;
    int priorityRate = 1;

    Priority(ArrayList<Process> a) {
        this.Processes = new ArrayList<>(a);
        this.readyQueue = new ArrayList<>();
        this.chart = new ArrayList<>();
    }
    public ArrayList<Process> getChart() {
        return chart;
    }

    public void schedule() {
        int time = 0;
        int processIndex = 0;
        Processes.sort((o1, o2) -> {
            if (o1.getArrivalTime() == o2.getArrivalTime()) {
                return CharSequence.compare(o1.getName(), o2.getName());
            }
            return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
        });

        while (!readyQueue.isEmpty() || processIndex < Processes.size()) {
            // check for new arrivals
            while (processIndex < Processes.size() && Processes.get(processIndex).getArrivalTime() == time) {
                readyQueue.add(Processes.get(processIndex));
                processIndex++;
            }

            // if the cpu is idle but there are still processes to be scheduled
            if (readyQueue.isEmpty()) {
                time++;
                continue;
            }
            // sort by priority with secondary sort by arrivalTime
            // readyQueue.sort(new Process.comparePriority());
            readyQueue.sort((o1, o2) -> {
                if (o1.getPriorityCounter() == o2.getPriorityCounter()) {
                    return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
                }
                return Integer.compare(o1.getPriorityCounter(), o2.getPriorityCounter());
            });

            // run the process if it still has burst time left
            // and decrement the process' burst and the rest's priority
            if (readyQueue.get(0).getBurst() > 0) {
                for (Process process : readyQueue) {
                    if (process == readyQueue.get(0)) process.setBurst(process.getBurst() - 1) ;
                    else if (time % priorityRate == 0) process.setPriorityCounter(max(process.getPriorityCounter() - 1, 0));
                }
                this.chart.add(readyQueue.get(0));
            }
            if (readyQueue.get(0).getBurst() == 0) {
                readyQueue.get(0).setCompletionTime(time);
                readyQueue.get(0).setTurnAroundTime(readyQueue.get(0).getCompletionTime() - readyQueue.get(0).getArrivalTime());
                readyQueue.get(0).setWaitTime(readyQueue.get(0).getTurnAroundTime() - readyQueue.get(0).getBurstTime());
                readyQueue.remove(0);
            }
            time++;
        }
    }

    public double avgTurnAround() {
        double avg = 0;
        for (Process process : this.Processes) {
            avg += process.getTurnAroundTime();
        }
        return avg / this.Processes.size();
    }

    public double avgWait() {
        double avg = 0;
        for (Process process : this.Processes) {
            avg += process.getWaitTime();
        }
        return avg / this.Processes.size();
    }

}
