package cl.duoc.sonidovivo_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Producto del catálogo. Incluye stockCritico: cuando el stock actual es
 * igual o menor a este valor, el sistema debe disparar una alerta (lógica
 * que se implementa en la capa de servicio, no aquí).
 */
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3)
    @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    // Opcional: si es null, no se evalúa alerta de stock crítico.
    @Min(0)
    private Integer stockCritico;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    private String imagen;

    /** Regla de negocio: true si el stock actual llegó al umbral crítico. */
    @Transient
    public boolean isStockBajoCritico() {
        return stockCritico != null && stock != null && stock <= stockCritico;
    }
}