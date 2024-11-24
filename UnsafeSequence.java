import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multiple threads operates on the same variable at the same time
 */
public class UnsafeSequence {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int threadsCount = 40;
        int valuesCounted = 40;

        AtomicBoolean start = new AtomicBoolean(false);
        Counter c = new Counter();

        Callable<List<String>> callable = () -> {
            List<String> numbers = new ArrayList<>(valuesCounted);
            String threadName = Thread.currentThread().getName();
            System.out.println("Thread activated: " + threadName);
            while (!start.get()) {
            } // just wait til start is set to true
            System.out.println("Thread started: " + threadName);


            for (int i = 0; i < valuesCounted; i++) {
                numbers.add(String.valueOf(c.getNext()));
            }

            String result = String.join("-", numbers);
            System.out.println("Results from thread: " + threadName + " is: " + result);

            return numbers;
        };

        List<FutureTask> tasks = new ArrayList<>(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            FutureTask<List<String>> futureTask = new FutureTask<>(callable);
            tasks.add(futureTask);
            Thread t = new Thread(futureTask);
            t.start();
        }

        // it does nothing until started
        start.set(true);

        List<String> allNumbers = new ArrayList<>();
        for (FutureTask<List<String>> futureTask : tasks) {
            allNumbers.addAll(futureTask.get());
        }

        System.out.println("All numbers: " + allNumbers.size());
        System.out.println("Unique numbers: " + new HashSet<>(allNumbers).size());

        Map<String, String> existingNumbers = new HashMap<>();
        for (String number : allNumbers) {
            if (existingNumbers.containsKey(number)) {
                System.out.println("Duplicate number: " + number);
            }
            existingNumbers.putIfAbsent(number, number);
        }

        System.out.println("End");
    }
}

class Counter {

    private int value;

    public int getNext() {
        // looks like a single operation, but there are 3 operations
        // - read the value from the memory
        // - increase it
        // - write that value back to the memory
        return ++value;
    }
}

class SynchronizedCounter {

    private int value;

    public synchronized int getNext() {
        return ++value;
    }
}

class AtomicCounter {

    private AtomicInteger value = new AtomicInteger();

    public synchronized int getNext() {
        return value.getAndIncrement();
    }
}