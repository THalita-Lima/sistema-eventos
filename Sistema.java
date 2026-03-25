import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Sistema {
    private static final String EVENT_FILE = "events.data";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final List<String> CATEGORIAS = List.of("Festa", "Esporte", "Show", "Curso", "Teatro", "Outros");

    private List<Evento> eventos = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioAtual;
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Sistema sistema = new Sistema();
        sistema.carregarEventos();
        sistema.exibirMenu();
    }

    private void exibirMenu() {
        System.out.println("--- SISTEMA DE EVENTOS ---");
        selecionarUsuario();
        while (true) {
            System.out.println("\n1. Cadastrar evento");
            System.out.println("2. Listar eventos");
            System.out.println("3. Consultar eventos na minha cidade");
            System.out.println("4. Confirmar participação em evento");
            System.out.println("5. Cancelar participação em evento");
            System.out.println("6. Visualizar minhas participações");
            System.out.println("7. Eventos ocorridos");
            System.out.println("8. Próximo evento");
            System.out.println("9. Salvar e sair");
            System.out.print("Escolha: ");
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1": cadastrarEvento(); break;
                case "2": listarEventos(); break;
                case "3": consultarEventosCidade(); break;
                case "4": confirmarParticipacao(); break;
                case "5": cancelarParticipacao(); break;
                case "6": visualizarMinhasParticipacoes(); break;
                case "7": eventosOcorridos(); break;
                case "8": proximoEvento(); break;
                case "9": salvarEventos(); System.out.println("Saindo..."); return;
                default: System.out.println("Opção inválida");
            }
        }
    }

    private void selecionarUsuario() {
        System.out.println("Informe dados do usuário para inicio:");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine().trim();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine().trim();

        usuarioAtual = new Usuario(nome, endereco, cidade, email);
        usuarios.add(usuarioAtual);
        System.out.println("Usuário registrado: " + usuarioAtual);
    }

    private void cadastrarEvento() {
        System.out.print("Nome do evento: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Endereço do evento: ");
        String endereco = scanner.nextLine().trim();
        System.out.print("Cidade do evento: ");
        String cidade = scanner.nextLine().trim();

        System.out.println("Categorias: ");
        for (int i = 0; i < CATEGORIAS.size(); i++) {
            System.out.printf("%d - %s\n", i + 1, CATEGORIAS.get(i));
        }
        System.out.print("Escolha categoria (número): ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        String categoria = CATEGORIAS.get(Math.max(0, Math.min(CATEGORIAS.size() - 1, idx - 1)));

        System.out.print("Data/hora (dd/MM/yyyy HH:mm): ");
        LocalDateTime dataHora;
        while (true) {
            try {
                dataHora = LocalDateTime.parse(scanner.nextLine().trim(), FORMATTER);
                break;
            } catch (Exception e) {
                System.out.print("Formato inválido. Informe dd/MM/yyyy HH:mm: ");
            }
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine().trim();

        int id = eventos.stream().mapToInt(Evento::getId).max().orElse(0) + 1;
        Evento evento = new Evento(id, nome, endereco, cidade, categoria, dataHora, descricao);
        eventos.add(evento);

        System.out.println("Evento cadastrado com sucesso: " + id);
    }

    private void listarEventos() {
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return;
        }
        List<Evento> ordenados = eventos.stream()
                .sorted(Comparator.comparing(Evento::getDataHora))
                .collect(Collectors.toList());

        ordenados.forEach(System.out::println);
    }

    private void consultarEventosCidade() {
        List<Evento> emCidade = eventos.stream()
                .filter(e -> e.getCidade().equalsIgnoreCase(usuarioAtual.getCidade()))
                .sorted(Comparator.comparing(Evento::getDataHora))
                .collect(Collectors.toList());
        if (emCidade.isEmpty()) {
            System.out.println("Nenhum evento na sua cidade (" + usuarioAtual.getCidade() + ")");
            return;
        }
        emCidade.forEach(System.out::println);
    }

    private Evento escolherEvento(String acao) {
        listarEventos();
        System.out.print("Digite o ID do evento para " + acao + ": ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            return eventos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
        } catch (Exception e) {
            System.out.println("ID inválido");
            return null;
        }
    }

    private void confirmarParticipacao() {
        Evento e = escolherEvento("confirmar participação");
        if (e == null) return;
        if (e.jaOcorreu()) {
            System.out.println("Evento já ocorreu e não é mais possível confirmar participação.");
            return;
        }
        if (e.estaConfirmado(usuarioAtual.getEmail())) {
            System.out.println("Você já confirmou participação nesse evento.");
            return;
        }
        e.adicionarParticipante(usuarioAtual.getEmail());
        System.out.println("Participação confirmada em: " + e.getNome());
    }

    private void cancelarParticipacao() {
        Evento e = escolherEvento("cancelar participação");
        if (e == null) return;
        if (!e.estaConfirmado(usuarioAtual.getEmail())) {
            System.out.println("Você não está inscrito nesse evento.");
            return;
        }
        e.removerParticipante(usuarioAtual.getEmail());
        System.out.println("Participação cancelada em: " + e.getNome());
    }

    private void visualizarMinhasParticipacoes() {
        List<Evento> minhas = eventos.stream()
                .filter(e -> e.estaConfirmado(usuarioAtual.getEmail()))
                .sorted(Comparator.comparing(Evento::getDataHora))
                .collect(Collectors.toList());
        if (minhas.isEmpty()) {
            System.out.println("Você não está participando de nenhum evento.");
            return;
        }
        minhas.forEach(System.out::println);
    }

    private void eventosOcorridos() {
        List<Evento> ocorridos = eventos.stream()
                .filter(Evento::jaOcorreu)
                .sorted(Comparator.comparing(Evento::getDataHora))
                .collect(Collectors.toList());
        if (ocorridos.isEmpty()) {
            System.out.println("Nenhum evento ocorrido ainda.");
            return;
        }
        ocorridos.forEach(System.out::println);
    }

    private void proximoEvento() {
        eventos.stream()
                .filter(e -> !e.jaOcorreu())
                .min(Comparator.comparing(Evento::getDataHora))
                .ifPresentOrElse(
                        e -> System.out.println("Próximo evento: " + e),
                        () -> System.out.println("Não há próximos eventos cadastrados."));
    }

    private void carregarEventos() {
        File file = new File(EVENT_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                Evento e = Evento.deserialize(linha);
                if (e != null) {
                    eventos.add(e);
                }
            }
            System.out.println("Eventos carregados: " + eventos.size());
        } catch (IOException ex) {
            System.out.println("Falha ao carregar eventos: " + ex.getMessage());
        }
    }

    private void salvarEventos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EVENT_FILE))) {
            for (Evento e : eventos) {
                bw.write(e.serialize());
                bw.newLine();
            }
            System.out.println("Eventos salvos em " + EVENT_FILE);
        } catch (IOException ex) {
            System.out.println("Falha ao salvar eventos: " + ex.getMessage());
        }
    }
}
