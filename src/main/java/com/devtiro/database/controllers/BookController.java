package com.devtiro.database.controllers;

import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.mappers.Mapper;
import com.devtiro.database.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>CONTROLLER REST: {@link BookController}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe funge da punto d'ingresso dello <b>Strato di Presentazione (Presentation Layer)</b>
 * per la risorsa "Book". Gestisce gli endpoint RESTful per l'intero ciclo di vita dei libri,
 * disaccoppiando l'API esterna dalle logiche interne tramite l'uso dei Data Transfer Objects (DTO).</p>
 *
 * <p><b>Principali Responsabilità:</b></p>
 * <ul>
 *   <li><b>Idempotenza e Chiavi Naturali:</b> A differenza della risorsa autore, l'identificatore
 *       principale dei libri (ISBN) viene fornito direttamente dal client. L'endpoint di creazione
 *       e quello di aggiornamento completo sono quindi unificati sotto il metodo HTTP {@code PUT}.</li>
 *   <li><b>Mappatura e Disaccoppiamento:</b> Delega la conversione bidirezionale tra {@code BookEntity}
 *       e {@code BookDto} al componente {@code BookMapper} per evitare la fuga di dettagli di persistenza.</li>
 *   <li><b>Paginazione ad alte prestazioni:</b> Sfrutta lo standard {@link org.springframework.data.domain.Pageable}
 *       per restituire i record in pagine, garantendo che le letture massive non saturino la memoria del server.</li>
 * </ul>
 *
 * <p><b>Endpoint Gestiti (Mappatura REST):</b></p>
 * <ul>
 *   <li><b>PUT {@code /books/{isbn}}</b>: Creazione ed aggiornamento completo. Se l'ISBN non è a database,
 *       salva la nuova entità restituendo {@code 201 Created}. Se è già presente, aggiorna
 *       l'intera risorsa restituendo {@code 200 OK}. L'ISBN indicato nel percorso URL sovrascrive
 *       sempre l'eventuale valore inserito nel corpo JSON per ragioni di coerenza dei dati.</li>
 *   <li><b>GET {@code /books}</b>: Recupero collettivo paginato dei libri. Accetta parametri di query
 *       quali {@code size}, {@code page} e {@code sort} (es. {@code /books?size=5&page=0}), restituendo
 *       un oggetto {@code Page<BookDto>}.</li>
 *   <li><b>GET {@code /books/{isbn}}</b>: Recupero di un singolo libro tramite ISBN. Restituisce
 *       {@code 200 OK} in caso di successo, altrimenti {@code 404 Not Found}.</li>
 *   <li><b>PATCH {@code /books/{isbn}}</b>: Aggiornamento parziale della risorsa. Consente al client
 *       di modificare selettivamente attributi (como il titolo) mantenendo inalterata la relazione dell'autore.
 *       Restituisce {@code 200 OK}.</li>
 *   <li><b>DELETE {@code /books/{isbn}}</b>: Cancellazione permanente di un libro a database tramite il suo ISBN.
 *       Restituisce lo status code {@code 204 No Content}.</li>
 * </ul>
 */
@RestController
public class BookController {

    private BookService bookService;
    private Mapper<BookEntity, BookDto> bookMapper;

    /**
     * Costruttore unico per la Dependency Injection.
     *
     * @param bookMapper il componente di mappatura DTO/Entity
     * @param bookService il servizio di business per la gestione dei libri
     */
    public BookController(Mapper<BookEntity, BookDto> bookMapper, BookService bookService) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    /**
     * Crea un nuovo libro o aggiorna completamente un libro esistente tramite il suo ISBN.
     *
     * @param isbn l'identificativo unico del libro (codice ISBN)
     * @param bookDto il DTO contenente i dati del libro
     * @return un {@link ResponseEntity} col DTO aggiornato/creato e lo status HTTP adeguato
     */
    @PutMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> createUpdateBook(@PathVariable String isbn, @RequestBody BookDto bookDto) {
        // Converte il DTO ricevuto nel Body in Entità JPA
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        // Verifica se il libro esiste prima di eseguire l'operazione
        boolean bookExists = bookService.isExists(isbn);
        // Salva o aggiorna l'entità richiamando il servizio
        BookEntity savedBookEntity = bookService.createUpdateBook(isbn, bookEntity);
        // Riconverte l'entità persistita nel DTO di risposta
        BookDto savedUpdatedBookDto = bookMapper.mapTo(savedBookEntity);

        // Determina lo status code RESTful corretto
        if(bookExists){
            // Restituisce il DTO con lo status HTTP 200 Ok per l'aggiornamento
            return new ResponseEntity(savedUpdatedBookDto, HttpStatus.OK);
        } else {
            // Restituisce il DTO con lo status HTTP 201 Created per la creazione
            return new ResponseEntity(savedUpdatedBookDto, HttpStatus.CREATED);
        }
    }

    /**
     * Esegue l'aggiornamento parziale (patch) di un libro esistente.
     *
     * @param isbn l'identificativo unico del libro
     * @param bookDto il DTO contenente i campi da modificare
     * @return il DTO modificato con {@code 200 OK}, oppure {@code 404 NOT FOUND} se non esiste
     */
    @PatchMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> partialUpdateBook(
            @PathVariable("isbn") String isbn,
            @RequestBody BookDto bookDto
    ){
        // Controllo esistenza risorsa
        if(!bookService.isExists(isbn)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }

        // Converte il DTO parziale in Entità
        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        // Esegue la modifica parziale nel Service Layer
        BookEntity updatedBookEntity = bookService.partialUpdate(isbn, bookEntity);
        // Riconverte in DTO e restituisce 200 OK
        return new ResponseEntity<>(
                bookMapper.mapTo(updatedBookEntity),
                HttpStatus.OK);
    }

    /**
     * Recupera una pagina di libri in base ai criteri di paginazione forniti.
     *
     * @param pageable i parametri di paginazione (page, size, sort)
     * @return un oggetto {@link Page} contenente i DTO dei libri
     */
    @GetMapping(path = "/books")
    public Page<BookDto> listBooks(Pageable pageable) {
        Page<BookEntity> books = bookService.findAll(pageable);
        return books.map(bookMapper::mapTo);
    }

    /*
    //Qui vi è il vecchio processo di findAll, senza la paginazione, quindi non ottimizzato per grandi numeri di istanze nel DB
    @GetMapping(path = "/books")
    public List<BookDto> listBooks() {
        List<BookEntity> books = bookService.findAll();
        return books.stream()
                .map(bookMapper::mapTo)
                .collect(Collectors.toList());
    }
     */
    /**
     * Recupera i dettagli di un singolo libro tramite il suo ISBN.
     *
     * @param isbn l'identificativo unico del libro
     * @return un {@link ResponseEntity} col DTO se trovato ({@code 200 OK}), altrimenti {@code 404 NOT FOUND}
     */
    @GetMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> getBook(@PathVariable("isbn") String isbn) {
        Optional<BookEntity> foundBook = bookService.findOne(isbn);
        return foundBook.map(bookEntity -> {
            BookDto bookDto = bookMapper.mapTo(bookEntity);
            return new ResponseEntity<>(bookDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Rimuove definitivamente un libro dal sistema tramite il suo ISBN.
     *
     * @param isbn l'identificativo unico del libro da eliminare
     * @return un {@link ResponseEntity} vuoto con stato {@code 204 NO CONTENT}
     */
    @DeleteMapping(path = "/books/{isbn}")
    public ResponseEntity deleteBook(@PathVariable("isbn") String isbn) {
        // Invocazione del servizio per l'eliminazione
        bookService.delete(isbn);
        // Ritorno dello status code 204 No Content
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}