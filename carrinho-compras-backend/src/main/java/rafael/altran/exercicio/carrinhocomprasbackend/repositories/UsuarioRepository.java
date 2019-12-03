package rafael.altran.exercicio.carrinhocomprasbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, Long> {
}
