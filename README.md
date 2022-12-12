# CPU Scheduling Simulator

Write a java program to simulate the following schedulers:</br>
1. preemptive Shortest- Job First (SJF) Scheduling with context switching</br>
2. Round Robin (RR) with context switching</br>
3. preemptive Priority Scheduling (with the solving of starvation problem)</br>
4. AG Scheduling :</br>
a. Each process is provided a static time to execute called quantum.</br>
b. Once a process is executed for given time period, it’s called FCFS till the</br>
finishing of (ceil(52%)) of its Quantum time then it’s converted tonon</br>
preemptive Priority till the finishing of the next(ceil(52%)), after that it’s</br>
converted to preemptive Shortest- Job First (SJF).</br>
c. We have 3 scenarios of the running process</br>
i. The running process used all its quantum time and it still have job to</br>
do (add this process to the end of the queue, then increases its</br>
Quantum time by Two).</br>
ii. The running process was execute as non-preemptive Priority and</br>
didn’t use all its quantum time based on another process converted</br>
from ready to running (add this process to the end of the queue, and</br>
then increase its Quantum time by ceil(the remaining Quantum</br>
time/2) ).</br>
iii. The running process was execute as preemptive Shortest- Job First</br>
(SJF) and didn’t use all its quantum time based on another process</br>
converted from ready to running (add this process to the end of the</br>
queue, and then increase its Quantum time by the remaining</br>
Quantum time).</br>
iv. The running process didn’t use all of its quantum time because it’s no</br>
longer need that time and the job was completed (set it’s quantum</br>
time to zero).</br>
