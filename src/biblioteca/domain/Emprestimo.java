package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Emprestimo {

    private final ItemAcervo item;
    private final Membro membro;
    private boolean devolvido;

    public Emprestimo(ItemAcervo item, Membro membro) {
        this.item = item;
        this.membro = membro;
        this.devolvido = false;
    }

    public ItemAcervo getItem() {
        return item;
    }

    public Membro getMembro() {
        return membro;
    }

    public boolean estaAtivo() {
        return !devolvido;
    }

    public double registrarDevolucao(int diasAtraso) throws RegraNegocioException {
        if (diasAtraso < 0) {
            throw new RegraNegocioException("Dias de atraso nao pode ser negativo.");
        }
        Multavel politicaDeMulta = item;
        this.devolvido = true;
        return politicaDeMulta.calcularMulta(diasAtraso);
    }
}
