package rafael.altran.exercicio.carrinhocomprasbackend.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "carts")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private Long id;

    @NotNull
    @DBRef
    private User user;

    @Valid
    @DBRef
    private List<CartItem> cartItems = new ArrayList<>();

    @NonNull
    private CartStatus status = CartStatus.OPEN;

    public BigDecimal getCartValue() {
        return cartItems.stream().map(CartItem::getItemValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
