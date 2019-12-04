package rafael.altran.exercicio.carrinhocomprasbackend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Document(collection="itens")
@EqualsAndHashCode(of = "id")
public class Item {

    @Id
    private Long id;

    @NotBlank
    @Size(max=100)
    private String name;

    @Positive
    private BigDecimal value;

}
