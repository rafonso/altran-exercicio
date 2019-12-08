package rafael.altran.exercicio.carrinhocomprasbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Cart;
import rafael.altran.exercicio.carrinhocomprasbackend.models.User;

import java.util.stream.Stream;

@Repository
public interface CartRepository extends MongoRepository<Cart, Long> {

    long countByUser(User user);

    Stream<Cart> findAllBy();

}
