package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Revista extends ItemAcervo {

    private static final int PRAZO_DIAS = 7;
    private static final double MULTA_POR_DIA = 0.50;
    private static final double TETO_MULTA = 10.00;

    private final int edicao;

    public Revista(String codigo, String titulo, int anoPublicacao, int edicao) throws RegraNegocioException {
        super(codigo, titulo, anoPublicacao);
        if (edicao <= 0) {
            throw new RegraNegocioException("Numero da edicao deve ser maior que zero.");
        }
        this.edicao = edicao;
    }

    public int getEdicao() {
        return edicao;
    }

    @Override
    public String getTipo() {
        return "Revista";
    }

    @Override
    public int getPrazoDias() {
        return PRAZO_DIAS;
    }

    @Override
    protected String descreverDadosEspecificos() {
        return "Edicao: " + edicao;
    }

    @Override
    public double calcularMulta(int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return Math.min(diasAtraso * MULTA_POR_DIA, TETO_MULTA);
    }

    @Override
    public String descreverPoliticaMulta() {
        return Multavel.formatarValor(MULTA_POR_DIA) + " por dia de atraso, limitada a "
                + Multavel.formatarValor(TETO_MULTA);
    }
}
