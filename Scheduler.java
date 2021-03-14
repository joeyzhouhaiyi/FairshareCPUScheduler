package com.company;

import java.util.*;

public class Scheduler extends Thread{

    final int TIME_INTERVAL_IN_MILLISECOND = 100;
    List<User> userList = new ArrayList<>(); //full representation of input.txt
    public static int currentTime = 1;
    Process runningProcess;
    int runningProcessRemainingTime;
    Process nextProcess;
    int nextProcessRemainingTime;

    List<Process> processQ = new ArrayList<>(); //running threads
    List<Process> remainingProcesses = new ArrayList<>();  //remaining processes to be allocated

    //{{UserA, 4},{UserB,2}} means 4 processes are running under UserA, 2 are running under UserB
    Map<User,Integer> currentUserProcessCount = new HashMap<>();
    int timeQuantum;

    public void setTimeQuantum(int t) {timeQuantum = t;}

    void incrementTime()
    {
        currentTime++;
        addProcessToQAtGivenTime();

    }

    void addProcessToQAtGivenTime()
    {
        //check if new process needs to be added at a given time
        if(remainingProcesses.size() != 0)
        {
            if(currentTime == remainingProcesses.get(0).readyTime)
            {
                //get the first process to be scheduled
                var p = remainingProcesses.get(0);
                //check if this the owner exists
                int count = currentUserProcessCount.getOrDefault(p.owner, 0);
                //update process count for the owner
                currentUserProcessCount.put(p.owner,count+1);
                //add to running process
                processQ.add(p);
                //remove from remaining processes
                remainingProcesses.remove(0);
            }
        }
    }

    public void addUser(User user)
    {
        userList.add(user);
    }

    public void startScheduling()
    {
        //1. get all remaining processes
        getAllRemainingProcesses();

        //2. sort processes by ready time in ascending order
        remainingProcesses.sort(Comparator.comparingInt(Process::getReadyTime));

        //3. if at given time no process is ready, increment time
        while(processQ.size()==0)
            incrementTime();

        //4. the real scheduling process begins
        while(remainingProcesses.size()!= 0 || processQ.size()!= 0) //keep running as long as not all processes have been finished
        {
            // if there is only one process to be run
            if(processQ.size() == 1)
            {
                runningProcess = processQ.get(0);
                runningProcessRemainingTime = timeQuantum;
            }
            else
            {
                //otherwise find the next process and assign a time
                updateNextProcess();
            }

            if(runningProcess != null)
            {
                //check if this process has been run before
                if(runningProcess.getTotalExecutionTime() == 0)
                    runningProcess.start();
                else
                    runningProcess.resumeSelf();

                //start running for assigned amount of time
                while (runningProcessRemainingTime > 0)
                {
                    try {
                        sleep(TIME_INTERVAL_IN_MILLISECOND);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runningProcess.incrementExecutionTime();
                    incrementTime();
                    runningProcessRemainingTime--;

                    // when total execution time reaches its service time, signifies process completion
                    if(runningProcess.getTotalExecutionTime() == runningProcess.serviceTime)
                    {
                        runningProcess.setState(Process.ProcessState.Paused);
                        runningProcess.setState(Process.ProcessState.Finished);
                        runningProcess.interrupt();
                        try {
                            runningProcess.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        processQ.remove(runningProcess);
                        //check if this the owner exists
                        int count = currentUserProcessCount.getOrDefault(runningProcess.owner, 0);
                        //update process count for the owner
                        currentUserProcessCount.put(runningProcess.owner,count-1);
                        runningProcessRemainingTime = 0;
                    }
                }

                //when running time reaches its allocated time, move on to the next process
                if(runningProcess!=null && runningProcessRemainingTime == 0)
                {
                    if(nextProcess == null)
                        updateNextProcess();
                    if(runningProcess.getCurrentState() != Process.ProcessState.Finished)
                        runningProcess.pauseSelf();

                    if(nextProcess !=null)
                    {
                        runningProcess = nextProcess;
                        runningProcessRemainingTime = nextProcessRemainingTime;
                        nextProcess = null;
                        nextProcessRemainingTime = 0;
                    }
                }
            }

        }
        System.out.println("all processes have been scheduled");
    }

    void updateNextProcess()
    {
        if(processQ.size() > 1)
        {
            //get next process
            for(var p : processQ)
            {
                if(p != runningProcess)
                {
                    nextProcess = p;
                    break;
                }
            }
            if(nextProcess != null)
            {
                //calculate running time
                int numUser = 0;
                for( var user : currentUserProcessCount.keySet())
                {
                    numUser += currentUserProcessCount.get(user) != 0? 1:0; // get the number of users who are present in the system
                }
                //calculate running time
                nextProcessRemainingTime = timeQuantum / numUser / currentUserProcessCount.get(nextProcess.owner);
            }
        }
    }

    void getAllRemainingProcesses()
    {
        userList.forEach(u -> remainingProcesses.addAll(u.getProcesses()));
    }

    @Override
    public void run() {
        startScheduling();
    }
}
