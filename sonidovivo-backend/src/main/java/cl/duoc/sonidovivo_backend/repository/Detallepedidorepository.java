package cl.duoc.sonidovivo_backend.repository;

import cl.duoc.sonidovivo_backend.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}