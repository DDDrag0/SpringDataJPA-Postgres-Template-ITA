package com.devtiro.database.mappers.impl;

import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * <h2>IMPLEMENTAZIONE MAPPER: {@link BookMapperImpl}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe implementa l'interfaccia generica {@code Mapper} per la risorsa "Book".
 * Traduce in modo bidirezionale i libri tra lo strato di presentazione ({@code BookDto}) e lo
 * strato di persistenza ({@code BookEntity}), gestendo in modo trasparente anche le relazioni
 * nidificate complesse.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Component}</b>: Configura la classe come componente di Spring per abilitare
 *       l'iniezione automatica delle dipendenze.</li>
 *   <li><b>Mappatura di Oggetti Nidificati</b>: Grazie alla strategia di matching <i>LOOSE</i>
 *       configurata su {@code ModelMapper}, questa classe è in grado di mappare non solo i campi piatti
 *       del libro, ma anche l'oggetto {@code AuthorDto} nidificato nel corrispondente {@code AuthorEntity}
 *       (e viceversa) in un unico passaggio atomico.</li>
 *   <li><b>Integrazione con il Cascade</b>: La corretta mappatura dell'autore nidificato effettuata da
 *       questa classe è il prerequisito fondamentale che permette a Spring Data JPA di salvare l'autore
 *       a cascata sul database alla creazione del libro.</li>
 * </ul>
 *
 * <p><b>Metodi Implementati:</b></p>
 * <ul>
 *   <li><b>{@code mapTo(BookEntity)}</b>: Converte una {@code BookEntity} (completa di eventuale
 *       relazione autore) in un {@code BookDto} strutturato.</li>
 *   <li><b>{@code mapFrom(BookDto)}</b>: Converte un {@code BookDto} in una {@code BookEntity}
 *       pronta per l'inserimento o l'aggiornamento a database.</li>
 * </ul>
 */
@Component
public class BookMapperImpl implements Mapper<BookEntity, BookDto> {

    /*
     * NOTA DI REFACTORING rispetto al tutorial didattico:
     * È stata aggiunta la parola chiave 'final' per garantire l'immutabilità
     * della dipendenza cablata tramite costruttore, seguendo le best practice di Spring.
     */
    private final ModelMapper modelMapper;

    /**
     * Costruttore unico per la Dependency Injection.
     *
     * @param modelMapper l'istanza centralizzata di ModelMapper
     */
    public BookMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Converte un'entità libro nel relativo DTO di presentazione.
     *
     * @param bookEntity l'entità di persistenza sorgente
     * @return il DTO mappato contenente i dati del libro e dell'autore nidificato
     */
    @Override
    public BookDto mapTo(BookEntity bookEntity) {
        return modelMapper.map(bookEntity, BookDto.class);
    }

    /**
     * Converte un DTO di presentazione del libro nell'entità di persistenza corrispondente.
     *
     * @param bookDto il DTO sorgente in ingresso
     * @return l'entità JPA configurata e pronta per lo strato di business
     */
    @Override
    public BookEntity mapFrom(BookDto bookDto) {
        return modelMapper.map(bookDto, BookEntity.class);
    }
}