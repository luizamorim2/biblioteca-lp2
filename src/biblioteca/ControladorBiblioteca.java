package biblioteca;

import biblioteca.domain.ItemAcervo;
import biblioteca.domain.Membro;
import biblioteca.domain.TipoMembro;

public class ControladorBiblioteca {

    private final SistemaBiblioteca sistema;

    public ControladorBiblioteca() {
        this.sistema = new SistemaBiblioteca();
    }

    public void carregarDadosIniciais() {
        sistema.carregarDadosIniciais();
    }

    public boolean cadastrarLivro(String codigo, String titulo, String autor) {
        return sistema.cadastrarLivro(codigo, titulo, autor);
    }

    public boolean cadastrarRevista(String codigo, String titulo, int edicao) {
        return sistema.cadastrarRevista(codigo, titulo, edicao);
    }

    public boolean cadastrarMembro(String matricula, String nome, TipoMembro tipo) {
        return sistema.cadastrarMembro(matricula, nome, tipo);
    }

    public ItemAcervo buscarItem(String codigo) {
        return sistema.buscarItem(codigo);
    }

    public Membro buscarMembro(String matricula) {
        return sistema.buscarMembro(matricula);
    }

    public int contarItens() {
        return sistema.contarItens();
    }

    public String consultarItem(String codigo) {
        return sistema.consultarItem(codigo);
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

    public boolean alterarTituloItem(String codigo, String novoTitulo) {
        return sistema.alterarTituloItem(codigo, novoTitulo);
    }

    public boolean alterarTipoMembro(String matricula, TipoMembro novoTipo) {
        return sistema.alterarTipoMembro(matricula, novoTipo);
    }

    public String realizarEmprestimo(String codigoItem, String matricula) {
        return sistema.realizarEmprestimo(codigoItem, matricula);
    }

    public String registrarDevolucao(String codigoItem, int diasAtraso) {
        return sistema.registrarDevolucao(codigoItem, diasAtraso);
    }

    public String getUltimoErro() {
        return sistema.getUltimoErro();
    }
}
