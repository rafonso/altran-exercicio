package rafael.altran.exercicio.carrinhocomprasbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rafael.altran.exercicio.carrinhocomprasbackend.models.CartItem;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, Long> {
}
