package rafael.altran.exercicio.carrinhocomprasbackend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection="carts")
@EqualsAndHashCode(of = "id")
public class Cart {

    @Id
    private Long id;

    private User user;

    private List<Item> items = new ArrayList<>();

    private CartStatus status = CartStatus.OPEN;



}
