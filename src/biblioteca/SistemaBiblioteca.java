package biblioteca;

import biblioteca.domain.Emprestimo;
import biblioteca.domain.ItemAcervo;
import biblioteca.domain.Livro;
import biblioteca.domain.Membro;
import biblioteca.domain.Multavel;
import biblioteca.domain.Revista;
import biblioteca.domain.TipoMembro;
import biblioteca.exception.EntidadeNaoEncontradaException;
import biblioteca.exception.RegraNegocioException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaBiblioteca {

    private final Map<String, ItemAcervo> acervo;
    private final Map<String, Membro> membros;
    private final List<Emprestimo> emprestimos;

    public SistemaBiblioteca() {
        this.acervo = new HashMap<>();
        this.membros = new HashMap<>();
        this.emprestimos = new ArrayList<>();
    }

    public void cadastrarLivro(String codigo, String titulo, String autor) throws RegraNegocioException {
        adicionarItem(new Livro(codigo, titulo, autor));
    }

    public void cadastrarRevista(String codigo, String titulo, int edicao) throws RegraNegocioException {
        adicionarItem(new Revista(codigo, titulo, edicao));
    }

    private void adicionarItem(ItemAcervo item) throws RegraNegocioException {
        if (acervo.containsKey(item.getCodigo())) {
            throw new RegraNegocioException("Ja existe um item com o codigo " + item.getCodigo() + ".");
        }
        acervo.put(item.getCodigo(), item);
    }

    public void cadastrarMembro(String matricula, String nome, TipoMembro tipo) throws RegraNegocioException {
        Membro membro = new Membro(matricula, nome, tipo);
        if (membros.containsKey(membro.getMatricula())) {
            throw new RegraNegocioException("Ja existe um membro com a matricula " + membro.getMatricula() + ".");
        }
        membros.put(membro.getMatricula(), membro);
    }

    private ItemAcervo buscarItem(String codigo) throws EntidadeNaoEncontradaException {
        ItemAcervo item = acervo.get(normalizar(codigo));
        if (item == null) {
            throw new EntidadeNaoEncontradaException("Item nao encontrado para o codigo " + normalizar(codigo) + ".");
        }
        return item;
    }

    private Membro buscarMembro(String matricula) throws EntidadeNaoEncontradaException {
        Membro membro = membros.get(normalizar(matricula));
        if (membro == null) {
            throw new EntidadeNaoEncontradaException("Membro nao encontrado para a matricula " + normalizar(matricula) + ".");
        }
        return membro;
    }

    private Emprestimo buscarEmprestimoAtivo(String codigoItem) throws EntidadeNaoEncontradaException {
        String codigo = normalizar(codigoItem);
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo() && emprestimo.getItem().getCodigo().equals(codigo)) {
                return emprestimo;
            }
        }
        throw new EntidadeNaoEncontradaException("Nao existe emprestimo ativo para o item " + codigo + ".");
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().toUpperCase();
    }

    public String consultarItem(String codigo) throws EntidadeNaoEncontradaException {
        return buscarItem(codigo).exibirInformacoes();
    }

    public String listarAcervo() {
        if (acervo.isEmpty()) {
            return "Nenhum item cadastrado no acervo.";
        }
        StringBuilder sb = new StringBuilder();
        for (ItemAcervo item : acervo.values()) {
            sb.append(item.exibirInformacoes()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public String listarMembros() {
        if (membros.isEmpty()) {
            return "Nenhum membro cadastrado.";
        }
        StringBuilder sb = new StringBuilder();
        for (Membro membro : membros.values()) {
            sb.append(membro.exibirInformacoes()).append("\n\n");
        }
        return sb.toString().trim();
    }

    public String listarEmprestimosAtivos() {
        StringBuilder sb = new StringBuilder();
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo()) {
                sb.append(emprestimo.exibirInformacoes()).append("\n\n");
            }
        }
        if (sb.length() == 0) {
            return "Nenhum emprestimo ativo no momento.";
        }
        return sb.toString().trim();
    }

    public String alterarTituloItem(String codigo, String novoTitulo)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        ItemAcervo item = buscarItem(codigo);
        item.setTitulo(novoTitulo);
        return "Titulo atualizado: " + item.exibirResumo();
    }

    public String alterarTipoMembro(String matricula, TipoMembro novoTipo)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        Membro membro = buscarMembro(matricula);
        membro.setTipo(novoTipo);
        return membro.getNome() + " agora e do tipo " + membro.getTipo().name() + ".";
    }

    public String realizarEmprestimo(String codigoItem, String matricula)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        ItemAcervo item = buscarItem(codigoItem);
        Membro membro = buscarMembro(matricula);

        item.emprestar();
        try {
            membro.registrarEmprestimo();
        } catch (RegraNegocioException e) {
            item.devolver();
            throw e;
        }

        emprestimos.add(new Emprestimo(item, membro));

        return "Emprestimo registrado para " + membro.getNome()
                + ".\nItem: " + item.exibirResumo()
                + "\nPrazo: " + item.getPrazoDias() + " dias";
    }

    public String registrarDevolucao(String codigoItem, int diasAtraso)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        Emprestimo emprestimo = buscarEmprestimoAtivo(codigoItem);

        double multa = emprestimo.registrarDevolucao(diasAtraso);
        emprestimo.getItem().devolver();
        emprestimo.getMembro().registrarDevolucao();

        return "Devolucao registrada: " + emprestimo.getItem().exibirResumo()
                + "\nDias de atraso: " + diasAtraso
                + "\nMulta: " + Multavel.formatarValor(multa)
                + "\nPolitica aplicada: " + emprestimo.getItem().descreverPoliticaMulta();
    }

    public void carregarDadosIniciais() throws RegraNegocioException {
        cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        cadastrarRevista("R01", "Superinteressante", 456);
        cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);
    }
}
