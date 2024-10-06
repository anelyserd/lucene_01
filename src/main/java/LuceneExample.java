import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class LuceneExample {
    public static void main(String[] args) {
        // Step 1: Create a directory to store the index
        try (Directory indexDirectory = FSDirectory.open(Paths.get("/Users/anelysedavid/Documents/Thesis/Lucene/output"))) {
            // Step 2: Create an analyzer
            StandardAnalyzer analyzer = new StandardAnalyzer();

            // Step 3: Configure the IndexWriter
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(indexDirectory, config);

            // Step 4: Index PDF documents
            File pdfFolder = new File("/Users/anelysedavid/Documents/Thesis/data_sample");
            for (File file : pdfFolder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    indexPdf(indexWriter, file);
                }
            }

            // Step 5: Close the IndexWriter
            indexWriter.close();
            System.out.println("Indexing completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Helper method to add PDF documents to the index
    private static void indexPdf(IndexWriter indexWriter, File file) throws IOException {

        // Step 1: Load the PDF file
        PDDocument document = PDDocument.load(file);

        // Step 2: Extract the text from the PDF
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String content = pdfStripper.getText(document);

        // Step 3: Print the extracted content to the console
//        System.out.println("Content of " + file.getName() + ":");
//        System.out.println(content);
//        System.out.println("---------------------------------");

        // Step 4: Create a new Lucene Document
        Document luceneDocument = new Document();

        // Step 5: Add the PDF file's name as a field (can be used as a title)
        luceneDocument.add(new TextField("title", file.getName(), Field.Store.YES));

        // Step 6: Add the extracted text as the content field
        luceneDocument.add(new TextField("content", content, Field.Store.YES));

        // Step 7: Add the document to the Lucene index
        indexWriter.addDocument(luceneDocument);

        // Step 8: Close the PDF document after processing
        document.close();
    }
}
