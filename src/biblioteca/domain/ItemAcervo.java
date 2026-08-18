package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public abstract class ItemAcervo implements Multavel {

    private final String codigo;
    private String titulo;
    private final int anoPublicacao;
    private boolean disponivel;

    protected ItemAcervo(String codigo, String titulo, int anoPublicacao) throws RegraNegocioException {
        validarTexto(codigo, "Codigo do item");
        validarTexto(titulo, "Titulo");
        if (anoPublicacao < 1400 || anoPublicacao > 2100) {
            throw new RegraNegocioException("Ano de publicacao invalido: " + anoPublicacao + ".");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.titulo = titulo.trim();
        this.anoPublicacao = anoPublicacao;
        this.disponivel = true;
    }

    protected static void validarTexto(String valor, String campo) throws RegraNegocioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException(campo + " nao pode ficar vazio.");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String novoTitulo) throws RegraNegocioException {
        validarTexto(novoTitulo, "Titulo");
        this.titulo = novoTitulo.trim();
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void emprestar() throws RegraNegocioException {
        if (!disponivel) {
            throw new RegraNegocioException("O item \"" + titulo + "\" ja esta emprestado.");
        }
        this.disponivel = false;
    }

    public void devolver() {
        this.disponivel = true;
    }

    public abstract String getTipo();

    public abstract int getPrazoDias();

    protected abstract String descreverDadosEspecificos();

    public String descreverSituacao() {
        if (disponivel) {
            return "Disponivel";
        }
        return "Emprestado";
    }

    public String exibirResumo() {
        return "[" + getTipo() + "] " + codigo + " - " + titulo + " (" + descreverSituacao() + ")";
    }

    public String exibirInformacoes() {
        return "[" + getTipo() + "] " + codigo + " - " + titulo
                + "\n   Ano: " + anoPublicacao
                + "\n   " + descreverDadosEspecificos()
                + "\n   Prazo: " + getPrazoDias() + " dias"
                + "\n   Situacao: " + descreverSituacao()
                + "\n   Multa: " + descreverPoliticaMulta();
    }
}
