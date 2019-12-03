package rafael.altran.exercicio.carrinhocomprasbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Carrinho;

@Repository
public interface CarrinhoRepository extends MongoRepository<Carrinho, Long> {
}
