package biblioteca;

import biblioteca.exception.EntidadeNaoEncontradaException;
import biblioteca.exception.RegraNegocioException;

public class ControladorBiblioteca {

    private final SistemaBiblioteca sistema;

    public ControladorBiblioteca() {
        this.sistema = new SistemaBiblioteca();
    }

    public void carregarDadosIniciais() throws RegraNegocioException {
        sistema.carregarDadosIniciais();
    }

    public void cadastrarLivro(String codigo, String titulo, int ano, String autor) throws RegraNegocioException {
        sistema.cadastrarLivro(codigo, titulo, ano, autor);
    }

    public void cadastrarRevista(String codigo, String titulo, int ano, int edicao) throws RegraNegocioException {
        sistema.cadastrarRevista(codigo, titulo, ano, edicao);
    }

    public void cadastrarMembro(String matricula, String nome) throws RegraNegocioException {
        sistema.cadastrarMembro(matricula, nome);
    }

    public String listarAcervo() {
        return sistema.listarAcervo();
    }

    public String listarMembros() {
        return sistema.listarMembros();
    }

    public String listarEmprestimosAtivos() {
        return sistema.listarEmprestimosAtivos();
    }

    public String consultarItem(String codigo) throws EntidadeNaoEncontradaException {
        return sistema.consultarItem(codigo);
    }

    public String alterarTituloItem(String codigo, String novoTitulo)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        return sistema.alterarTituloItem(codigo, novoTitulo);
    }

    public String alterarNomeMembro(String matricula, String novoNome)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        return sistema.alterarNomeMembro(matricula, novoNome);
    }

    public String realizarEmprestimo(String codigoItem, String matricula)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        return sistema.realizarEmprestimo(codigoItem, matricula);
    }

    public String registrarDevolucao(String codigoItem, int diasAtraso)
            throws EntidadeNaoEncontradaException, RegraNegocioException {
        return sistema.registrarDevolucao(codigoItem, diasAtraso);
    }
}
