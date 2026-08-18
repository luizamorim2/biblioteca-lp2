package biblioteca.domain;

public enum TipoMembro {

    ALUNO(3),
    PROFESSOR(5),
    COMUNIDADE(2);

    private final int limiteEmprestimos;

    TipoMembro(int limiteEmprestimos) {
        this.limiteEmprestimos = limiteEmprestimos;
    }

    public int getLimiteEmprestimos() {
        return limiteEmprestimos;
    }

    @Override
    public String toString() {
        return name() + " (ate " + limiteEmprestimos + " emprestimos)";
    }
}
