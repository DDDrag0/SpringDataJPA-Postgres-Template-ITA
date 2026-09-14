package com.devtiro.database.services.impl;

import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.repositories.AuthorRepository;
import com.devtiro.database.services.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * <h2>SERVIZIO APPLICATIVO: {@link AuthorServiceImpl}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe implementa l'interfaccia {@code AuthorService} e costituisce il cuore dello
 * <b>Strato di Servizio (Service Layer)</b> per la risorsa "Author". Ha la responsabilità di
 * orchestrare le regole di business dell'applicazione e coordinare la persistenza dei dati,
 * agendo come intermediario sicuro tra lo strato di presentazione (Controller) e lo strato
 * di persistenza (Repository).</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Service}</b>: Indica a Spring Boot che si tratta di un componente di business
 *       (Bean), abilitandone il rilevamento automatico e l'inserimento nell'Application Context.</li>
 *   <li><b>Constructor Injection</b>: Utilizza l'iniezione tramite costruttore per accoppiare
 *       in modo saldo e testabile {@link com.devtiro.database.repositories.AuthorRepository}.</li>
 *   <li><b>La Scelta di Design del Metodo {@code save}</b>: Invece di mantenere un metodo specifico
 *       per la sola creazione, l'interfaccia è stata refattorizzata per utilizzare un unico metodo
 *       generico {@code save()}. Questo si allinea con il comportamento nativo di Spring Data JPA,
 *       il cui metodo {@code .save()} effettua un inserimento (SQL Insert) se la chiave è assente,
 *       o una sovrascrittura (SQL Update) se l'ID esiste già nel database.</li>
 *   <li><b>Gestione della Paginazione</b>: Nello sviluppo del metodo {@code findAll()}, la lista di
 *       entità restituita dall'{@code iterable} del repository viene incapsulata ed elaborata tramite
 *       le API di {@link java.util.stream.StreamSupport} per garantire un tipo di ritorno standardizzato.</li>
 * </ul>
 *
 * <p><b>Metodi Gestiti:</b></p>
 * <ul>
 *   <li><b>{@code save(AuthorEntity)}</b>: Salva o aggiorna un autore nel database relazionale.</li>
 *   <li><b>{@code findAll()}</b>: Recupera l'elenco completo degli autori convertendo l'iterable in una lista Java.</li>
 *   <li><b>{@code findOne(Long)}</b>: Cerca un autore tramite ID, restituendo un {@link java.util.Optional}
 *       per evitare eccezioni di tipo NullPointerException nel controller.</li>
 *   <li><b>{@code isExists(Long)}</b>: Effettua una chiamata pass-through a {@code existsById} per ottimizzare
 *       le verifiche preventive di integrità.</li>
 *   <li><b>{@code partialUpdate(Long, AuthorEntity)}</b>: Applica aggiornamenti parziali (PATCH). Recupera
 *       l'autore esistente, ispeziona i campi del payload tramite {@code Optional.ofNullable} e sovrascrive
 *       solo i valori non nulli prima di effettuare il salvataggio.</li>
 *   <li><b>{@code delete(Long)}</b>: Rimuove in modo definitivo l'autore corrispondente all'ID.</li>
 * </ul>
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    /**
     * Costruttore unico per la Dependency Injection.
     *
     * @param authorRepository la repository per l'accesso ai dati degli autori
     */
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Salva o aggiorna un autore a database.
     *
     * @param authorEntity l'entità autore da persistere
     * @return l'entità salvata e sincronizzata con lo stato del database
     */
    @Override
    public AuthorEntity save(AuthorEntity authorEntity) {
        return authorRepository.save(authorEntity);
    }

    /**
     * Recupera l'elenco completo degli autori registrati.
     *
     * @return una {@link List} di tutte le entità {@link AuthorEntity}
     */
    @Override
    public List<AuthorEntity> findAll() {
        return StreamSupport.stream(authorRepository
                        .findAll()
                        .spliterator(),
                        false)
                .collect(Collectors.toList());
    }

    /**
     * Cerca un singolo autore in base al suo identificativo univoco.
     *
     * @param id l'identificativo dell'autore
     * @return un {@link Optional} contenente l'entità se trovata, altrimenti vuoto
     */
    @Override
    public Optional<AuthorEntity> findOne(Long id) {
        return authorRepository.findById(id);
    }

    /**
     * Verifica l'esistenza di un autore a database.
     *
     * @param id l'identificativo dell'autore
     * @return {@code true} se l'autore esiste, altrimenti {@code false}
     */
    @Override
    public boolean isExists(Long id) {
        return authorRepository.existsById(id);
    }

    /**
     * Esegue l'aggiornamento parziale (PATCH) di un autore esistente.
     *
     * @param id l'identificativo dell'autore da modificare
     * @param authorEntity l'entità contenente i campi parziali da sovrascrivere
     * @return l'entità autore aggiornata e persistita
     * @throws RuntimeException se l'autore da aggiornare non viene trovato a database
     */
    @Override
    public AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity) {
        // Assicura l'allineamento dell'ID sull'entità passata
        authorEntity.setId(id);

        // Recupera l'autore esistente dal database
        return authorRepository.findById(id).map(existingAuthor -> {
            // Se il campo "name" dell'entità in ingresso non è null, lo aggiorna sul record esistente
            Optional.ofNullable(authorEntity.getName()).ifPresent(existingAuthor::setName);
            // Se il campo "age" dell'entità in ingresso non è null, lo aggiorna sul record esistente
            Optional.ofNullable(authorEntity.getAge()).ifPresent(existingAuthor::setAge);
            // Salva l'entità esistente con le sole modifiche apportate
            return authorRepository.save(existingAuthor);
        }).orElseThrow(() -> new RuntimeException("Author does not exist")); // Misura di protezione di fallback
    }

    /**
     * Rimuove definitivamente un autore in base al suo identificativo.
     *
     * @param id l'identificativo dell'autore da eliminare
     */
    @Override
    public void delete(Long id) {
        authorRepository.deleteById(id);
    }
}