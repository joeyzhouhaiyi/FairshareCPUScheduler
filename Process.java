package com.company;

public class Process extends Thread{

    public enum ProcessState {
        Default,
        Started,
        Resumed,
        Paused,
        Finished
    }

    ProcessState currentState = ProcessState.Default;
    int readyTime;
    int serviceTime;
    int totalExecutionTime;
    User owner;
    int id;

    public Process(int readyTime, int serviceTime, User owner, int id) {
        this.readyTime = readyTime;
        this.serviceTime = serviceTime;
        this.owner = owner;
        this.id = id;
    }

    public void setState(ProcessState s){
        currentState = s;
        System.out.println("Time " + Scheduler.currentTime + ", " + owner.getName() + ", Process "+ id + ", " + currentState.toString());
        OutputLogger.getInstance().writeLine("Time " + Scheduler.currentTime + ", " + owner.getName() + ", Process "+ id + "," + currentState.toString());
    }
    public int getReadyTime() { return readyTime;}
    public int getTotalExecutionTime() { return totalExecutionTime;}
    public void incrementExecutionTime() { totalExecutionTime++; }
    public ProcessState getCurrentState() { return currentState; }

    //pause current thread
    public void pauseSelf()
    {
        setState(ProcessState.Paused);
        this.suspend();
    }

    //resume current thread
    public void resumeSelf()
    {
        this.resume();
        setState(ProcessState.Resumed);
    }

    @Override
    public void run() {
        if(totalExecutionTime == 0)
            setState(ProcessState.Started);
        setState(ProcessState.Resumed);
        while(true)
        {
            if(Thread.interrupted())
            {
                break;
            }
        }
    }
}