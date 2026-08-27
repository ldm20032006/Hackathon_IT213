package demo.btth_session13.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Loads hotel information documents into the PGVector store on startup.
 * Uses TokenTextSplitter to chunk text into ~512-token segments before embedding.
 *
 * This runner is idempotent only if PgVectorStore is configured NOT to re-add
 * duplicate documents; consider adding a flag/check to skip if already loaded.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentLoader implements ApplicationRunner {

    private final VectorStore vectorStore;

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== DocumentLoader: Starting hotel document ingestion ===");

        try {
            ingestTextFile("documents/lotusbay_info.txt");
            log.info("=== DocumentLoader: Ingestion completed successfully ===");
        } catch (Exception e) {
            log.error("DocumentLoader: Failed to ingest documents – {}", e.getMessage(), e);
        }
    }

    private void ingestTextFile(String classpathPath) {
        log.info("Loading document: {}", classpathPath);

        ClassPathResource resource = new ClassPathResource(classpathPath);
        TextReader reader = new TextReader(resource);
        reader.getCustomMetadata().put("source", classpathPath);

        List<Document> rawDocs = reader.read();
        log.info("Read {} raw document(s) from {}", rawDocs.size(), classpathPath);

        TokenTextSplitter splitter = new TokenTextSplitter(
                512,   // default chunk size in tokens
                128,   // overlap
                5,     // min chunk size
                10000, // max chunk size
                true   // keep separator
        );

        List<Document> chunks = splitter.apply(rawDocs);
        log.info("Split into {} chunk(s) – adding to VectorStore...", chunks.size());

        vectorStore.add(chunks);
        log.info("Successfully added {} chunk(s) to VectorStore.", chunks.size());
    }
}
