package biblioteca.domain;

import biblioteca.exception.RegraNegocioException;

public class Emprestimo {

    private final ItemAcervo item;
    private final Membro membro;
    private StatusEmprestimo status;
    private double multa;

    public Emprestimo(ItemAcervo item, Membro membro) throws RegraNegocioException {
        if (item == null || membro == null) {
            throw new RegraNegocioException("Dados do emprestimo incompletos.");
        }
        this.item = item;
        this.membro = membro;
        this.status = StatusEmprestimo.ATIVO;
        this.multa = 0.0;
    }

    public ItemAcervo getItem() {
        return item;
    }

    public Membro getMembro() {
        return membro;
    }

    public StatusEmprestimo getStatus() {
        return status;
    }

    public double getMulta() {
        return multa;
    }

    public boolean estaAtivo() {
        return status == StatusEmprestimo.ATIVO;
    }

    public double registrarDevolucao(int diasAtraso) throws RegraNegocioException {
        if (status == StatusEmprestimo.DEVOLVIDO) {
            throw new RegraNegocioException("Este emprestimo ja foi devolvido.");
        }
        if (diasAtraso < 0) {
            throw new RegraNegocioException("Dias de atraso nao pode ser negativo.");
        }
        Multavel politicaDeMulta = item;
        this.multa = politicaDeMulta.calcularMulta(diasAtraso);
        this.status = StatusEmprestimo.DEVOLVIDO;
        return multa;
    }

    public String exibirInformacoes() {
        return item.exibirResumo()
                + "\n   Membro: " + membro.getNome() + " (" + membro.getMatricula() + ")"
                + "\n   Prazo: " + item.getPrazoDias() + " dias"
                + "\n   Status: " + status.getDescricao();
    }
}
