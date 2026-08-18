package biblioteca.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import biblioteca.exception.RegraNegocioException;

public class EmprestimoTest {

    private static final double MARGEM = 0.001;

    private ItemAcervo livro;
    private Membro membro;

    @Before
    public void preparar() throws RegraNegocioException {
        livro = new Livro("L01", "Dom Casmurro", "Machado de Assis");
        membro = new Membro("A100", "Ana Souza", TipoMembro.ALUNO);
    }

    @Test
    public void testEmprestimoNasceAtivo() throws Exception {
        Emprestimo emprestimo = new Emprestimo(livro, membro);

        assertTrue(emprestimo.estaAtivo());
        assertTrue(emprestimo.exibirInformacoes().contains("Ana Souza"));
    }

    @Test
    public void testDevolucaoEncerraOEmprestimoERetornaAMulta() throws Exception {
        Emprestimo emprestimo = new Emprestimo(livro, membro);

        double multa = emprestimo.registrarDevolucao(10);

        assertEquals(15.00, multa, MARGEM);
        assertFalse(emprestimo.estaAtivo());
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoDevolverOMesmoEmprestimoDuasVezes() throws Exception {
        Emprestimo emprestimo = new Emprestimo(livro, membro);

        emprestimo.registrarDevolucao(0);
        emprestimo.registrarDevolucao(0);
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoAceitarDiasDeAtrasoNegativos() throws Exception {
        new Emprestimo(livro, membro).registrarDevolucao(-1);
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoCriarEmprestimoSemMembro() throws Exception {
        new Emprestimo(livro, null);
    }

    @Test
    public void testMembroLiberaVagaAoDevolver() throws Exception {
        membro.registrarEmprestimo();
        assertTrue(membro.exibirInformacoes().contains("1/3"));

        membro.registrarDevolucao();
        assertTrue(membro.exibirInformacoes().contains("0/3"));
    }
}
