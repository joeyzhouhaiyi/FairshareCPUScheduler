package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here
        BufferedReader reader = null;

        try {
            if(args.length == 0){
                System.out.println("input.txt not found, please pass the absolute path of input.txt to arg[0]");
                return;
            }
            reader = new BufferedReader(new FileReader(args[0]));
            List<String> inputArrayList = new ArrayList<>();
            OutputLogger.getInstance();

            // read array from file
            String line;
            int index = 0;
            while( (line = reader.readLine()) != null)
                inputArrayList.add(index++, line);

            int timeQuantum = Integer.parseInt(inputArrayList.get(0));

            if(timeQuantum<1){
                System.out.println("Error: Time quantum cannot be less than 1");
                return;
            }

            // read from file and parse it to userlist in scheduler
            Scheduler scheduler = new Scheduler();
            scheduler.setTimeQuantum(timeQuantum);
            for(int i = 1; i < inputArrayList.size();)
            {
                String block = inputArrayList.get(i);
                String [] userAndProcess = block.split(" ");
                String userName = userAndProcess[0];
                User user = new User(userName);
                int numOfProcess = Integer.parseInt(userAndProcess[1]);
                int processId = 0;
                int tmp = ++i;
                for (; i < tmp + numOfProcess ;i++ )
                {
                    String[] startTimeAndDuration = inputArrayList.get(i).split(" ");
                    Process p = new Process(Integer.parseInt(startTimeAndDuration[0]),Integer.parseInt(startTimeAndDuration[1]),user, processId++);
                    user.AddProcess(p);
                }
                scheduler.addUser(user);
            }


            // print out input array
            System.out.println("Input array:\n" + Arrays.toString(inputArrayList.toArray()) + "\n");

            long startTime = System.currentTimeMillis();
            // start scheduler
            scheduler.start();
            scheduler.join();
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;

            System.out.println("Execution time in milliseconds: " + timeElapsed);
            // close writer
            OutputLogger.getInstance().close();

        } finally {
            if( reader != null)
                // close reader
                reader.close();
        }
    }
}
