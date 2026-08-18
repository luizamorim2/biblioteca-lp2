package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Livro extends ItemAcervo {

    private static final int PRAZO_DIAS = 14;
    private static final double MULTA_POR_DIA = 1.50;

    private final String autor;

    public Livro(String codigo, String titulo, String autor) throws RegraNegocioException {
        super(codigo, titulo);
        validarTexto(autor, "Autor");
        this.autor = autor.trim();
    }

    @Override
    public String getTipo() {
        return "Livro";
    }

    @Override
    public int getPrazoDias() {
        return PRAZO_DIAS;
    }

    @Override
    protected String descreverDadosEspecificos() {
        return "Autor: " + autor;
    }

    @Override
    public double calcularMulta(int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return diasAtraso * MULTA_POR_DIA;
    }

    @Override
    public String descreverPoliticaMulta() {
        return Multavel.formatarValor(MULTA_POR_DIA) + " por dia de atraso";
    }
}
