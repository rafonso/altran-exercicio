package rafael.altran.exercicio.carrinhocomprasbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Document(collection = "cart_items")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    private Long id;

    @NotNull
    @DBRef
    private Item item;

    @Positive
    @NotNull
    private Integer quantity;

    @JsonProperty
    public BigDecimal getItemValue() {
        return (Objects.isNull(item) || Objects.isNull(item.getValue()) || Objects.isNull(quantity)) ?
                BigDecimal.ZERO :
                item.getValue().multiply(BigDecimal.valueOf(quantity.longValue()));
    }

}
