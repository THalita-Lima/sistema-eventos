import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String nome;
    private String endereco;
    private String cidade;
    private String categoria;
    private LocalDateTime dataHora;
    private String descricao;
    private List<String> participantes;

    public Evento(int id, String nome, String endereco, String cidade, String categoria, LocalDateTime dataHora, String descricao) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.categoria = categoria;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.participantes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getParticipantes() {
        return participantes;
    }

    public void adicionarParticipante(String participante) {
        if (!participantes.contains(participante)) {
            participantes.add(participante);
        }
    }

    public void removerParticipante(String participante) {
        participantes.remove(participante);
    }

    public boolean estaConfirmado(String participante) {
        return participantes.contains(participante);
    }

    public boolean jaOcorreu() {
        return LocalDateTime.now().isAfter(dataHora);
    }

    public String serialize() {
        String participantesStr = String.join(";", participantes);
        return String.format("%d|%s|%s|%s|%s|%s|%s|%s", id, escape(nome), escape(endereco), escape(cidade), escape(categoria), dataHora.format(FORMATTER), escape(descricao), escape(participantesStr));
    }

    public static Evento deserialize(String linha) {
        try {
            String[] campos = linha.split("\\|", -1);
            if (campos.length < 8) {
                return null;
            }
            int id = Integer.parseInt(campos[0]);
            String nome = unescape(campos[1]);
            String endereco = unescape(campos[2]);
            String cidade = unescape(campos[3]);
            String categoria = unescape(campos[4]);
            LocalDateTime dataHora = LocalDateTime.parse(campos[5], FORMATTER);
            String descricao = unescape(campos[6]);
            Evento evento = new Evento(id, nome, endereco, cidade, categoria, dataHora, descricao);
            String part = unescape(campos[7]);
            if (!part.isEmpty()) {
                for (String p : part.split(";")) {
                    if (!p.isBlank()) {
                        evento.adicionarParticipante(p);
                    }
                }
            }
            return evento;
        } catch (Exception e) {
            return null;
        }
    }

    private static String escape(String s) {
        return s.replace("|", "\\|").replace(";", "\\;");
    }

    private static String unescape(String s) {
        return s.replace("\\|", "|").replace("\\;", ";");
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (\n  Local: %s, %s | Categoria: %s | Data/Hora: %s\n  Descrição: %s\n  Participantes: %s\n)", id, nome, endereco, cidade, categoria, dataHora.format(FORMATTER), descricao, participantes.isEmpty() ? "Nenhum" : String.join(", ", participantes));
    }
}
