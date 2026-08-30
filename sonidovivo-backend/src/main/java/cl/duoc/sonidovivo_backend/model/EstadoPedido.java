package cl.duoc.sonidovivo_backend.model;

/** Estados posibles del ciclo de vida de un pedido. */
public enum EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    DESPACHADO,
    ENTREGADO,
    CANCELADO
}