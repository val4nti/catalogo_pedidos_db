package cl.duoc.sonidovivo_backend.exception;

/** Se lanza cuando se intenta comprar más unidades de las que hay en stock. */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}