package com.devtiro.database.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h2>CONFIGURAZIONE DI SISTEMA: {@link MapperConfig}</h2>
 *
 * <p><b>Ruolo nell'Architettura:</b><br>
 * Questa classe gestisce la configurazione e la registrazione centralizzata di
 * {@link org.modelmapper.ModelMapper} all'interno del contesto di Spring.
 * Funge da pilastro per il Presentation e il Mapping Layer, automatizzando la conversione
 * bidirezionale tra gli oggetti di trasferimento dati (DTO) e le entità JPA.
 * Decoppia lo strato dei controller dalle logiche di persistenza del database.</p>
 *
 * <p><b>Analisi delle Scelte di Design:</b>
 * <ul>
 *   <li><b>{@code @Configuration}</b>: Segnala a Spring Boot che questa classe contiene
 *       definizioni di Bean di sistema da elaborare all'avvio dell'applicazione.</li>
 *   <li><b>{@code @Bean}</b>: Registra l'istanza restituita nel pool dei componenti di Spring
 *       (Application Context), rendendola disponibile per la Dependency Injection.</li>
 *   <li><b>{@code MatchingStrategies.LOOSE}</b>: Modifica la strategia di matching predefinita di
 *       ModelMapper. Per impostazione predefinita, la conversione fallisce nell'associare
 *       oggetti complessi e nidificati profondi (come l'autore nidificato all'interno di
 *       un libro), impostandoli a {@code null}. L'opzione <i>LOOSE</i> permette di
 *       scendere nella gerarchia dei DTO e di mapparli integralmente nelle rispettive entità.</li>
 * </ul>
 * </p>
 *
 * <p><b>Integrazione con lo Strato di Persistenza:</b><br>
 * Grazie alla risoluzione dei grafi di oggetti complessi abilitata da questa strategia,
 * Spring Data JPA può catturare l'entità autore nidificata all'interno del libro ed eseguire
 * il salvataggio a cascata (Cascade) direttamente sul database relazionale.</p>
 */
@Configuration
public class MapperConfig {

    /**
     * Crea e configura il bean centralizzato di ModelMapper.
     *
     * @return un'istanza configurata di {@link ModelMapper} con strategia LOOSE
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Configura la strategia di matching su LOOSE per supportare gli oggetti nidificati
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}