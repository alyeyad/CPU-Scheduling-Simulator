import java.util.ArrayList;

public class RoundRobin {
    ArrayList<Process> Processes;
    ArrayList<Process> readyQueue;
    ArrayList<Process> chart;
    int contextSwitching;
    int Quantum;

    RoundRobin(ArrayList<Process> a, int c, int q) {
        this.Processes = new ArrayList<>(a);
        this.contextSwitching = c;
        this.Quantum = q;
        this.readyQueue = new ArrayList<>();
        this.chart = new ArrayList<>();
    }
    public ArrayList<Process> getChart() {
        return chart;
    }

    public void schedule() {
        int processIndex = 0;
        int time = 0;
        int quantumCounter, contextCounter;
        Process prev = new Process(" ");
        Processes.sort((o1, o2) -> {
            if (o1.getArrivalTime() == o2.getArrivalTime()) {
                return CharSequence.compare(o1.getName(), o2.getName());
            }
            return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
        });

        // a loop that stops if the ready queue is empty and there are no process left
        while (!readyQueue.isEmpty() || processIndex < Processes.size()) {
            quantumCounter = this.Quantum;
            contextCounter = this.contextSwitching;
            // Quantum loop to schedule by <quantum> times
            while (quantumCounter > 0) {
                // check the arrival of new processes
                while (processIndex < Processes.size() && time == Processes.get(processIndex).getArrivalTime()) {
                    readyQueue.add(Processes.get(processIndex));
                    processIndex++;
                }
                time++;

                prev = readyQueue.get(0);

                if (readyQueue.get(0).getBurst() > 0) {
                    readyQueue.get(0).setBurst(readyQueue.get(0).getBurst() - 1);
                    this.chart.add(readyQueue.get(0));
                    quantumCounter--;
                }
                if (readyQueue.get(0).getBurst() == 0) {
                    quantumCounter = 0;
                    readyQueue.get(0).setCompletionTime(time);
                    readyQueue.get(0).setTurnAroundTime(readyQueue.get(0).getCompletionTime() - readyQueue.get(0).getArrivalTime());
                    readyQueue.get(0).setWaitTime(readyQueue.get(0).getTurnAroundTime() - readyQueue.get(0).getBurstTime());
                    readyQueue.remove(prev);
                    prev = null;
                }
//                System.out.print(time + " : ");
//                chart.get(chart.size() - 1).traceDisplay();
            }
            if (readyQueue.size() == 1 && prev != null) continue;

            // Context Switch loop to save the run of the process in <Context Switch> time
            while (contextCounter > 0 && !readyQueue.isEmpty()) {
                time++;
                this.chart.add(new Process("Sw"));

//                System.out.print(time + " : ");
//                chart.get(chart.size() - 1).traceDisplay();

                contextCounter--;
            }
            if (prev != null) {
                readyQueue.remove(0);
                readyQueue.add(prev);
            }
            if (processIndex < Processes.size()) {
                for (int i = contextSwitching; i > 0; i--) {
                    if (Processes.get(processIndex).getArrivalTime() == time - i) {
                        readyQueue.add(Processes.get(processIndex));
                        processIndex++;
                    }
                }
            }
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
