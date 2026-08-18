package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public abstract class ItemAcervo implements Multavel {

    private final String codigo;
    private String titulo;
    private boolean disponivel;

    protected ItemAcervo(String codigo, String titulo) throws RegraNegocioException {
        validarTexto(codigo, "Codigo do item");
        validarTexto(titulo, "Titulo");
        this.codigo = codigo.trim().toUpperCase();
        this.titulo = titulo.trim();
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

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setTitulo(String novoTitulo) throws RegraNegocioException {
        validarTexto(novoTitulo, "Titulo");
        this.titulo = novoTitulo.trim();
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

    private String descreverSituacao() {
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
                + "\n   " + descreverDadosEspecificos()
                + "\n   Prazo: " + getPrazoDias() + " dias"
                + "\n   Situacao: " + descreverSituacao()
                + "\n   Multa: " + descreverPoliticaMulta();
    }
}
