package com.devtiro.database.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h2>ENTITÀ DI PERSISTENZA: {@link BookEntity}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe rappresenta l'entità dello <b>Strato di Persistenza (Persistence Layer)</b>
 * per la risorsa "Book". Configura la mappatura relazionale della tabella {@code books} e
 * gestisce l'associazione diretta con l'entità autore correlata.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>Gestione della Chiave Primaria Naturale</b>: Utilizza il codice ISBN come chiave primaria ({@code @Id}).
 *       A differenza della risorsa autore, l'ISBN è una stringa fornita manualmente dal client e non viene
 *       autogenerata dal sistema.</li>
 *   <li><b>Associazione Relazionale (Many-to-One)</b>: Definisce una relazione {@code @ManyToOne} verso
 *       {@link AuthorEntity}. Un autore può scrivere molti libri, ma ogni libro è associato a un solo
 *       autore all'interno della tabella.</li>
 *   <li><b>Salvataggio a Cascata (Cascade Type ALL)</b>: Configura il parametro {@code cascade = CascadeType.ALL}
 *       sulla relazione dell'autore. Questa impostazione cruciale assicura che qualsiasi operazione di scrittura,
 *       modifica o cancellazione eseguita su un libro si propaghi automaticamente sull'entità autore associata
 *       (es. creando l'autore a database se non esiste ancora).</li>
 *   <li><b>Chiave Esterna (Join Column)</b>: Specifica {@code @JoinColumn(name = "author_id")} per mappare
 *       la colonna della chiave esterna che punta alla chiave primaria della tabella autori.</li>
 * </ul>
 *
 * <p><b>Proprietà Mappate:</b></p>
 * <ul>
 *   <li>{@code isbn} (String): Chiave primaria non autogenerata (codice ISBN univoco del libro).</li>
 *   <li>{@code title} (String): Titolo del libro.</li>
 *   <li>{@code authorEntity} (AuthorEntity): Riferimento all'entità autore associata tramite foreign key.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="books")
public class BookEntity {

    @Id
    private String isbn;

    private String title;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private AuthorEntity authorEntity;

}