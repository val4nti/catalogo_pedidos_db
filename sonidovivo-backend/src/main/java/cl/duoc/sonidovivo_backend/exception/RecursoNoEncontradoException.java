package cl.duoc.sonidovivo_backend.exception;

/** Se lanza cuando se busca un producto, categoría o pedido que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
