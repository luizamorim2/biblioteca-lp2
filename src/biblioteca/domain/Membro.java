package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Membro {

    private final String matricula;
    private final String nome;
    private TipoMembro tipo;
    private int emprestimosAtivos;

    public Membro(String matricula, String nome, TipoMembro tipo) throws RegraNegocioException {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new RegraNegocioException("Matricula nao pode ficar vazia.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Nome do membro nao pode ficar vazio.");
        }
        if (tipo == null) {
            throw new RegraNegocioException("Tipo de membro invalido.");
        }
        this.matricula = matricula.trim().toUpperCase();
        this.nome = nome.trim();
        this.tipo = tipo;
        this.emprestimosAtivos = 0;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setTipo(TipoMembro novoTipo) throws RegraNegocioException {
        if (novoTipo == null) {
            throw new RegraNegocioException("Tipo de membro invalido.");
        }
        if (emprestimosAtivos > novoTipo.getLimiteEmprestimos()) {
            throw new RegraNegocioException(nome + " possui " + emprestimosAtivos
                    + " emprestimos ativos e o tipo " + novoTipo.name()
                    + " permite apenas " + novoTipo.getLimiteEmprestimos() + ".");
        }
        this.tipo = novoTipo;
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void registrarEmprestimo() throws RegraNegocioException {
        if (emprestimosAtivos >= tipo.getLimiteEmprestimos()) {
            throw new RegraNegocioException(nome + " atingiu o limite de "
                    + tipo.getLimiteEmprestimos() + " emprestimos do tipo " + tipo.name() + ".");
        }
        this.emprestimosAtivos++;
    }

    public void registrarDevolucao() {
        if (emprestimosAtivos > 0) {
            this.emprestimosAtivos--;
        }
    }

    public String exibirInformacoes() {
        return matricula + " - " + nome
                + "\n   Tipo: " + tipo.name()
                + "\n   Emprestimos ativos: " + emprestimosAtivos + "/" + tipo.getLimiteEmprestimos();
    }
}
