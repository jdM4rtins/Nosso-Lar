package com.nossolaritapetininga.InstitutoNossoLar.service;

import com.nossolaritapetininga.InstitutoNossoLar.model.ExecucaoBackup;
import com.nossolaritapetininga.InstitutoNossoLar.repository.ExecucaoBackupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupService.class);
    private final ExecucaoBackupRepository repository;
    private final boolean enabled;
    private final Path directory;
    private final int retentionDays;
    private final String dumpCommand;

    public BackupService(
            ExecucaoBackupRepository repository,
            @Value("${app.backup.enabled:false}") boolean enabled,
            @Value("${app.backup.directory:./backups}") String directory,
            @Value("${app.backup.retention-days:30}") int retentionDays,
            @Value("${app.backup.pg-dump-command:pg_dump}") String dumpCommand) {
        this.repository = repository;
        this.enabled = enabled;
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        this.retentionDays = Math.max(1, retentionDays);
        this.dumpCommand = dumpCommand;
    }

    @Scheduled(cron = "${app.backup.schedule:0 0 3 * * *}")
    public void executarAgendado() {
        if (enabled) {
            executar();
        }
    }

    @Transactional
    public void executar() {
        ExecucaoBackup registro = new ExecucaoBackup();
        registro.setTipo("PG_DUMP");
        registro.setStatus("EM_EXECUCAO");
        registro.setDataInicio(LocalDateTime.now());
        registro = repository.save(registro);
        Path arquivo = directory.resolve("instituto_nosso_lar-" + registro.getDataInicio().toLocalDate() + ".dump");
        try {
            Files.createDirectories(directory);
            String dbUrl = System.getenv().getOrDefault("DB_URL", "");
            String database = dbUrl.startsWith("jdbc:postgresql:")
                    ? dbUrl.substring("jdbc:".length()) : dbUrl;
            List<String> argumentos = new java.util.ArrayList<>(List.of(
                    dumpCommand, "--format=custom", "--no-owner", "--file=" + arquivo));
            if (!database.isBlank()) {
                argumentos.add("--dbname=" + database);
            }
            ProcessBuilder processo = new ProcessBuilder(argumentos);
            adicionarVariavel(processo, "PGHOST", System.getenv("DB_HOST"));
            adicionarVariavel(processo, "PGPORT", System.getenv("DB_PORT"));
            adicionarVariavel(processo, "PGUSER", System.getenv("DB_USERNAME"));
            adicionarVariavel(processo, "PGPASSWORD", System.getenv("DB_PASSWORD"));
            processo.redirectErrorStream(true);
            Process processoIniciado = processo.start();
            String saida;
            try (InputStream stream = processoIniciado.getInputStream()) {
                saida = new String(stream.readAllBytes());
            }
            if (!processoIniciado.waitFor(30, TimeUnit.MINUTES) || processoIniciado.exitValue() != 0) {
                throw new IOException("pg_dump falhou: " + saida);
            }
            registro.setStatus("SUCESSO");
            registro.setLocalArquivo(arquivo.toString());
            registro.setTamanho(Files.size(arquivo));
            registro.setChecksum(checksum(arquivo));
            limparAntigos();
        } catch (Exception ex) {
            registro.setStatus("FALHA");
            registro.setMensagemErro(ex.getMessage());
            log.error("Falha no backup do banco", ex);
        } finally {
            registro.setDataFim(LocalDateTime.now());
            repository.save(registro);
        }
    }

    private void adicionarVariavel(ProcessBuilder processo, String nome, String valor) {
        if (valor != null && !valor.isBlank()) {
            processo.environment().put(nome, valor);
        }
    }

    private String checksum(Path arquivo) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream stream = Files.newInputStream(arquivo)) {
            byte[] buffer = new byte[8192];
            int lidos;
            while ((lidos = stream.read(buffer)) >= 0) {
                digest.update(buffer, 0, lidos);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private void limparAntigos() throws IOException {
        long limite = LocalDateTime.now().minusDays(retentionDays).toEpochSecond(ZoneOffset.UTC);
        try (var arquivos = Files.list(directory)) {
            arquivos.filter(path -> path.getFileName().toString().endsWith(".dump"))
                    .filter(path -> {
                        try { return Files.getLastModifiedTime(path).toInstant().getEpochSecond() < limite; }
                        catch (IOException ex) { return false; }
                    }).forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (IOException ex) { log.warn("Não foi possível remover backup antigo {}", path, ex); }
                    });
        }
    }
}
