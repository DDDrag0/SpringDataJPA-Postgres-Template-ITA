package com.devtiro.database.repositories;

import com.devtiro.database.domain.entities.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * <h2>INTERFACCIA REPOSITORY: {@link BookRepository}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa interfaccia rappresenta lo <b>Strato di Persistenza (Persistence Layer)</b> per la
 * risorsa "Book". Fornisce un accesso astratto e ad alte prestazioni alla tabella dei libri,
 * integrando sia le operazioni CRUD di base sia il supporto per il caricamento ottimizzato dei dati.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Repository}</b>: Registra l'interfaccia come Bean gestito dal container di Spring.</li>
 *   <li><b>Gestione della Chiave Primaria Naturale</b>: Estende {@code CrudRepository<BookEntity, String>},
 *       dove la chiave primaria {@code ID} è mappata como {@code String} per accogliere il codice ISBN
 *       fornito direttamente dal client (invece di un ID numerico incrementale).</li>
 *   <li><b>Paginazione e Ordinamento Scalabili</b>: Estende l'interfaccia {@code PagingAndSortingRepository<BookEntity, String>}.
 *       Questo sblocca il metodo nativo {@code findAll(Pageable pageable)}, consentendo al database di
 *       restituire i dati in blocchi ridotti (pagine) per evitare problemi di saturazione della memoria.</li>
 *   <li><b>Integrazione con le Relazioni</b>: Lavora in sinergia con la relazione {@code @ManyToOne} verso
 *       l'autore, supportando le operazioni a cascata (Cascade) all'atto del salvataggio dei libri.</li>
 * </ul>
 *
 * <p><b>Integrazione con il Domain Model:</b><br>
 * Opera direttamente sull'entità {@code BookEntity}, traducendo le operazioni ad oggetti in istruzioni SQL
 * ed eseguendo l'unione dei dati relazionali con gli autori corrispondenti.</p>
 */
@Repository
public interface BookRepository extends CrudRepository<BookEntity, String>,
        PagingAndSortingRepository<BookEntity, String> {
}