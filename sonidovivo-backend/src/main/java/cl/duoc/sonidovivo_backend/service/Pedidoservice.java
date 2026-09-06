package cl.duoc.sonidovivo_backend.service;

import cl.duoc.sonidovivo_backend.exception.RecursoNoEncontradoException;
import cl.duoc.sonidovivo_backend.exception.StockInsuficienteException;
import cl.duoc.sonidovivo_backend.model.DetallePedido;
import cl.duoc.sonidovivo_backend.model.Pedido;
import cl.duoc.sonidovivo_backend.model.Producto;
import cl.duoc.sonidovivo_backend.repository.PedidoRepository;
import cl.duoc.sonidovivo_backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
    }

    /**
     * Crea un pedido a partir de un Map genérico (sin DTO), con la forma:
     * { "usuarioId": 1, "items": [ {"productoId": 5, "cantidad": 2}, ... ] }
     *
     * Al no usar una clase tipada, hay que convertir "a mano" cada valor
     * (Number -> Long/Integer) y validar que no venga nulo o mal escrito,
     * cosas que con un DTO + @Valid Spring habría hecho automáticamente.
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public Pedido crearPedido(Map<String, Object> body) {
        Long usuarioId = convertirALong(body.get("usuarioId"), "usuarioId");

        Object itemsObj = body.get("items");
        if (!(itemsObj instanceof List<?> itemsList) || itemsList.isEmpty()) {
            throw new StockInsuficienteException("El carrito está vacío o mal formado.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);

        BigDecimal total = BigDecimal.ZERO;

        for (Object itemObj : itemsList) {
            if (!(itemObj instanceof Map<?, ?> itemMapRaw)) {
                throw new StockInsuficienteException("Cada item del carrito debe ser un objeto JSON.");
            }
            Map<String, Object> itemMap = (Map<String, Object>) itemMapRaw;

            Long productoId = convertirALong(itemMap.get("productoId"), "productoId");
            Integer cantidad = convertirAInteger(itemMap.get("cantidad"), "cantidad");

            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + productoId));

            if (producto.getStock() < cantidad) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para \"" + producto.getNombre() + "\". Disponible: " + producto.getStock());
            }

            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.getDetalles().add(detalle);
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    private Long convertirALong(Object valor, String nombreCampo) {
        if (valor == null) {
            throw new StockInsuficienteException("Falta el campo \"" + nombreCampo + "\".");
        }
        if (valor instanceof Number numero) {
            return numero.longValue();
        }
        throw new StockInsuficienteException("El campo \"" + nombreCampo + "\" debe ser un número.");
    }

    private Integer convertirAInteger(Object valor, String nombreCampo) {
        if (valor == null) {
            throw new StockInsuficienteException("Falta el campo \"" + nombreCampo + "\".");
        }
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        throw new StockInsuficienteException("El campo \"" + nombreCampo + "\" debe ser un número.");
    }
}