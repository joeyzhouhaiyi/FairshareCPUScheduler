package com.company;

import java.util.ArrayList;
import java.util.List;

public class User {
    String name;
    List<Process> processes = new ArrayList<>();

    public User(String name)
    {
        this.name = name;
    }

    public String getName(){ return name;}

    public List<Process> getProcesses(){ return processes; }

    public void AddProcess(Process p)
    {
        processes.add(p);
    }

    public void removeProcess(Process p){
        processes.remove(p);
    }

    public int getNumberOfProcesses() {
        return processes.size();
    }
}
