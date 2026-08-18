package biblioteca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import biblioteca.domain.TipoMembro;
import biblioteca.exception.EntidadeNaoEncontradaException;
import biblioteca.exception.RegraNegocioException;

public class ControladorBibliotecaTest {

    private ControladorBiblioteca controlador;

    @Before
    public void preparar() throws RegraNegocioException {
        controlador = new ControladorBiblioteca();
        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarRevista("R01", "Superinteressante", 456);
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        controlador.cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);
    }

    @Test
    public void testCadastrarLivroEConsultarPeloCodigo() throws Exception {
        String res = controlador.consultarItem("L01");

        assertTrue(res.contains("Dom Casmurro"));
        assertTrue(res.contains("Autor: Machado de Assis"));
        assertTrue(res.contains("Situacao: Disponivel"));
    }

    @Test
    public void testCodigoDoItemNaoDiferenciaMaiusculas() throws Exception {
        String res = controlador.consultarItem("l01");

        assertTrue(res.contains("Dom Casmurro"));
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoCadastrarItemComCodigoDuplicado() throws Exception {
        controlador.cadastrarLivro("L01", "Outro Livro", "Outro Autor");
    }

    @Test(expected = RegraNegocioException.class)
    public void testNaoCadastrarLivroSemTitulo() throws Exception {
        controlador.cadastrarLivro("L99", "   ", "Machado de Assis");
    }

    @Test(expected = EntidadeNaoEncontradaException.class)
    public void testConsultarItemInexistente() throws Exception {
        controlador.consultarItem("XX99");
    }

    @Test
    public void testAlterarTituloDoItem() throws Exception {
        controlador.alterarTituloItem("L01", "Dom Casmurro - Edicao Comentada");

        assertTrue(controlador.consultarItem("L01").contains("Dom Casmurro - Edicao Comentada"));
    }

    @Test
    public void testEmprestimoMarcaItemComoEmprestado() throws Exception {
        controlador.realizarEmprestimo("L01", "A100");

        assertTrue(controlador.consultarItem("L01").contains("Situacao: Emprestado"));
        assertTrue(controlador.listarMembros().contains("Emprestimos ativos: 1/3"));
    }

    @Test
    public void testNaoEmprestarItemJaEmprestado() throws Exception {
        controlador.realizarEmprestimo("L01", "A100");

        try {
            controlador.realizarEmprestimo("L01", "P200");
            fail("Deveria ter lancado RegraNegocioException.");
        } catch (RegraNegocioException e) {
            assertEquals("O item \"Dom Casmurro\" ja esta emprestado.", e.getMessage());
        }
    }

    @Test
    public void testAlunoNaoPassaDoLimiteDoSeuTipo() throws Exception {
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");
        controlador.cadastrarLivro("L04", "Iracema", "Jose de Alencar");

        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("L02", "A100");
        controlador.realizarEmprestimo("L03", "A100");

        try {
            controlador.realizarEmprestimo("L04", "A100");
            fail("Deveria ter lancado RegraNegocioException.");
        } catch (RegraNegocioException e) {
            assertEquals("Ana Souza atingiu o limite de 3 emprestimos do tipo ALUNO.", e.getMessage());
        }
    }

    @Test
    public void testItemContinuaDisponivelQuandoLimiteEstoura() throws Exception {
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");
        controlador.cadastrarLivro("L04", "Iracema", "Jose de Alencar");

        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("L02", "A100");
        controlador.realizarEmprestimo("L03", "A100");

        try {
            controlador.realizarEmprestimo("L04", "A100");
        } catch (RegraNegocioException e) {
            assertTrue(e.getMessage().contains("limite"));
        }

        assertTrue(controlador.consultarItem("L04").contains("Situacao: Disponivel"));
        controlador.realizarEmprestimo("L04", "P200");
        assertTrue(controlador.consultarItem("L04").contains("Situacao: Emprestado"));
    }

    @Test
    public void testProfessorTemLimiteMaiorQueAluno() throws Exception {
        assertTrue(controlador.listarMembros().contains("Emprestimos ativos: 0/3"));
        assertTrue(controlador.listarMembros().contains("Emprestimos ativos: 0/5"));
    }

    @Test
    public void testNaoRebaixarTipoComEmprestimosAcimaDoNovoLimite() throws Exception {
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");

        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("L02", "A100");
        controlador.realizarEmprestimo("L03", "A100");

        try {
            controlador.alterarTipoMembro("A100", TipoMembro.COMUNIDADE);
            fail("Deveria ter lancado RegraNegocioException.");
        } catch (RegraNegocioException e) {
            assertEquals("Ana Souza possui 3 emprestimos ativos e o tipo COMUNIDADE permite apenas 2.",
                    e.getMessage());
        }

        assertTrue(controlador.listarMembros().contains("Tipo: ALUNO"));
    }

    @Test
    public void testAlterarTipoMembroQuandoPermitido() throws Exception {
        String res = controlador.alterarTipoMembro("A100", TipoMembro.PROFESSOR);

        assertEquals("Ana Souza agora e do tipo PROFESSOR.", res);
        assertFalse(controlador.listarMembros().contains("Tipo: ALUNO"));
    }

    @Test
    public void testDevolucaoLiberaItemEVagaDoMembro() throws Exception {
        controlador.realizarEmprestimo("L01", "A100");

        String res = controlador.registrarDevolucao("L01", 0);

        assertTrue(res.contains("Dias de atraso: 0"));
        assertTrue(controlador.consultarItem("L01").contains("Situacao: Disponivel"));
        assertTrue(controlador.listarMembros().contains("Emprestimos ativos: 0/3"));
        assertEquals("Nenhum emprestimo ativo no momento.", controlador.listarEmprestimosAtivos());
    }

    @Test
    public void testDevolucaoComAtrasoInformaAPoliticaDoItem() throws Exception {
        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("R01", "P200");

        String resLivro = controlador.registrarDevolucao("L01", 45);
        String resRevista = controlador.registrarDevolucao("R01", 45);

        assertTrue(resLivro.contains("por dia de atraso"));
        assertFalse(resLivro.contains("limitada a"));
        assertTrue(resRevista.contains("limitada a"));
    }

    @Test(expected = EntidadeNaoEncontradaException.class)
    public void testNaoDevolverItemSemEmprestimoAtivo() throws Exception {
        controlador.registrarDevolucao("L01", 0);
    }

    @Test(expected = EntidadeNaoEncontradaException.class)
    public void testNaoDevolverDuasVezesOMesmoEmprestimo() throws Exception {
        controlador.realizarEmprestimo("L01", "A100");
        controlador.registrarDevolucao("L01", 0);
        controlador.registrarDevolucao("L01", 0);
    }

    @Test
    public void testListarAcervoTrazTodosOsTiposDeItem() {
        String res = controlador.listarAcervo();

        assertTrue(res.contains("[Livro] L01"));
        assertTrue(res.contains("[Revista] R01"));
        assertTrue(res.contains("Autor: Machado de Assis"));
        assertTrue(res.contains("Edicao: 456"));
    }
}
