import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadedSampler {
    private int threadMax;
    private int noGames;
    private int scientistIter;
    private int creatureIter;
    private double exploration;
    private ExecutorService executor;

    public ThreadedSampler(int scientistIter, int creatureIter, double exploration, int threadMax, int noGames) {
        this.scientistIter = scientistIter;
        this.creatureIter = creatureIter;
        this.exploration = exploration;
        this.threadMax = threadMax;
        this.noGames = noGames;
        // Create executor service
        this.executor = Executors.newFixedThreadPool(threadMax);
    }

    public List<CallableGame> runSample() throws InterruptedException {
        // Make all the callables
        List<CallableGame> callables = IntStream.range(0, this.noGames).mapToObj(i -> {
            return new CallableGame(
                    new ISMCTS(this.scientistIter, false, this.exploration),
                    new ISMCTS(this.creatureIter, false, this.exploration)
            );
        }).collect(Collectors.toList());
        // Run them all and get results
        List<Future<CallableGame>> results = this.executor.invokeAll(callables);
        // Return simulation results
        return results.stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(c -> !Objects.isNull(c))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadedSampler ts = new ThreadedSampler(10000, 10000, 0.7, 4, 100);
        List<CallableGame> games = ts.runSample();
        long sciWins = games.stream()
                .filter(g -> g.getWinner() == PlayerType.SCIENTIST)
                .count();
        long creatureWins = games.size() - sciWins;
        System.out.println("Scientist: " + sciWins + ", Creature: " + creatureWins);
        ts.executor.shutdown();
    }
}
