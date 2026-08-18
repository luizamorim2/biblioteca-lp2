package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Membro {

    private static final int LIMITE_EMPRESTIMOS = 3;

    private final String matricula;
    private String nome;
    private int emprestimosAtivos;

    public Membro(String matricula, String nome) throws RegraNegocioException {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new RegraNegocioException("Matricula nao pode ficar vazia.");
        }
        validarNome(nome);
        this.matricula = matricula.trim().toUpperCase();
        this.nome = nome.trim();
        this.emprestimosAtivos = 0;
    }

    private static void validarNome(String nome) throws RegraNegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("Nome do membro nao pode ficar vazio.");
        }
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String novoNome) throws RegraNegocioException {
        validarNome(novoNome);
        this.nome = novoNome.trim();
    }

    public int getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public void registrarEmprestimo() throws RegraNegocioException {
        if (emprestimosAtivos >= LIMITE_EMPRESTIMOS) {
            throw new RegraNegocioException(nome + " atingiu o limite de "
                    + LIMITE_EMPRESTIMOS + " emprestimos.");
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
                + "\n   Emprestimos ativos: " + emprestimosAtivos + "/" + LIMITE_EMPRESTIMOS;
    }
}
