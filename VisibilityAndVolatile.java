import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class VisibilityAndVolatile {

    private static final MagicContainer magicContainer = new MagicContainer();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Long> callable = () -> {
            while (!magicContainer.ready) {
                Thread.yield();
            }
            long localmagic = magicContainer.magic - Long.MAX_VALUE + 1;
            return localmagic;
        };

        List<FutureTask> tasks = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            FutureTask<Long> futureTask = new FutureTask<>(callable);
            tasks.add(futureTask);
            Thread t = new Thread(futureTask);
            t.start();
        }

        magicContainer.ready = true;
        magicContainer.magic = Long.MAX_VALUE;

        List<Long> longs = new ArrayList<>(3000);
        for (FutureTask<Long> futureTask : tasks) {
            longs.add(futureTask.get());
        }

        System.out.println(longs);
        System.out.println(new HashSet<>(longs));
    }

    static class MagicContainer {
        boolean ready;
        long magic;
    }
}
