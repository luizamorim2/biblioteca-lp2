package biblioteca;

import biblioteca.domain.TipoMembro;
import org.junit.Test;

import static org.junit.Assert.*;

public class ControladorBibliotecaTest {

    @Test
    public void testCadastrarLivroValido() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        boolean res = controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        assertTrue(res);
        assertEquals(1, controlador.contarItens());
    }

    @Test
    public void testCadastrarRevistaValida() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        boolean res = controlador.cadastrarRevista("R01", "Superinteressante", 456);

        assertTrue(res);
        assertEquals(1, controlador.contarItens());
    }

    @Test
    public void testNaoCadastrarItemComCodigoDuplicado() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        boolean res1 = controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        boolean res2 = controlador.cadastrarRevista("L01", "Superinteressante", 456);

        assertTrue(res1);
        assertFalse(res2);
        assertEquals(1, controlador.contarItens());
        assertEquals("Ja existe um item com o codigo L01.", controlador.getUltimoErro());
    }

    @Test
    public void testNaoCadastrarLivroSemTitulo() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        boolean res = controlador.cadastrarLivro("L01", "   ", "Machado de Assis");

        assertFalse(res);
        assertEquals(0, controlador.contarItens());
        assertEquals("Titulo nao pode ficar vazio.", controlador.getUltimoErro());
    }

    @Test
    public void testNaoCadastrarRevistaComEdicaoInvalida() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        boolean res = controlador.cadastrarRevista("R01", "Superinteressante", 0);

        assertFalse(res);
        assertEquals("Numero da edicao deve ser maior que zero.", controlador.getUltimoErro());
    }

    @Test
    public void testBuscarItemIgnorandoMaiusculas() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        assertNotNull(controlador.buscarItem("l01"));
        assertEquals("Dom Casmurro", controlador.buscarItem("l01").getTitulo());
    }

    @Test
    public void testBuscarItemInexistente() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        assertNull(controlador.buscarItem("XX99"));
        assertEquals("Item nao encontrado.", controlador.consultarItem("XX99"));
    }

    @Test
    public void testPrazoDependeDoTipoDoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarRevista("R01", "Superinteressante", 456);

        assertEquals(14, controlador.buscarItem("L01").getPrazoDias());
        assertEquals(7, controlador.buscarItem("R01").getPrazoDias());
    }

    @Test
    public void testMultaDoLivroCrescePorDiaSemTeto() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        assertEquals(1.50, controlador.buscarItem("L01").calcularMulta(1), 0.001);
        assertEquals(67.50, controlador.buscarItem("L01").calcularMulta(45), 0.001);
    }

    @Test
    public void testMultaDaRevistaParaNoTeto() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarRevista("R01", "Superinteressante", 456);

        assertEquals(2.00, controlador.buscarItem("R01").calcularMulta(4), 0.001);
        assertEquals(10.00, controlador.buscarItem("R01").calcularMulta(52), 0.001);
    }

    @Test
    public void testItemNoPrazoNaoGeraMulta() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        assertEquals(0.0, controlador.buscarItem("L01").calcularMulta(0), 0.001);
    }

    @Test
    public void testAlterarTituloDoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        boolean res = controlador.alterarTituloItem("L01", "Dom Casmurro - Edicao Comentada");

        assertTrue(res);
        assertEquals("Dom Casmurro - Edicao Comentada", controlador.buscarItem("L01").getTitulo());
    }

    @Test
    public void testNaoAlterarTituloParaVazioMantemOAnterior() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        boolean res = controlador.alterarTituloItem("L01", "");

        assertFalse(res);
        assertEquals("Titulo nao pode ficar vazio.", controlador.getUltimoErro());
        assertEquals("Dom Casmurro", controlador.buscarItem("L01").getTitulo());
    }

    @Test
    public void testRealizarEmprestimo() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);

        String res = controlador.realizarEmprestimo("L01", "A100");

        assertTrue(res.contains("Emprestimo registrado para Ana Souza."));
        assertFalse(controlador.buscarItem("L01").isDisponivel());
        assertEquals(1, controlador.buscarMembro("A100").getEmprestimosAtivos());
    }

    @Test
    public void testNaoEmprestarItemJaEmprestado() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        controlador.cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);

        controlador.realizarEmprestimo("L01", "A100");
        String res = controlador.realizarEmprestimo("L01", "P200");

        assertEquals("O item \"Dom Casmurro\" ja esta emprestado.", res);
        assertEquals(0, controlador.buscarMembro("P200").getEmprestimosAtivos());
    }

    @Test
    public void testAlunoNaoPassaDoLimiteDoSeuTipo() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");
        controlador.cadastrarLivro("L04", "Iracema", "Jose de Alencar");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);

        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("L02", "A100");
        controlador.realizarEmprestimo("L03", "A100");
        String res = controlador.realizarEmprestimo("L04", "A100");

        assertEquals("Ana Souza atingiu o limite de 3 emprestimos do tipo ALUNO.", res);
        assertEquals(3, controlador.buscarMembro("A100").getEmprestimosAtivos());
        assertTrue(controlador.buscarItem("L04").isDisponivel());
    }

    @Test
    public void testProfessorTemLimiteMaiorQueAluno() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");
        controlador.cadastrarLivro("L04", "Iracema", "Jose de Alencar");
        controlador.cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);

        controlador.realizarEmprestimo("L01", "P200");
        controlador.realizarEmprestimo("L02", "P200");
        controlador.realizarEmprestimo("L03", "P200");
        String res = controlador.realizarEmprestimo("L04", "P200");

        assertTrue(res.contains("Emprestimo registrado para Carlos Lima."));
        assertEquals(4, controlador.buscarMembro("P200").getEmprestimosAtivos());
    }

    @Test
    public void testNaoRebaixarTipoComEmprestimosAcimaDoNovoLimite() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarLivro("L02", "Clean Code", "Robert C. Martin");
        controlador.cadastrarLivro("L03", "O Cortico", "Aluisio Azevedo");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);

        controlador.realizarEmprestimo("L01", "A100");
        controlador.realizarEmprestimo("L02", "A100");
        controlador.realizarEmprestimo("L03", "A100");

        boolean res = controlador.alterarTipoMembro("A100", TipoMembro.COMUNIDADE);

        assertFalse(res);
        assertEquals("Ana Souza possui 3 emprestimos ativos e o tipo COMUNIDADE permite apenas 2.",
                controlador.getUltimoErro());
        assertEquals(TipoMembro.ALUNO, controlador.buscarMembro("A100").getTipo());
    }

    @Test
    public void testAlterarTipoMembroQuandoPermitido() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);

        boolean res = controlador.alterarTipoMembro("A100", TipoMembro.PROFESSOR);

        assertTrue(res);
        assertEquals(TipoMembro.PROFESSOR, controlador.buscarMembro("A100").getTipo());
    }

    @Test
    public void testDevolucaoLiberaItemEVagaDoMembro() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        controlador.realizarEmprestimo("L01", "A100");

        String res = controlador.registrarDevolucao("L01", 0);

        assertTrue(res.contains("Dias de atraso: 0"));
        assertTrue(controlador.buscarItem("L01").isDisponivel());
        assertEquals(0, controlador.buscarMembro("A100").getEmprestimosAtivos());
        assertEquals("Nenhum emprestimo ativo no momento.", controlador.listarEmprestimosAtivos());
    }

    @Test
    public void testDevolucaoInformaAPoliticaDeMultaDoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarRevista("R01", "Superinteressante", 456);
        controlador.cadastrarMembro("P200", "Carlos Lima", TipoMembro.PROFESSOR);
        controlador.realizarEmprestimo("L01", "P200");
        controlador.realizarEmprestimo("R01", "P200");

        String resLivro = controlador.registrarDevolucao("L01", 45);
        String resRevista = controlador.registrarDevolucao("R01", 45);

        assertFalse(resLivro.contains("limitada a"));
        assertTrue(resRevista.contains("limitada a"));
    }

    @Test
    public void testDevolverItemSemEmprestimoAtivo() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        String res = controlador.registrarDevolucao("L01", 0);

        assertEquals("Nao existe emprestimo ativo para este item.", res);
    }

    @Test
    public void testNaoDevolverDuasVezesOMesmoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        controlador.realizarEmprestimo("L01", "A100");
        controlador.registrarDevolucao("L01", 0);

        String res = controlador.registrarDevolucao("L01", 0);

        assertEquals("Nao existe emprestimo ativo para este item.", res);
    }

    @Test
    public void testDiasDeAtrasoNegativoNaoEAceito() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);
        controlador.realizarEmprestimo("L01", "A100");

        String res = controlador.registrarDevolucao("L01", -5);

        assertEquals("Dias de atraso nao pode ser negativo.", res);
        assertFalse(controlador.buscarItem("L01").isDisponivel());
    }

    @Test
    public void testListarAcervoTrazTodosOsTiposDeItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarRevista("R01", "Superinteressante", 456);

        String res = controlador.listarAcervo();

        assertTrue(res.contains("[Livro] L01"));
        assertTrue(res.contains("Autor: Machado de Assis"));
        assertTrue(res.contains("[Revista] R01"));
        assertTrue(res.contains("Edicao: 456"));
    }
}
