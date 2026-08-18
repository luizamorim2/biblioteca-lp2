package biblioteca.domain;

public enum StatusEmprestimo {

    ATIVO("Ativo"),
    DEVOLVIDO("Devolvido");

    private final String descricao;

    StatusEmprestimo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
