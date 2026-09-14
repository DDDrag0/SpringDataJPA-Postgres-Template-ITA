package com.devtiro.database.services.impl;

import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.repositories.BookRepository;
import com.devtiro.database.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <h2>SERVIZIO APPLICATIVO: {@link BookServiceImpl}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe implementa l'interfaccia {@code BookService} e gestisce la logica di business
 * per lo <b>Strato di Servizio (Service Layer)</b> della risorsa "Book". Coordina le operazioni
 * di persistenza e manipolazione dei dati dei libri nel database relazionale.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Service}</b>: Registra la classe come componente di business nel container IoC di Spring.</li>
 *   <li><b>Garanzia dell'Integrità dei Dati (ISBN Override)</b>: Durante la creazione o l'aggiornamento
 *       del libro nel metodo {@code createUpdateBook()}, il servizio forza l'applicazione dell'ISBN passato
 *       tramite il percorso URL dell'API sul corpo dell'entità ({@code book.setIsbn(isbn)}). Questa regola di
 *       sicurezza previene anomalie dovute a discordanze tra il payload JSON inviato dal client e l'URL di chiamata.</li>
 *   <li><b>Integrazione della Paginazione Scalabile</b>: Implementa il metodo sovraccaricato
 *       {@code findAll(Pageable)} delegando a {@link com.devtiro.database.repositories.BookRepository}
 *       il recupero dei dati in pagine. Questo garantisce massime prestazioni anche in presenza di
 *       milioni di record.</li>
 *   <li><b>Aggiornamento Parziale Robusto</b>: Il metodo {@code partialUpdate()} esegue un controllo selettivo
 *       sull'entità del libro. Per motivi di stabilità dell'applicazione, il servizio consente di aggiornare
 *       esclusivamente il titolo del libro, bloccando l'alterazione abusiva dell'ISBN (chiave primaria naturale)
 *       e della foreign key dell'autore.</li>
 * </ul>
 *
 * <p><b>Metodi Gestiti:</b></p>
 * <ul>
 *   <li><b>{@code createUpdateBook(String, BookEntity)}</b>: Assicura l'allineamento dell'ISBN ed esegue
 *       il salvataggio (creazione o sovrascrittura totale) a database.</li>
 *   <li><b>{@code findAll()}</b>: Restituisce l'elenco completo di tutti i libri a database (da evitare su grandi volumi).</li>
 *   <li><b>{@code findAll(Pageable)}</b>: Interroga il database restituendo un oggetto {@link org.springframework.data.domain.Page}
 *       di libri, riducendo drasticamente il consumo di memoria del server.</li>
 *   <li><b>{@code findOne(String)}</b>: Recupera un libro tramite l'ISBN (restituendo un {@link java.util.Optional}).</li>
 *   <li><b>{@code isExists(String)}</b>: Controlla se un libro è presente nel sistema usando il suo ISBN.</li>
 *   <li><b>{@code partialUpdate(String, BookEntity)}</b>: Ispeziona selettivamente ed aggiorna i soli attributi
 *       consentiti (titolo) per il libro identificato dall'ISBN.</li>
 *   <li><b>{@code delete(String)}</b>: Cancella definitivamente un libro tramite ISBN.</li>
 * </ul>
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    /**
     * Costruttore unico per la Dependency Injection.
     *
     * @param bookRepository la repository per l'accesso ai dati dei libri
     */
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Crea un nuovo libro o ne aggiorna completamente uno esistente sincronizzando l'ISBN.
     *
     * @param isbn l'identificativo unico (codice ISBN) del libro
     * @param book l'entità libro contenente i dati da persistere
     * @return l'entità libro salvata nel database
     */
    @Override
    public BookEntity createUpdateBook(String isbn, BookEntity book) {
        book.setIsbn(isbn);
        return bookRepository.save(book);
    }

    /**
     * Recupera l'elenco completo di tutti i libri registrati nel sistema.
     * Note: Da utilizzare con cautela per via del potenziale impatto sulle prestazioni.
     *
     * @return una {@link List} contenente tutte le entità {@link BookEntity}
     */
    @Override
    public List<BookEntity> findAll() {
        return StreamSupport
                .stream(
                        bookRepository.findAll().spliterator(),
                        false)
                .collect(Collectors.toList());
    }

    /**
     * Recupera una pagina di libri in base ai criteri di paginazione e ordinamento forniti.
     *
     * @param pageable i parametri di paginazione (page, size, sort)
     * @return un oggetto {@link Page} contenente i libri della pagina corrente
     */
    @Override
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Cerca un singolo libro a database tramite il suo codice ISBN.
     *
     * @param isbn il codice ISBN del libro da cercare
     * @return un {@link Optional} contenente il libro se trovato, altrimenti vuoto
     */
    @Override
    public Optional<BookEntity> findOne(String isbn) {
        return bookRepository.findById(isbn);
    }

    /**
     * Verifica se un libro esiste nel sistema in base all'ISBN.
     *
     * @param isbn il codice ISBN del libro da verificare
     * @return {@code true} se il libro esiste, altrimenti {@code false}
     */
    @Override
    public boolean isExists(String isbn) {
        return bookRepository.existsById(isbn);
    }

    /**
     * Esegue l'aggiornamento parziale (PATCH) delle sole proprietà consentite di un libro.
     *
     * @param isbn l'identificativo unico del libro da modificare
     * @param bookEntity l'entità contenente le modifiche parziali
     * @return l'entità libro aggiornata e persistita
     * @throws RuntimeException se il libro da aggiornare non esiste a database
     */
    @Override
    public BookEntity partialUpdate(String isbn, BookEntity bookEntity) {
        // Forza l'ISBN corretto sull'entità in ingresso
        bookEntity.setIsbn(isbn);

        // Cerca il libro esistente nel database
        return bookRepository.findById(isbn).map(existingBook -> {
            // Se il client ha inviato un titolo non nullo, lo aggiorna
            Optional.ofNullable(bookEntity.getTitle()).ifPresent(existingBook::setTitle);
            // Nota: non supportiamo l'aggiornamento dell'ISBN o dell'autore nidificato in questa fase
            // Salva l'entità originaria con le sole modifiche apportate
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Book does not exist")); // Blocco di sicurezza fallback
    }

    /**
     * Rimuove in modo permanente un libro dal sistema.
     *
     * @param isbn il codice ISBN del libro da eliminare
     */
    @Override
    public void delete(String isbn) {
        bookRepository.deleteById(isbn);
    }
}