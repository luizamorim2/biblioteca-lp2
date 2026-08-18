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
    public void testAlterarTituloDoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");

        boolean res = controlador.alterarTituloItem("L01", "Dom Casmurro - Edicao Comentada");

        assertTrue(res);
        assertEquals("Dom Casmurro - Edicao Comentada", controlador.buscarItem("L01").getTitulo());
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
    public void testMultaDependeDaPoliticaDoItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarRevista("R01", "Superinteressante", 456);

        assertEquals(67.50, controlador.buscarItem("L01").calcularMulta(45), 0.001);
        assertEquals(10.00, controlador.buscarItem("R01").calcularMulta(45), 0.001);
    }

    @Test
    public void testEmprestarEDevolverUmItem() {
        ControladorBiblioteca controlador = new ControladorBiblioteca();

        controlador.cadastrarLivro("L01", "Dom Casmurro", "Machado de Assis");
        controlador.cadastrarMembro("A100", "Ana Souza", TipoMembro.ALUNO);

        controlador.realizarEmprestimo("L01", "A100");

        assertFalse(controlador.buscarItem("L01").isDisponivel());
        assertEquals(1, controlador.buscarMembro("A100").getEmprestimosAtivos());

        controlador.registrarDevolucao("L01", 0);

        assertTrue(controlador.buscarItem("L01").isDisponivel());
        assertEquals(0, controlador.buscarMembro("A100").getEmprestimosAtivos());
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
    }
}
