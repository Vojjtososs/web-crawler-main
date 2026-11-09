package utb.fai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class ParserCallback implements Runnable {

    private final URIinfo uriInfo;
    private final int maxDepth;
    private final Set<String> visitedURIs;
    private final ConcurrentHashMap<String, AtomicInteger> wordCounts;
    private final ExecutorService executor;
    private final Phaser phaser;

    // Regulární výraz pro identifikaci slov (pouze písmena)
    private static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}]+");

    public ParserCallback(URIinfo uriInfo, int maxDepth, Set<String> visitedURIs, 
                          ConcurrentHashMap<String, AtomicInteger> wordCounts, 
                          ExecutorService executor, Phaser phaser) {
        this.uriInfo = uriInfo;
        this.maxDepth = maxDepth;
        this.visitedURIs = visitedURIs;
        this.wordCounts = wordCounts;
        this.executor = executor;
        this.phaser = phaser;
    }

    @Override
    public void run() {
        try {
            String urlString = uriInfo.uri.toString();
            
            // Kontrola, zda již URL nebylo navštíveno. Set.add() je vláknově bezpečné.
            if (!visitedURIs.add(urlString)) {
                return; 
            }

            // Připojení a získání obsahu pomocí JSoup (automaticky řeší kódování)
            Document doc = Jsoup.connect(urlString)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // Zpracování textu na stránce
            handleText(doc.body().text());

            // Zpracování odkazů, pokud jsme nedosáhli maximální hloubky
            if (uriInfo.depth < maxDepth) {
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String nextUrl = link.attr("abs:href"); // absolutní URL
                    
                    if (nextUrl.startsWith("http") && !visitedURIs.contains(nextUrl)) {
                        // Registrujeme nový úkol ve Phaseru a pošleme ho do Executoru
                        phaser.register();
                        executor.submit(new ParserCallback(
                                new URIinfo(nextUrl, uriInfo.depth + 1), 
                                maxDepth, visitedURIs, wordCounts, executor, phaser
                        ));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Chyba při zpracování " + uriInfo.uri + ": " + e.getMessage());
        } finally {
            // Bez ohledu na to, zda úkol uspěl nebo spadl, musíme Phaseru říct, že jsme skončili
            phaser.arriveAndDeregister();
        }
    }

    /**
     * Vlastní implementace analýzy četnosti slov
     */
    private void handleText(String text) {
        // Převedeme na malá písmena, odstraníme znaky co nejsou písmena a rozdělíme na slova
        String[] words = WORD_PATTERN.matcher(text).replaceAll(" ").toLowerCase().split("\\s+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                // Vláknově bezpečné zvýšení počítadla daného slova
                wordCounts.computeIfAbsent(word, k -> new AtomicInteger(0)).incrementAndGet();
            }
        }
    }
}