import java.sql.SQLOutput;
import java.util.ArrayList;

class AG
{
    // The input list of processes that need to be scheduled
    private final ArrayList<Process> ProcessList;

    // The total number of processes, the size of the process list
    private final int NumProc;

    // The List of processes currently in the ready queue
    private final ArrayList<Process> ongoing = new ArrayList<>();

    // A list storing the execution history of all processes
    private final ArrayList<Process> execHistory = new ArrayList<>();

    // The current time instant of the scheduler
    private int curTime;

    public AG(ArrayList<Process> processList)
    {
        this.ProcessList = new ArrayList<>(processList);
        NumProc = ProcessList.size();
    }

    public ArrayList<Process> getChart()
    {
        return execHistory;
    }

    // Prints the Current Quantum Values of all processes
    private void printQuantums()
    {
        ArrayList<Process> _tmp = new ArrayList<>(ProcessList);

        _tmp.sort(new SJF.ComparatorArrivalTime());
        StringBuilder quantums = new StringBuilder("(");
        for (Process p : _tmp)
        {
            quantums.append(p.getQuantum()).append(", ");
        }
        quantums.append("\b\b)\n");
        System.out.println(quantums);
    }

    // A function that returns the next process in the ready queue using FCFS scheduling
    private Process FCFSSch()
    {
        if (ongoing.isEmpty()) return null;
        return ongoing.get(0);
    }

    // A function that returns the next process in the ready queue using Non-preemptive Priority Scheduling
    private Process nonPreempPrioritySch()
    {
        //get min priority instead of sorting

        Process res = null;
        int mn_priority = Integer.MAX_VALUE;
        for (Process p : ongoing)
        {
            if (p.getPriority() < mn_priority)
            {
                res = p;
                mn_priority = p.getPriority();
            }
        }
        return res;
    }

    // A function that returns the next process in the ready queue using Preemptive Priority Scheduling
    private Process preempSJFSch()
    {
        //get min priority instead of sorting
        Process res = null;
        int mn_burst = Integer.MAX_VALUE;

        for (Process p : ongoing)
        {
            if (p.getBurst() < mn_burst)
            {
                res = p;
                mn_burst = p.getBurst();
            }
        }
        return res;
    }

    // A function that adds processes to the ready queue, given that they have arrived before or at the current time instant
    private int checkArrivals(int lastEntered)
    {
        while (lastEntered < NumProc - 1 && ProcessList.get(lastEntered + 1).getArrivalTime() <= curTime)
        {
            ongoing.add(ProcessList.get(++lastEntered));
        }
        // In case there is a time gap where the ready queue is idle, jumps to the next arrival time and adds a process
        if (lastEntered < NumProc - 1 && ongoing.isEmpty())
        {
            for (int i = curTime; i < ProcessList.get(lastEntered + 1).getArrivalTime(); i++)
                execHistory.add(new Process("MT"));
            curTime = ProcessList.get(lastEntered + 1).getArrivalTime();
            ongoing.add(ProcessList.get(++lastEntered));
        }
        return lastEntered;
    }

    // The main function responsible for the scheduling operation
    public void schedule()
    {
        // Sort the process list by arrival time first
        ProcessList.sort(new SJF.ComparatorArrivalTime());



        StringBuilder names = new StringBuilder("(");
        for (Process p : ProcessList)
        {
            names.append(p.getName()).append(", ");
        }
        names.append("\b\b)\n");
        System.out.print(names);


        // Start from the arrival time of the first process
        curTime = ProcessList.get(0).getArrivalTime();

        // Add the first process to the ready queue
        ongoing.add(ProcessList.get(0));

        // A vbl holding the current process to be executed
        Process curProcess = ongoing.get(0);

        int lastArrivedInd = 0;

        // Breaks when all processes have arrived and the ready queue is empty
        while (lastArrivedInd < NumProc - 1 || !ongoing.isEmpty())
        {
            // Checks for process arrivals at the current time instant
            lastArrivedInd = checkArrivals(lastArrivedInd);

            // Breaks when all processes have arrived and the ready queue is empty
            if (lastArrivedInd == NumProc - 1 && ongoing.isEmpty()) break;

            // The length of the current execution, by default = ceil(25%) of quantum
            int len = curProcess.getQuarterTime();

            // If the remaining burst time is less than ceil(25%) of quantum, the process executes till it's done
            if (curProcess.getBurst() < len) len = curProcess.getBurst();

            // Adding the current process to the execution history
            for (int i = curTime; i < curTime + len; i++) execHistory.add(curProcess);

            // Time advances
            curTime += len;

            // The remaining burst time of the executed process is updated
            curProcess.setBurst(curProcess.getBurst() - len);

            // Check for new Arrivals at the current time instant
            lastArrivedInd = checkArrivals(lastArrivedInd);

            // If a process is done, its quantum is set to 0, and it's removed from the ready queue
            if (curProcess.getBurst() == 0)
            {
                curProcess.setQuantum(0);
                ongoing.remove(curProcess);

                // Its completion time is recorded for later calculations
                curProcess.setCompletionTime(curTime);
                curProcess.setTurnAroundTime(curProcess.getCompletionTime() - curProcess.getArrivalTime());
                curProcess.setWaitTime(curProcess.getTurnAroundTime() - curProcess.getBurstTime());

                // The next process is chosen using FCFS criteria
                curProcess = FCFSSch();

                // At the end of every execution, the current quantum values are printed
                printQuantums();
                continue;
            }

            // Checking if the process has finished its quantum, but it's burst time hasn't finished yet
            if (curProcess.getQuantum() == 0)
            {
                // The process is added to the end of the queue
                ongoing.remove(curProcess);
                ongoing.add(curProcess);

                // The Quantum is increased by 2
                curProcess.setQuantum(curProcess.getQuantum() + 2);

                // The next process is chosen using FCFS criteria
                curProcess = FCFSSch();

                // At the end of every execution, the current quantum values are printed
                printQuantums();
                continue;
            }

            // Choosing the next process according to non-preemptive priority
            Process stage2 = nonPreempPrioritySch();

            // If it's the same process from stage 1
            if (curProcess == stage2)
            {
                // This process operates till it reaches ceil(50%) of its quantum time
                len = curProcess.getHalfTime() - curProcess.getQuarterTime();

                // If the remaining burst time is less than ceil(50%) of quantum, the process executes till it's done
                if (curProcess.getBurst() < len) len = curProcess.getBurst();

                // Adding the current process to the execution history
                for (int i = curTime; i < curTime + len; i++) execHistory.add(curProcess);

                // Time advances
                curTime += len;

                // The remaining burst time of the executed process is updated
                curProcess.setBurst(curProcess.getBurst() - len);

                // Check for new Arrivals at the current time instant
                lastArrivedInd = checkArrivals(lastArrivedInd);

                // If a process is done, its quantum is set to 0, and it's removed from the ready queue
                if (curProcess.getBurst() == 0)
                {
                    curProcess.setQuantum(0);
                    ongoing.remove(curProcess);

                    // Its completion time is recorded for later calculations
                    curProcess.setCompletionTime(curTime);
                    curProcess.setTurnAroundTime(curProcess.getCompletionTime() - curProcess.getArrivalTime());
                    curProcess.setWaitTime(curProcess.getTurnAroundTime() - curProcess.getBurstTime());

                    // The next process is chosen using FCFS criteria
                    curProcess = FCFSSch();

                    // At the end of every execution, the current quantum values are printed
                    printQuantums();
                    continue;
                }

                // Checking if the process has finished its quantum, but it's burst time hasn't finished yet
                if (curProcess.getQuantum() == 0)
                {
                    // The process is added to the end of the queue
                    ongoing.remove(curProcess);
                    ongoing.add(curProcess);

                    // The Quantum is increased by 2
                    curProcess.setQuantum(curProcess.getQuantum() + 2);

                    // The next process is chosen using FCFS criteria
                    curProcess = FCFSSch();

                    // At the end of every execution, the current quantum values are printed
                    printQuantums();
                    continue;
                }

                // Choosing the next process according to preemptive Shortest Job First
                Process stage3 = preempSJFSch();

                // If it's the same process from stages 1 & 2
                if (curProcess == stage3)
                {
                    // The process executes for the remainder of its quantum time
                    len = curProcess.getQuantum() - curProcess.getHalfTime();

                    // If the remaining burst time is less than the remaining of quantum, the process executes till it's done
                    if (curProcess.getBurst() < len) len = curProcess.getBurst();

                    // A flag indicating whether a process will be removed from the queue due to a shorter one arriving
                    boolean preemption = false;

                    // IF there are no more arrivals, then the shortest job is the one chosen already
                    // and IF the arrival time of the next process is halfway through the execution of the cur process
                    // and IF the new process is shorter than the current process
                    if (lastArrivedInd < NumProc - 1 && ProcessList.get(lastArrivedInd + 1).getArrivalTime() <= (curTime + len) && ProcessList.get(lastArrivedInd + 1).getBurst() < curProcess.getBurst())
                    {
                        // Then execute the process only until the next one arrives
                        len = ProcessList.get(lastArrivedInd + 1).getArrivalTime() - curTime;

                        // Preemption flag used to determine criteria of choosing the next process
                        preemption = true;
                    }

                    // Adding the current process to the execution history
                    for (int i = curTime; i < curTime + len; i++) execHistory.add(curProcess);

                    // Time advances
                    curTime += len;

                    // The remaining burst time of the executed process is updated
                    curProcess.setBurst(curProcess.getBurst() - len);

                    // Check for new Arrivals at the current time instant
                    lastArrivedInd = checkArrivals(lastArrivedInd);

                    // If a process is done, its quantum is set to 0, and it's removed from the ready queue
                    if (curProcess.getBurst() == 0)
                    {
                        curProcess.setQuantum(0);
                        ongoing.remove(curProcess);

                        // Its completion time is recorded for later calculations
                        curProcess.setCompletionTime(curTime);
                        curProcess.setTurnAroundTime(curProcess.getCompletionTime() - curProcess.getArrivalTime());
                        curProcess.setWaitTime(curProcess.getTurnAroundTime() - curProcess.getBurstTime());

                        // If no preemption has happened, choose the next process normally using FCFS
                        if (!preemption) curProcess = FCFSSch();
                            // else add that next process to the ready queue and choose it using SJF
                        else
                        {
                            lastArrivedInd = checkArrivals(lastArrivedInd);
                            curProcess = preempSJFSch();
                        }

                        // At the end of every execution, the current quantum values are printed
                        printQuantums();
                        continue;
                    }

                    // Checking if the process has finished its quantum, but it's burst time hasn't finished yet
                    if (curProcess.getQuantum() == 0)
                    {
                        ongoing.remove(curProcess);
                        ongoing.add(curProcess);

                        // The Quantum is increased by 2
                        curProcess.setQuantum(curProcess.getQuantum() + 2);

                        // If no preemption has happened, choose the next process normally using FCFS
                        if (!preemption) curProcess = FCFSSch();
                            // else add that next process to the ready queue and choose it using SJF
                        else
                        {
                            lastArrivedInd = checkArrivals(lastArrivedInd);
                            curProcess = preempSJFSch();
                        }

                        // At the end of every execution, the current quantum values are printed
                        printQuantums();
                        continue;
                    }

                    if (preemption)
                    {
                        lastArrivedInd = checkArrivals(lastArrivedInd);
                        curProcess = preempSJFSch();
                        continue;
                    }
                } else // if (curProcess != stage3)
                {
                    // Remove the old process and add it to the end of the queue
                    ongoing.remove(curProcess);
                    ongoing.add(curProcess);

                    // Increase Quantum by the remaining part of it
                    int rem_quantum = curProcess.getQuantum() - curProcess.getHalfTime();
                    curProcess.setQuantum(curProcess.getQuantum() + rem_quantum);

                    // Change the current process for the new iteration
                    curProcess = stage3;

                    // At the end of every execution, the current quantum values are printed
                    printQuantums();
                    continue;
                }
            } else //if (curProcess != stage2)
            {
                // Remove the old process and add it to the end of the queue
                ongoing.remove(curProcess);
                ongoing.add(curProcess);

                // Increase Quantum by ceil(the remaining part)/2
                int rem_quantum = curProcess.getQuantum() - curProcess.getQuarterTime();
                curProcess.setQuantum(curProcess.getQuantum() + (int) Math.ceil(rem_quantum / 2.0));

                // Change the current process for the new iteration
                curProcess = stage2;

                // At the end of every execution, the current quantum values are printed
                printQuantums();
                continue;
            }
            // At the end of every execution, the current quantum values are printed
            printQuantums();
        }
    }
}