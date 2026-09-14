package com.devtiro.database.controllers;

import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.mappers.Mapper;
import com.devtiro.database.services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h2>CONTROLLER REST: {@link AuthorController}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe funge da punto di ingresso dello <b>Strato di Presentazione (Presentation Layer)</b>
 * per la risorsa "Author". Ha la responsabilità esclusiva di esporre gli endpoint HTTP pubblici,
 * gestire il ciclo di richiesta/risposta RESTful e disaccoppiare lo strato esterno dalle entità di
 * persistenza tramite l'uso dei Data Transfer Objects (DTO).</p>
 *
 * <p><b>Principali Responsabilità:</b></p>
 * <ul>
 *   <li><b>Gestione del Disaccoppiamento:</b> Accetta e restituisce esclusivamente oggetti {@code AuthorDto},
 *       delegando ad un mappatore dedicato ({@code AuthorMapper}) la conversione bidirezionale da/verso
 *       l'entità di persistenza {@code AuthorEntity}.</li>
 *   <li><b>Controllo degli Stati HTTP:</b> Sfrutta {@link org.springframework.http.ResponseEntity} per
 *       restituire codici di stato precisi ed espressivi (e.g., 201 Created per la scrittura, 204 No Content
 *       per la cancellazione, 404 Not Found per risorse inesistenti).</li>
 *   <li><b>Iniezione delle Dipendenze:</b> Utilizza la Constructor Injection per integrare in modo sicuro
 *       e testabile i servizi di business ({@code AuthorService}) e di mappatura ({@code Mapper}).</li>
 * </ul>
 *
 * <p><b>Endpoint Gestiti (Mappatura REST):</b></p>
 * <ul>
 *   <li><b>POST {@code /authors}</b>: Creazione di un nuovo autore. Restituisce {@code 201 Created} ed il
 *       DTO dell'oggetto persistito con ID autogenerato.</li>
 *   <li><b>GET {@code /authors}</b>: Recupero dell'elenco completo degli autori registrati nel sistema.
 *       Restituisce {@code 200 OK}.</li>
 *   <li><b>GET {@code /authors/{id}}</b>: Recupero di un singolo autore tramite il suo identificativo unico.
 *       Restituisce {@code 200 OK} se presente, oppure {@code 404 Not Found}.</li>
 *   <li><b>PUT {@code /authors/{id}}</b>: Aggiornamento completo (sostituzione integrale) dello stato di un autore.
 *       Restituisce {@code 200 OK} se aggiornato, o {@code 404 Not Found} se inesistente.</li>
 *   <li><b>PATCH {@code /authors/{id}}</b>: Aggiornamento parziale. Consente di modificare selettivamente solo
 *       alcuni campi (es. solo il nome), lasciando inalterati gli altri. Restituisce {@code 200 OK}.</li>
 *   <li><b>DELETE {@code /authors/{id}}</b>: Eliminazione definitiva di un autore dal database. Restituisce
 *       {@code 204 No Content} indipendentemente dalla presenza preventiva del record.</li>
 * </ul>
 */
@RestController
public class AuthorController {

    private AuthorService authorService;

    private Mapper<AuthorEntity, AuthorDto> authorMapper;

    /**
     * Costruttore unico per la Dependency Injection.
     *
     * @param authorService il servizio di business per la gestione degli autori
     * @param authorMapper il componente di mappatura DTO/Entity
     */
    public AuthorController(AuthorService authorService, Mapper<AuthorEntity, AuthorDto> authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    /**
     * Crea un nuovo autore nel sistema.
     *
     * @param author il DTO dell'autore da creare
     * @return un {@link ResponseEntity} contenente il DTO dell'autore creato e lo stato {@code 201 CREATED}
     */
    @PostMapping(path = "/authors")
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto author) {
        // Si converte il DTO ricevuto in Entità
        AuthorEntity authorEntity = authorMapper.mapFrom(author);
        // Si salva l'entità sul database tramite il Service
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);
        // Con authorMapper.mapTo(savedAuthorEntity) si riconverte l'entità salvata in DTO per la risposta
        // Infine si restituisce il DTO con lo status HTTP 201 Created
        return new ResponseEntity<>(authorMapper.mapTo(savedAuthorEntity), HttpStatus.CREATED);
    }

    /**
     * Recupera la lista di tutti gli autori presenti a sistema.
     *
     * @return una lista di {@link AuthorDto}
     */
    @GetMapping(path = "/authors")
    public List<AuthorDto> listAuthors() {
        List<AuthorEntity> authors = authorService.findAll();
        return authors.stream()
                .map(authorMapper::mapTo)
                .collect(Collectors.toList());
    }

    /**
     * Recupera un autore specifico tramite il suo ID.
     *
     * @param id l'identificativo univoco dell'autore
     * @return un {@link ResponseEntity} col DTO se trovato ({@code 200 OK}), altrimenti {@code 404 NOT FOUND}
     */
    @GetMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable("id") Long id) {
        Optional<AuthorEntity> foundAuthor = authorService.findOne(id);
        return foundAuthor.map(authorEntity -> {
            AuthorDto authorDto = authorMapper.mapTo(authorEntity);
            return new ResponseEntity<>(authorDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Esegue l'aggiornamento completo (sovrascrittura) di un autore esistente.
     *
     * @param id l'identificativo univoco dell'autore da aggiornare
     * @param authorDto il DTO contenente i nuovi dati
     * @return il DTO aggiornato con {@code 200 OK}, oppure {@code 404 NOT FOUND} se l'id non esiste
     */
    @PutMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> fullUpdateAuthor(
            @PathVariable("id") Long id,
            @RequestBody AuthorDto authorDto) {

        // Verifica se l'autore esiste
        if(!authorService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }

        // Allinea l'ID del DTO con quello del percorso URL
        authorDto.setId(id);
        // Converte il DTO in Entità
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        // Salva l'entità aggiornata nel database
        AuthorEntity savedAuthorEntity = authorService.save(authorEntity);
        // Riconverte l'entità in DTO e restituisce 200 OK
        return new ResponseEntity<>(
                authorMapper.mapTo(savedAuthorEntity),
                HttpStatus.OK);
    }

    /**
     * Esegue l'aggiornamento parziale di un autore (patch dei soli campi inviati).
     *
     * @param id l'identificativo univoco dell'autore
     * @param authorDto il DTO contenente le modifiche parziali
     * @return il DTO aggiornato con {@code 200 OK}, oppure {@code 404 NOT FOUND} se l'id non esiste
     */
    @PatchMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> partialUpdate(
            @PathVariable("id") Long id,
            @RequestBody AuthorDto authorDto
    ) {
        // Verifica se l'autore esiste a DB
        if(!authorService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }

        // Converte il DTO in Entità
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        // Esegue l'aggiornamento parziale tramite il servizio
        AuthorEntity updatedAuthor = authorService.partialUpdate(id, authorEntity);
        // Converte il risultato in DTO e restituisce 200 OK
        return new ResponseEntity<>(
                authorMapper.mapTo(updatedAuthor),
                HttpStatus.OK);
    }

    /**
     * Rimuove definitivamente un autore dal sistema tramite il suo ID.
     *
     * @param id l'identificativo univoco dell'autore da eliminare
     * @return un {@link ResponseEntity} vuoto con stato {@code 204 NO CONTENT}
     */
    @DeleteMapping(path = "/authors/{id}")
    public ResponseEntity deleteAuthor(@PathVariable("id") Long id) {
        // Invocazione del servizio per l'eliminazione
        authorService.delete(id);
        // Ritorno dello status code 204 No Content
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}