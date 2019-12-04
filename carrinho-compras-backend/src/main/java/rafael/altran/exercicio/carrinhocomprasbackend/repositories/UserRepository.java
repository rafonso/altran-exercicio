package rafael.altran.exercicio.carrinhocomprasbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rafael.altran.exercicio.carrinhocomprasbackend.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, Long> {
}
