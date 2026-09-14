# ====================================================================================
# CONFIGURAZIONE IMMAGINE APPLICATIVA: Dockerfile
# ====================================================================================
# Questo file descrive i passaggi per creare l'immagine Docker dell'applicazione.
# Utilizza un approccio a strati (layers) per pacchettizzare il file JAR compilato
# e configurare l'ambiente di runtime ottimizzato per Java 17.
# ====================================================================================

# 1. IMMAGINE DI PARTENZA (Base Image)
# Utilizza una distribuzione Linux Alpine minimale contenente l'OpenJDK 17.
# NOTA TECNICA:
# In ambienti di produzione maturi, si preferisce usare immagini JRE (es. eclipse-temurin:17-jre-alpine)
# al posto della JDK completa, perché riducono la dimensione dell'immagine finale e la superficie
# di attacco per la sicurezza, contenendo solo la JVM necessaria a eseguire il bytecode.
# Qui si mantiene la versione openjdk:17-jdk-alpine (DEPRECATA) per garantire la massima compatibilità
# con il tutorial di riferimento e con eventuali operazioni di debug che richiedano tool aggiuntivi.
#CONSIGLIO: FROM eclipse-temurin:17-jre-alpine
FROM openjdk:17-jdk-alpine

# 2. METADATI (Metadata)
# Specifica il creatore o il manutentore responsabile di questa immagine.
# NOTA TECNICA:
# Il comando MAINTAINER è stato ufficialmente deprecato da Docker in favore del label OCI.
# In un progetto moderno si utilizzerebbe: LABEL maintainer="devtiro.com"
# Lo si mantiene qui per coerenza con il materiale didattico originale, senza inficiare
# il funzionamento del build.
#CONSIGLIO: LABEL maintainer="devtiro.com"
MAINTAINER devtiro.com

# 3. TRASFERIMENTO DEGLI ARTEFATTI (Artifact Copy)
# Copia il file JAR generato dalla fase di build di Maven (nella cartella target)
# rinominandolo in 'app.jar' all'interno del file system isolato del container.
COPY target/*.jar app.jar

# 4. PUNTO DI INGRESSO (Entrypoint)
# Definisce il comando predefinito che verrà eseguito all'avvio del container.
# Esegue l'applicazione Spring Boot lanciando direttamente il file JAR copiato.
ENTRYPOINT ["java","-jar","/app.jar"]