package jobscheduling;

import java.util.ArrayList;
import java.util.Arrays;

class Job implements Comparable<Job> {
    final int reqTime;      // time required to process the job
    int remTime;            // remaining time
    long turnaround;        // turnaround time
    
    Job(int t) {  remTime = reqTime = t;  }
    Job(Job j) {                                    // copy constructor
        reqTime = j.reqTime;
        remTime = j.remTime;
        turnaround = j.turnaround;
    }
    
    public int compareTo(Job j) {
        //Job j = (Job) o;
        if(remTime < j.remTime) return -1;
        else if(remTime > j.remTime) return 1;
        else return 0;
    }
}

class ScheduleTest {
    static int timeslice;           // unit of time for processing jobs
    static long tally, sum;         // total time worked so far
                                    // and sum of all turnaround times
    
    // Jobs processed in order receivd (simplest)
    public static void FIFO(Job[] j, int t) {
        ArrayList<Job> jobs = new ArrayList<>();    // copy the array so as not
        jobs.addAll(Arrays.asList(j));              // to change the original
        
        tally = 0;
        sum = 0;
        
        System.out.print("\nFIFO:\nProcessing " + j.length + " random jobs "
                + "(time slice irrelevant)...");
        
        for(Job x : jobs) {                     // process a job and add the
            tally += x.reqTime;                 // finish time to the total               
            sum += tally;               // keep a running sum of the finish times
        }
        
        System.out.println("done.\nTotal processing time for the batch was "
                        + tally + " units.");
        System.out.println("Avg turnaround time for each job was "
                        + (sum / j.length) + " units.");
    }
    
     // Jobs processed for a fixed slice of time then sent to end of the line
    public static void RR(Job[] j, int t) {
        ArrayList<Job> jobs = new ArrayList<>();    // copy the array so as not
        jobs.addAll(Arrays.asList(j));              // to change the original
        timeslice = t;
        tally = 0;
        sum = 0;
        
        System.out.print("\nRR:\nProcessing " + j.length + " random jobs with "
                + "a time slice of " + timeslice + " units...");
        
        while(!jobs.isEmpty()) {                          // process jobs until
            Job temp = new Job(jobs.get(0));              // the queue is empty
            if(temp.remTime < timeslice) {
                tally += temp.remTime;          
                temp.remTime = 0;                         // process each job for
            }                                             // remaining time or
            else {                                        // for the time slice,
                tally += timeslice;                       // whichever is smaller
                temp.remTime -= timeslice;
            }
            
            if(temp.remTime > 0) {                        // if job not finished
                jobs.remove(0);                             
                jobs.add(temp);                           // send to end of line
            }
            else {
                temp.turnaround = tally;
                sum += tally;
                jobs.remove(0);
            }
        }
        
        System.out.println("done.\nTotal processing time for the batch was "
                        + tally + " units.");
        System.out.println("Avg turnaround time for each job was "
                        + (sum / j.length) + " units.");
    }
    
    // Jobs processed shortest first using a minimum priority queue
    public static void SJF(Job[] j, int t) {
        int N = j.length;
        Job[] jobs = new Job[N];
        System.arraycopy(j, 0, jobs, 0, N);         // copy the array
        
        tally = 0;
        sum = 0;
        
        N--;                                // N is last element (length - 1)
        for(int i=(N-1)/2; i>=0; i--)       // Build the heap
            sink(jobs, i, N);
        
        System.out.print("\nSJF:\nProcessing " + j.length + " random jobs (time "
                + "slice irrelevant)...");
        
        while(N > 0) {                                  // process the shortest
            tally += jobs[0].reqTime;                      // running total
            sum += tally;
            exch(jobs, 0, N--);                            // keep the shortest
            sink(jobs, 0, N);                              // in first place
        }
        tally += jobs[0].reqTime;  sum += tally;           // process last job
        
        System.out.println("done.\nTotal processing time for the batch was "
                        + tally + " units.");
        System.out.println("Avg turnaround time for each job was "
                        + (sum / jobs.length) + " units.");
    }
    
    // sink "greater" items to the bottom (MinPQ)
    private static void sink(Comparable a[], int p, int N) {
        int c = 2*p + 1;                               // 1st possible child of p
        while(c <= N) {  // while a child exists
            if(c+1 <= N && less(a[c+1], a[c])) c++;      // choose lowest sibling
            if(!less(a[c],a[p])) break;     // if parent already <= child, stop
            exch(a, c, p);
            p = c;                          // child becomes next node to look at
            c = 2*c + 1;                    // first child of that node
        }
    }
    
    private static boolean less(Comparable v, Comparable w) 
    {   return v.compareTo(w) < 0;   }
    
    private static void exch(Comparable a[], int i, int j)
    {   Comparable temp = a[i];  
        a[i] = a[j];  
        a[j] = temp;   
    }
}

public class JobScheduling {

    public static void main(String[] args) {
        int jobsize = 100;
        Job[] myjobs = new Job[jobsize];
        
        // initialize jobs with random #s from 1 to jobsize
        for(int i=0; i<jobsize; i++)
            myjobs[i] = new Job( (int) (Math.random()*jobsize + 1) );  
        
        ScheduleTest.FIFO(myjobs, 20);
        ScheduleTest.SJF(myjobs, 20);
        ScheduleTest.RR(myjobs, 20);
        ScheduleTest.RR(myjobs, 15);
        ScheduleTest.RR(myjobs, 10);
        ScheduleTest.RR(myjobs, 5);        
    }
}
/*
FIFO:
Processing 100 random jobs (time slice irrelevant)...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 2469 units.

SJF:
Processing 100 random jobs (time slice irrelevant)...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 1684 units.

RR:
Processing 100 random jobs with a time slice of 20 units...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 3262 units.

RR:
Processing 100 random jobs with a time slice of 15 units...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 3313 units.

RR:
Processing 100 random jobs with a time slice of 10 units...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 3295 units.

RR:
Processing 100 random jobs with a time slice of 5 units...done.
Total processing time for the batch was 5006 units.
Avg turnaround time for each job was 3299 units.
*/