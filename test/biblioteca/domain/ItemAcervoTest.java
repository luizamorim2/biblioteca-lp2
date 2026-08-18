package biblioteca.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import biblioteca.exception.RegraNegocioException;

public class ItemAcervoTest {

    private static final double MARGEM = 0.001;

    private ItemAcervo livro;
    private ItemAcervo revista;

    @Before
    public void preparar() throws RegraNegocioException {
        livro = new Livro("L01", "Dom Casmurro", "Machado de Assis");
        revista = new Revista("R01", "Superinteressante", 456);
    }

    @Test
    public void testPrazoDependeDoTipoDoItem() {
        assertEquals(14, livro.getPrazoDias());
        assertEquals(7, revista.getPrazoDias());
    }

    @Test
    public void testCadaItemDescreveSeuProprioTipo() {
        assertEquals("Livro", livro.getTipo());
        assertEquals("Revista", revista.getTipo());
    }

    @Test
    public void testMultaDoLivroCrescePorDiaSemTeto() {
        Multavel politica = livro;

        assertEquals(1.50, politica.calcularMulta(1), MARGEM);
        assertEquals(15.00, politica.calcularMulta(10), MARGEM);
        assertEquals(67.50, politica.calcularMulta(45), MARGEM);
    }

    @Test
    public void testMultaDaRevistaParaNoTeto() {
        Multavel politica = revista;

        assertEquals(2.00, politica.calcularMulta(4), MARGEM);
        assertEquals(10.00, politica.calcularMulta(20), MARGEM);
        assertEquals(10.00, politica.calcularMulta(52), MARGEM);
    }

    @Test
    public void testItemEntregueNoPrazoNaoGeraMulta() {
        assertEquals(0.0, livro.calcularMulta(0), MARGEM);
        assertEquals(0.0, revista.calcularMulta(0), MARGEM);
    }

    @Test
    public void testItemComecaDisponivelEMudaAoSerEmprestado() throws Exception {
        assertTrue(livro.exibirResumo().contains("Disponivel"));

        livro.emprestar();
        assertTrue(livro.exibirResumo().contains("Emprestado"));

        livro.devolver();
        assertTrue(livro.exibirResumo().contains("Disponivel"));
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoEmprestarItemIndisponivel() throws Exception {
        livro.emprestar();
        livro.emprestar();
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoCriarRevistaComEdicaoInvalida() throws Exception {
        new Revista("R99", "Revista Sem Edicao", 0);
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoAlterarTituloParaVazio() throws Exception {
        livro.setTitulo("");
    }
}
