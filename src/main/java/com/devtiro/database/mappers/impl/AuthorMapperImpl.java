package com.devtiro.database.mappers.impl;

import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * <h2>IMPLEMENTAZIONE MAPPER: {@link AuthorMapperImpl}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe implementa l'interfaccia generica {@code Mapper} per la risorsa "Author".
 * Si colloca nello <b>Strato di Mappatura (Mapping Layer)</b> dell'applicazione ed ha il compito
 * esclusivo di convertire in modo bidirezionale le entità di persistenza ({@code AuthorEntity})
 * nei corrispondenti oggetti di trasferimento dati ({@code AuthorDto}) e viceversa.</p>
 *
 * <p><b>Dettagli Tecnici e Scelte di Design:</b></p>
 * <ul>
 *   <li><b>{@code @Component}</b>: Registra questa classe come Bean di Spring gestito dal container,
 *       rendendola disponibile per l'autocablaggio (Autowiring) all'interno dei controller
 *       o dei servizi.</li>
 *   <li><b>Constructor Injection</b>: Utilizza l'iniezione tramite costruttore per integrare in
 *       modo sicuro l'istanza configurata di {@link org.modelmapper.ModelMapper}.</li>
 *   <li><b>Decoppiamento Rigido</b>: Isola lo strato di presentazione (Controller) dalle entità
 *       JPA del database, garantendo che le modifiche ai dettagli di persistenza non si ripercuotano
 *       sull'interfaccia REST pubblica dell'applicazione.</li>
 * </ul>
 *
 * <p><b>Metodi Implementati:</b></p>
 * <ul>
 *   <li><b>{@code mapTo(AuthorEntity)}</b>: Converte un'entità database {@code AuthorEntity} in un
 *       {@code AuthorDto} pronto per essere serializzato in JSON e restituito al client.</li>
 *   <li><b>{@code mapFrom(AuthorDto)}</b>: Converte un DTO in ingresso {@code AuthorDto} in un'entità
 *       {@code AuthorEntity} pronta per essere elaborata ed eventualmente persistita dal servizio.</li>
 * </ul>
 */
@Component
public class AuthorMapperImpl implements Mapper<AuthorEntity, AuthorDto> {

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
    public AuthorMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Converte un'entità autore nel relativo DTO di presentazione.
     *
     * @param authorEntity l'entità di persistenza sorgente
     * @return il DTO mappato contenente i dati dell'autore
     */
    @Override
    public AuthorDto mapTo(AuthorEntity authorEntity) {
        return modelMapper.map(authorEntity, AuthorDto.class);
    }

    /**
     * Converte un DTO di presentazione nell'entità di persistenza corrispondente.
     *
     * @param authorDto il DTO sorgente in ingresso
     * @return l'entità JPA configurata e pronta per lo strato di business
     */
    @Override
    public AuthorEntity mapFrom(AuthorDto authorDto) {
        return modelMapper.map(authorDto, AuthorEntity.class);
    }
}