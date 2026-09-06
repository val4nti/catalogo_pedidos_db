package cl.duoc.sonidovivo_backend.service;


import cl.duoc.sonidovivo_backend.model.DetallePedido;
import cl.duoc.sonidovivo_backend.model.Pedido;
import cl.duoc.sonidovivo_backend.model.Producto;
import cl.duoc.sonidovivo_backend.repository.Pedidorepository;
import cl.duoc.sonidovivo_backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final Pedidorepository pedidoRepository;
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
     * Crea un pedido a partir del carrito. @Transactional asegura que, si
     * algo falla a mitad de camino (ej: el tercer producto no tiene stock),
     * TODO se revierte -- no queda un pedido a medio crear ni stock
     * descontado de productos anteriores del mismo carrito.
     */
    @Transactional
    public Pedido crearPedido(CrearPedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(request.getUsuarioId());

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para \"" + producto.getNombre() + "\". Disponible: " + producto.getStock());
            }

            // Descuenta el stock inmediatamente.
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            pedido.getDetalles().add(detalle);
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }
}