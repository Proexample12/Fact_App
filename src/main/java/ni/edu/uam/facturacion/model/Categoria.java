package ni.edu.uam.facturacion.model;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    private Integer id;
    private String nombre;
    private Boolean activo;

    @Override
    public String toString() {
        return nombre; }
}
