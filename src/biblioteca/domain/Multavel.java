package biblioteca.domain;

public interface Multavel {

    double calcularMulta(int diasAtraso);

    String descreverPoliticaMulta();

    static String formatarValor(double valor) {
        return String.format("R$ %.2f", valor);
    }
}
