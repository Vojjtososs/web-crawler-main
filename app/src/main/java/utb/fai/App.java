package utb.fai;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

public class App {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Missing parameter - start URL");
            return;
        }

        String startUrl = args[0];
        int maxDepth = 2; // Výchozí hodnota dle zadání

        if (args.length >= 2) {
            try {
                maxDepth = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Max depth must be an integer.");
                return;
            }
        }

        // Sdílené datové struktury pro multithreading (z požadavků zadání)
        Set<String> visitedURIs = ConcurrentHashMap.newKeySet();
        ConcurrentHashMap<String, AtomicInteger> wordCounts = new ConcurrentHashMap<>();

        // Multithreading setup
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        Phaser phaser = new Phaser(1); // Hlavní vlákno se registruje

        System.err.println("Zahajuji prohledavani: " + startUrl + " (hloubka: " + maxDepth + ")");

        // Spuštění parsování počáteční stránky
        URIinfo startUriInfo = new URIinfo(startUrl, 0);
        phaser.register();
        executor.submit(new ParserCallback(startUriInfo, maxDepth, visitedURIs, wordCounts, executor, phaser));

        // Hlavní vlákno čeká, než se všechny vygenerované "ParserCallback" úlohy dokončí
        phaser.arriveAndAwaitAdvance();
        executor.shutdown();

        // Výpis 20 nejčastějších slov na standardní výstup
        printTopWords(wordCounts);
    }

    private static void printTopWords(ConcurrentHashMap<String, AtomicInteger> wordCounts) {
        wordCounts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().get(), e1.getValue().get())) // Sestupné řazení
                .limit(20)
                .forEach(entry -> System.out.println(entry.getKey() + ";" + entry.getValue().get()));
    }
}