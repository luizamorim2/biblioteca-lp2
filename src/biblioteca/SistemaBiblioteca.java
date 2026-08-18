package biblioteca;

import biblioteca.domain.Emprestimo;
import biblioteca.domain.ItemAcervo;
import biblioteca.domain.Livro;
import biblioteca.domain.Membro;
import biblioteca.domain.Multavel;
import biblioteca.domain.Revista;
import biblioteca.domain.TipoMembro;
import biblioteca.exception.RegraNegocioException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaBiblioteca {

    private final Map<String, ItemAcervo> acervo;
    private final Map<String, Membro> membros;
    private final List<Emprestimo> emprestimos;
    private String ultimoErro;

    public SistemaBiblioteca() {
        this.acervo = new HashMap<>();
        this.membros = new HashMap<>();
        this.emprestimos = new ArrayList<>();
        this.ultimoErro = "";
    }

    public boolean cadastrarLivro(String codigo, String titulo, String autor) {
        try {
            return adicionarItem(new Livro(codigo, titulo, autor));
        } catch (RegraNegocioException e) {
            ultimoErro = e.getMessage();
            return false;
        }
    }

    public boolean cadastrarRevista(String codigo, String titulo, int edicao) {
        try {
            return adicionarItem(new Revista(codigo, titulo, edicao));
        } catch (RegraNegocioException e) {
            ultimoErro = e.getMessage();
            return false;
        }
    }

    private boolean adicionarItem(ItemAcervo item) throws RegraNegocioException {
        if (acervo.containsKey(item.getCodigo())) {
            throw new RegraNegocioException("Ja existe um item com o codigo " + item.getCodigo() + ".");
        }
        acervo.put(item.getCodigo(), item);
        return true;
    }

    public boolean cadastrarMembro(String matricula, String nome, TipoMembro tipo) {
        try {
            Membro membro = new Membro(matricula, nome, tipo);
            if (membros.containsKey(membro.getMatricula())) {
                throw new RegraNegocioException("Ja existe um membro com a matricula " + membro.getMatricula() + ".");
            }
            membros.put(membro.getMatricula(), membro);
            return true;
        } catch (RegraNegocioException e) {
            ultimoErro = e.getMessage();
            return false;
        }
    }

    public ItemAcervo buscarItem(String codigo) {
        return acervo.get(normalizar(codigo));
    }

    public Membro buscarMembro(String matricula) {
        return membros.get(normalizar(matricula));
    }

    private Emprestimo buscarEmprestimoAtivo(String codigoItem) {
        String codigo = normalizar(codigoItem);
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.estaAtivo() && emprestimo.getItem().getCodigo().equals(codigo)) {
                return emprestimo;
            }
        }
        return null;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.trim().toUpperCase();
    }

    public int contarItens() {
        return acervo.size();
    }

    public String consultarItem(String codigo) {
        ItemAcervo item = buscarItem(codigo);
        if (item == null) {
            return "Item nao encontrado.";
        }
        return item.exibirInformacoes();
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

    public boolean alterarTituloItem(String codigo, String novoTitulo) {
        ItemAcervo item = buscarItem(codigo);
        if (item == null) {
            ultimoErro = "Item nao encontrado.";
            return false;
        }
        try {
            item.setTitulo(novoTitulo);
            return true;
        } catch (RegraNegocioException e) {
            ultimoErro = e.getMessage();
            return false;
        }
    }

    public String realizarEmprestimo(String codigoItem, String matricula) {
        ItemAcervo item = buscarItem(codigoItem);
        if (item == null) {
            return "Item nao encontrado.";
        }

        Membro membro = buscarMembro(matricula);
        if (membro == null) {
            return "Membro nao encontrado.";
        }

        try {
            membro.registrarEmprestimo();
        } catch (RegraNegocioException e) {
            return e.getMessage();
        }

        try {
            item.emprestar();
        } catch (RegraNegocioException e) {
            membro.registrarDevolucao();
            return e.getMessage();
        }

        emprestimos.add(new Emprestimo(item, membro));

        return "Emprestimo registrado para " + membro.getNome()
                + ".\nItem: " + item.exibirResumo()
                + "\nPrazo: " + item.getPrazoDias() + " dias";
    }

    public String registrarDevolucao(String codigoItem, int diasAtraso) {
        Emprestimo emprestimo = buscarEmprestimoAtivo(codigoItem);
        if (emprestimo == null) {
            return "Nao existe emprestimo ativo para este item.";
        }

        try {
            double multa = emprestimo.registrarDevolucao(diasAtraso);
            emprestimo.getItem().devolver();
            emprestimo.getMembro().registrarDevolucao();

            return "Devolucao registrada: " + emprestimo.getItem().exibirResumo()
                    + "\nDias de atraso: " + diasAtraso
                    + "\nMulta: " + Multavel.formatarValor(multa)
                    + "\nPolitica aplicada: " + emprestimo.getItem().descreverPoliticaMulta();
        } catch (RegraNegocioException e) {
            return e.getMessage();
        }
    }

    public String getUltimoErro() {
        return ultimoErro;
    }

    public void carregarDadosIniciais() {
        cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        cadastrarRevista("R01", "Superinteressante", 456);
        cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);
    }
}
