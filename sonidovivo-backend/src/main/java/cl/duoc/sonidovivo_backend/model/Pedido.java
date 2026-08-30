package cl.duoc.sonidovivo_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabecera de un pedido/compra.
 *
 * OJO: no tiene una relación @ManyToOne hacia Usuario, porque Usuario vive
 * en OTRO microservicio con su propia base de datos (usuarios-service).
 * En una arquitectura de microservicios, cada servicio es dueño de sus
 * propias tablas; entre servicios solo se comparten IDs, nunca relaciones
 * JPA directas. Por eso aquí solo guardamos el id del usuario como un
 * simple número (usuarioId).
 */
@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();
}