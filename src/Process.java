import static java.lang.Math.ceil;


public class Process {
    private final String name;
    private int burstTime = 0;
    private int burst = 0; //counter for the scheduling
    private int arrivalTime = 0;
    private int priority = 0;
    private int priorityCounter = 0;
    private int quantum = 0;
    private int completionTime = 0;
    private int waitTime = 0;
    private int turnAroundTime = 0;

    public Process(String n, int b, int a, int p, int q) {
        this.name = n;
        this.burstTime = b;
        this.arrivalTime = a;
        this.priority = p;
        this.quantum = q;
        this.burst = b;
        this.priorityCounter = p;
    }

    public Process(String n) {
        this.name = n;
    }

    public void setTurnAroundTime(int t) {
        this.turnAroundTime = t;
    }

    public void setCompletionTime(int c) {
        this.completionTime = c;
    }

    public void setWaitTime(int w) {
        this.waitTime = w;
    }

    public void setBurst(int b) {
        this.burst = b;
    }

    public void setPriorityCounter(int p) {
        this.priorityCounter = p;
    }

    public void setQuantum(int q) {
        this.quantum = q;
    }

    public String getName() {
        return name;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getBurst() {
        return burst;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getPriorityCounter() {
        return priorityCounter;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getPriority() {
        return priority;
    }

    public int getQuarterTime() {
        return (int) ceil((float) quantum / 4);
    }

    public int getHalfTime() {
        return (int) (ceil((float) quantum / 2));
    }

    public void display() {
        System.out.println(this.name + '\t' + this.burstTime + '\t' + this.arrivalTime + '\t' + this.priority + '\t' + this.quantum + '\t' + this.completionTime + '\t' + this.turnAroundTime + '\t' + this.waitTime);
    }

    public void traceDisplay() {
        System.out.println(this.name + '\t' + this.burst + '\t' + this.arrivalTime + '\t' + this.priorityCounter + '\t' + this.quantum);
    }
}
