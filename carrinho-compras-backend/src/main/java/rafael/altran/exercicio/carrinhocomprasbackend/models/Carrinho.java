package rafael.altran.exercicio.carrinhocomprasbackend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection="carrinhos")
@EqualsAndHashCode(of = "id")
public class Carrinho {

    @Id
    private Long id;

    private Usuario usuario;

    private List<Item> itens = new ArrayList<>();

    private StatusCarrinho status = StatusCarrinho.ABERTO;



}
