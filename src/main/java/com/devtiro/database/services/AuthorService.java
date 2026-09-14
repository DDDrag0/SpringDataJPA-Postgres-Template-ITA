package com.devtiro.database.services;

import com.devtiro.database.domain.entities.AuthorEntity;

import java.util.List;
import java.util.Optional;

/**
 * <h2>INTERFACCIA SERVIZIO: {@link AuthorService}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa interfaccia definisce il contratto formale dello <b>Strato di Servizio (Service Layer)</b>
 * per la risorsa "Author". Funge da mediatore tra lo strato di presentazione (Controller) e lo
 * strato di persistenza (Repository), isolando la logica di business e orchestrando le operazioni
 * sulle entità {@code AuthorEntity}.</p>
 *
 * <p><b>Dettagli di Design ed Evoluzione:</b></p>
 * <ul>
 *   <li><b>Risoluzione del Disaccoppiamento:</b> Mentre il Controller gestisce i DTO per l'esterno,
 *       lo strato di servizio opera esclusivamente con le entità di persistenza, garantendo che
 *       la logica di business rimanga isolata dalle problematiche di presentazione.</li>
 *   <li><b>Refactoring del Metodo {@code save}:</b> Assegnando una precisa linea guida aziendale,
 *       l'iniziale metodo {@code createAuthor} è stato unificato e reso generico sotto il nome
 *       {@code save(AuthorEntity)}. Questo riflette perfettamente l'interfaccia di Spring Data JPA,
 *       dove lo stesso metodo gestisce sia l'inserimento (SQL Insert) che l'aggiornamento completo (PUT).</li>
 * </ul>
 *
 * <p><b>Metodi Dichiarati (Contratto di Business):</b></p>
 * <ul>
 *   <li><b>{@code save(AuthorEntity)}</b>: Salva una nuova entità o aggiorna interamente una esistente,
 *       restituendo l'entità persistita.</li>
 *   <li><b>{@code findAll()}</b>: Recupera l'elenco completo di tutti gli autori registrati nel sistema.</li>
 *   <li><b>{@code findOne(Long)}</b>: Cerca un autore tramite il suo ID numerico, restituendo un {@link java.util.Optional}
 *       per gestire in modo sicuro i casi in cui l'ID non sia presente.</li>
 *   <li><b>{@code isExists(Long)}</b>: Verifica in modo efficiente la presenza preventiva di un autore nel database.</li>
 *   <li><b>{@code partialUpdate(Long, AuthorEntity)}</b>: Applica modifiche parziali (PATCH) ispezionando
 *       solo i campi non nulli del payload inviato dal client.</li>
 *   <li><b>{@code delete(Long)}</b>: Rimuove permanentemente un autore dal database relazionale tramite il suo ID.</li>
 * </ul>
 */
public interface AuthorService {

    /**
     * Salva o aggiorna un autore a database.
     *
     * @param authorEntity l'entità autore da persistere
     * @return l'entità salvata e sincronizzata con lo stato del database
     */
    AuthorEntity save(AuthorEntity authorEntity);

    /**
     * Recupera l'elenco completo degli autori registrati.
     *
     * @return una {@link List} di tutte le entità {@link AuthorEntity}
     */
    List<AuthorEntity> findAll();

    /**
     * Cerca un singolo autore in base al suo identificativo univoco.
     *
     * @param id l'identificativo dell'autore
     * @return un {@link Optional} contenente l'entità se trovata, altrimenti vuoto
     */
    Optional<AuthorEntity> findOne(Long id);

    /**
     * Verifica l'esistenza di un autore a database tramite ID.
     *
     * @param id l'identificativo dell'autore
     * @return {@code true} se l'autore esiste, altrimenti {@code false}
     */
    boolean isExists(Long id);

    /**
     * Esegue l'aggiornamento parziale (PATCH) di un autore esistente.
     *
     * @param id l'identificativo dell'autore da modificare
     * @param authorEntity l'entità contenente i campi parziali da sovrascrrivere
     * @return l'entità autore aggiornata e persistita
     */
    AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity);

    /**
     * Rimuove definitivamente un autore in base al suo identificativo.
     *
     * @param id l'identificativo dell'autore da eliminare
     */
    void delete(Long id);
}