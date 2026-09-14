package com.devtiro.database.services;

import com.devtiro.database.domain.entities.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * <h2>INTERFACCIA SERVIZIO: {@link BookService}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa interfaccia definisce il contratto formale dello <b>Strato di Servizio (Service Layer)</b>
 * per la risorsa "Book". Gestisce la logica di business associata ai libri e coordina la
 * persistenza e l'accesso ai dati delle entità {@code BookEntity}.</p>
 *
 * <p><b>Dettagli di Design ed Evoluzione:</b></p>
 * <ul>
 *   <li><b>Integrità dei Dati (ISBN Override):</b> Nel metodo {@code createUpdateBook}, il servizio
 *       impone l'ISBN proveniente dal percorso URL sul corpo del record. Questo previene discrepanze
 *       tra la chiave naturale nell'URL e il corpo JSON inviato dal client.</li>
 *   <li><b>Evoluzione verso la Paginazione:</b> Introduce il supporto sovraccaricato a
 *       {@link org.springframework.data.domain.Pageable} e {@link org.springframework.data.domain.Page}
 *       per consentire il caricamento efficiente, controllato e scalabile dei libri sul database.</li>
 *   <li><b>Protezione degli Aggiornamenti Parziali (PATCH):</b> Il metodo {@code partialUpdate}
 *       limita l'aggiornamento parziale al solo titolo del libro, impedendo l'alterazione abusiva dell'ISBN
 *       (chiave primaria naturale) o della foreign key dell'autore, per salvaguardare l'integrità referenziale.</li>
 * </ul>
 *
 * <p><b>Metodi Dichiarati (Contratto di Business):</b></p>
 * <ul>
 *   <li><b>{@code createUpdateBook(String, BookEntity)}</b>: Crea o aggiorna interamente un libro assicurando
 *       la coerenza dell'ISBN fornito dall'URL e gestendo il salvataggio a cascata dell'autore.</li>
 *   <li><b>{@code findAll()}</b>: Recupera l'intera lista di libri presenti nel database (da evitare su grandi volumi).</li>
 *   <li><b>{@code findAll(Pageable)}</b>: Recupera un sottoinsieme di libri strutturato in pagine, con metadati
 *       utili per il client.</li>
 *   <li><b>{@code findOne(String)}</b>: Cerca un libro tramite il suo codice ISBN (chiave naturale stringa).</li>
 *   <li><b>{@code isExists(String)}</b>: Controlla la presenza di un libro nel sistema tramite il suo ISBN.</li>
 *   <li><b>{@code partialUpdate(String, BookEntity)}</b>: Modifica selettivamente solo gli attributi consentiti (titolo)
 *       di un libro esistente.</li>
 *   <li><b>{@code delete(String)}</b>: Rimuove in modo definitivo un libro tramite il suo ISBN.</li>
 * </ul>
 */
public interface BookService {

    /**
     * Crea un nuovo libro o ne aggiorna completamente uno esistente, forzando la coerenza dell'ISBN.
     *
     * @param isbn l'identificativo unico (codice ISBN) da applicare al libro
     * @param book l'entità libro contenente i dati da persistere
     * @return l'entità libro salvata nel database
     */
    BookEntity createUpdateBook(String isbn, BookEntity book);

    /**
     * Recupera l'elenco completo di tutti i libri registrati nel sistema.
     *
     * @return una {@link List} contenente tutte le entità {@link BookEntity}
     */
    List<BookEntity> findAll();

    /**
     * Recupera una pagina di libri in base ai criteri di paginazione e ordinamento forniti.
     *
     * @param pageable i parametri di paginazione (page, size, sort)
     * @return un oggetto {@link Page} contenente i libri della pagina corrente
     */
    Page<BookEntity> findAll(Pageable pageable);

    /**
     * Cerca un singolo libro a database tramite il suo codice ISBN.
     *
     * @param isbn il codice ISBN del libro da cercare
     * @return un {@link Optional} contenente il libro se trovato, altrimenti vuoto
     */
    Optional<BookEntity> findOne(String isbn);

    /**
     * Verifica se un libro esiste nel sistema in base all'ISBN.
     *
     * @param isbn il codice ISBN del libro da verificare
     * @return {@code true} se il libro esiste, altrimenti {@code false}
     */
    boolean isExists(String isbn);

    /**
     * Esegue l'aggiornamento parziale (PATCH) delle sole proprietà consentite (titolo) di un libro.
     *
     * @param isbn l'identificativo unico del libro da modificare
     * @param bookEntity l'entità contenente le modifiche parziali
     * @return l'entità libro aggiornata e persistita
     */
    BookEntity partialUpdate(String isbn, BookEntity bookEntity);

    /**
     * Rimuove in modo permanente un libro dal sistema tramite il suo ISBN.
     *
     * @param isbn il codice ISBN del libro da eliminare
     */
    void delete(String isbn);
}