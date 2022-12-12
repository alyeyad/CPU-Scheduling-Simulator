import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class SJF
{
    //The input list of processes that need to be scheduled
    private final ArrayList<Process> ProcessList;

    //The total number of processes, the size of the previous list
    private final int NumProc;

    // A list simulating the ready queue
    private final ArrayList<Process> ongoing = new ArrayList<>();

    // A list storing the process executed at every second
    private final ArrayList<Process> execHistory = new ArrayList<>();

    //The length of a single context switching operation
    private final int ContextSw;

    public SJF(ArrayList<Process> processList, int ContextSw)
    {
        ProcessList = new ArrayList<>(processList);
        NumProc = processList.size();
        this.ContextSw = ContextSw;
    }

    public ArrayList<Process> getChart()
    {
        return execHistory;
    }

    public static double getAvgWaitTime(ArrayList<Process> processList)
    {
        double agg = 0.0;
        for (Process p : processList)
            agg += p.getWaitTime();
        agg /= processList.size();
        return agg;
    }

    public static double getAvgTurnTime(ArrayList<Process> processList)
    {
        double agg = 0.0;
        for (Process p : processList)
            agg += p.getTurnAroundTime();
        agg /= processList.size();
        return agg;
    }

    //The main method performing the actual scheduling operation
    public void schedule()
    {
        // Sorting the input Process List according to arrival time
        ProcessList.sort(new ComparatorArrivalTime());

        // lastArrivedInd is the index of the most recently-added process
        int lastArrivedInd = 0;

        // The time starts when the first process arrives
        //The current time instant when using the scheduler
        int curTime = ProcessList.get(lastArrivedInd).getArrivalTime();

        // adding the first process to the ongoing list to begin execution
        ongoing.add(ProcessList.get(lastArrivedInd));

        // The loop exits when all ongoing processes have finished and there are no other processes yet to arrive
        while (!ongoing.isEmpty() || lastArrivedInd != NumProc - 1)
        {
            //The ongoing queue is sorted according to burst time, the first process is the shortest
            Process curProcess = ongoing.get(0);
            String oldName = curProcess.getName();
            // len is the amount of time the current process will execute, until it uses all its burst time, or a shorter
            // process arrives halfway through
            int len;

            // by default, a process is allowed to run until it uses all its burst time
            len = curProcess.getBurst();


            // if the next process to arrive has a smaller burst time than the current process, the current process
            // will only run until the next process arrives
            if (lastArrivedInd != NumProc - 1)
                len = Math.min(ProcessList.get(lastArrivedInd + 1).getArrivalTime() - curTime, curProcess.getBurst());


            // decrementing the remaining burst time by the length of the current run
            curProcess.setBurst(curProcess.getBurst() - len);

            // if the remaining burst time is 0, then the process has finished executing
            if (curProcess.getBurst() == 0)
            {
                ongoing.remove(curProcess);
                curProcess.setCompletionTime(curTime + len);
                curProcess.setTurnAroundTime(curProcess.getCompletionTime() - curProcess.getArrivalTime());
                curProcess.setWaitTime(curProcess.getTurnAroundTime() - curProcess.getBurstTime());
            }

            // Adding the current process to the execution history
            for(int i=curTime; i<curTime+len; i++) execHistory.add(curProcess);

            // updating the current time
            curTime += len;


            // Cases of adding a new process
            if (lastArrivedInd != NumProc - 1)
            {
                // If there is a time gap between the execution of one process and the arrival of another,
                // the new process is not added to the ongoing queue unless it is empty
                if (!ongoing.isEmpty() && curTime < ProcessList.get(lastArrivedInd + 1).getArrivalTime())
                {
                    if (!ongoing.isEmpty())
                    {
                        //If the process is going to change, need to perform context switching
                        if (!Objects.equals(oldName, ongoing.get(0).getName()))
                        {
                            // Adding Context Switching to the execution history
                            for(int i=curTime; i<curTime+ContextSw; i++)
                                execHistory.add(new Process("SW"));

                            // Updating the current time
                            curTime += ContextSw;

                            // If a process arrives halfway through the context switching period
                            while (lastArrivedInd != NumProc - 1 && curTime >= ProcessList.get(lastArrivedInd + 1).getArrivalTime())
                            {
                                // the process is added and the ready queue is re-sorted by arrival time
                                ongoing.add(ProcessList.get(++lastArrivedInd));
                                ongoing.sort(new ComparatorBurstTime());
                            }
                        }
                    }
                    continue;
                }

                // If there is a time gap in which the ongoing queue is empty, skip ahead to the time when the
                // next process will arrive
                if (ongoing.isEmpty())
                {
                    int nextArrivalTime = ProcessList.get(lastArrivedInd + 1).getArrivalTime();

                    // Adding the idle period to the execution history
                    for(int i=curTime; i<nextArrivalTime; i++)
                        execHistory.add(new Process("MT"));

                    // Time advances
                    curTime = nextArrivalTime;
                }

                // Otherwise add the new process and sort normally
                ongoing.add(ProcessList.get(++lastArrivedInd));
                ongoing.sort(new ComparatorBurstTime());
            }

            if (!ongoing.isEmpty())
            {
                //If the process is going to change, need to perform context switching
                if (!Objects.equals(oldName, ongoing.get(0).getName()))
                {
                    // Adding Context Switching to the execution history
                    for(int i=curTime; i<curTime+ContextSw; i++)
                        execHistory.add(new Process("SW"));

                    curTime += ContextSw;

                    // If a process arrives halfway through the context switching period
                    while (lastArrivedInd != NumProc - 1 && curTime >= ProcessList.get(lastArrivedInd + 1).getArrivalTime())
                    {
                        ongoing.add(ProcessList.get(++lastArrivedInd));
                        ongoing.sort(new ComparatorBurstTime());
                    }
                }
            }
        }
        for(int i=curTime; i<curTime+ContextSw; i++)
            execHistory.add(new Process("SW"));
    }

    static class ComparatorArrivalTime implements Comparator<Process>
    {

        @Override
        public int compare(Process o1, Process o2)
        {
            if (o1.getArrivalTime() == o2.getArrivalTime()) return o1.getBurst() - o2.getBurst();
            return o1.getArrivalTime() - o2.getArrivalTime();
        }
    }

    //A comparator used to sort processes according to burst time
    static class ComparatorBurstTime implements Comparator<Process>
    {

        @Override
        public int compare(Process o1, Process o2)
        {
            if (o1.getBurst() == o2.getBurst()) return o1.getArrivalTime() - o2.getArrivalTime();
            return o1.getBurst() - o2.getBurst();
        }
    }
}
