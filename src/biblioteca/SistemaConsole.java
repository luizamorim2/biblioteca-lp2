package biblioteca;

import biblioteca.domain.TipoMembro;

import java.util.Scanner;

public class SistemaConsole {

    private final Scanner scanner;
    private final ControladorBiblioteca controlador;

    public SistemaConsole() {
        this.scanner = new Scanner(System.in);
        this.controlador = new ControladorBiblioteca();
    }

    public void iniciar() {
        controlador.carregarDadosIniciais();
        System.out.println("Dados de exemplo carregados.\n");

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Opcao: ");
            executarOpcao(opcao);
            System.out.println();
        } while (opcao != 0);

        scanner.close();
    }

    private void exibirMenu() {
        System.out.println("======================================");
        System.out.println("        BIBLIOTECA UNIVERSITARIA");
        System.out.println("======================================");
        System.out.println("1  - Cadastrar livro");
        System.out.println("2  - Cadastrar revista");
        System.out.println("3  - Cadastrar membro");
        System.out.println("4  - Listar acervo");
        System.out.println("5  - Listar membros");
        System.out.println("6  - Consultar item por codigo");
        System.out.println("7  - Alterar titulo de um item");
        System.out.println("8  - Alterar tipo de um membro");
        System.out.println("9  - Realizar emprestimo");
        System.out.println("10 - Registrar devolucao");
        System.out.println("0  - Sair");
        System.out.println();
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                cadastrarLivro();
                break;
            case 2:
                cadastrarRevista();
                break;
            case 3:
                cadastrarMembro();
                break;
            case 4:
                System.out.println(controlador.listarAcervo());
                break;
            case 5:
                System.out.println(controlador.listarMembros());
                break;
            case 6:
                consultarItem();
                break;
            case 7:
                alterarTituloItem();
                break;
            case 8:
                alterarTipoMembro();
                break;
            case 9:
                realizarEmprestimo();
                break;
            case 10:
                registrarDevolucao();
                break;
            case 0:
                System.out.println("Sistema encerrado.");
                break;
            default:
                System.out.println("Opcao invalida.");
        }
    }

    private void cadastrarLivro() {
        String codigo = lerTexto("Codigo: ");
        String titulo = lerTexto("Titulo: ");
        String autor = lerTexto("Autor: ");

        boolean sucesso = controlador.cadastrarLivro(codigo, titulo, autor);

        if (sucesso) {
            System.out.println("Livro cadastrado com sucesso.");
        } else {
            System.out.println(controlador.getUltimoErro());
        }
    }

    private void cadastrarRevista() {
        String codigo = lerTexto("Codigo: ");
        String titulo = lerTexto("Titulo: ");
        int edicao = lerInteiro("Numero da edicao: ");

        boolean sucesso = controlador.cadastrarRevista(codigo, titulo, edicao);

        if (sucesso) {
            System.out.println("Revista cadastrada com sucesso.");
        } else {
            System.out.println(controlador.getUltimoErro());
        }
    }

    private void cadastrarMembro() {
        String matricula = lerTexto("Matricula: ");
        String nome = lerTexto("Nome: ");
        TipoMembro tipo = lerTipoMembro();

        if (tipo == null) {
            return;
        }

        boolean sucesso = controlador.cadastrarMembro(matricula, nome, tipo);

        if (sucesso) {
            System.out.println("Membro cadastrado com sucesso.");
        } else {
            System.out.println(controlador.getUltimoErro());
        }
    }

    private void consultarItem() {
        String codigo = lerTexto("Codigo do item: ");
        System.out.println(controlador.consultarItem(codigo));
    }

    private void alterarTituloItem() {
        String codigo = lerTexto("Codigo do item: ");
        String novoTitulo = lerTexto("Novo titulo: ");

        boolean sucesso = controlador.alterarTituloItem(codigo, novoTitulo);

        if (sucesso) {
            System.out.println("Titulo alterado com sucesso.");
        } else {
            System.out.println(controlador.getUltimoErro());
        }
    }

    private void alterarTipoMembro() {
        String matricula = lerTexto("Matricula do membro: ");
        TipoMembro tipo = lerTipoMembro();

        if (tipo == null) {
            return;
        }

        boolean sucesso = controlador.alterarTipoMembro(matricula, tipo);

        if (sucesso) {
            System.out.println("Tipo alterado com sucesso.");
        } else {
            System.out.println(controlador.getUltimoErro());
        }
    }

    private void realizarEmprestimo() {
        String codigo = lerTexto("Codigo do item: ");
        String matricula = lerTexto("Matricula do membro: ");

        System.out.println(controlador.realizarEmprestimo(codigo, matricula));
    }

    private void registrarDevolucao() {
        String codigo = lerTexto("Codigo do item: ");
        int diasAtraso = lerInteiro("Dias de atraso (0 se estiver no prazo): ");

        System.out.println(controlador.registrarDevolucao(codigo, diasAtraso));
    }

    private TipoMembro lerTipoMembro() {
        TipoMembro[] tipos = TipoMembro.values();
        System.out.println("TIPOS DISPONIVEIS");
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + " - " + tipos[i]);
        }

        int escolha = lerInteiro("Tipo: ");

        if (escolha < 1 || escolha > tipos.length) {
            System.out.println("Tipo invalido.");
            return null;
        }
        return tipos[escolha - 1];
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine();

            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero inteiro valido.");
            }
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine();
    }
}
