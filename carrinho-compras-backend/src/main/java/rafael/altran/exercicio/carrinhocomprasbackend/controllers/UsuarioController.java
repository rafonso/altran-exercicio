package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Usuario;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UsuarioRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/carrinho/usuario")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public List<Usuario> getAll() {
        Sort sortByEmail = new Sort(Sort.Direction.ASC, "email");
        return usuarioRepository.findAll(sortByEmail);
    }

    @PostMapping("/")
    public Usuario create(@Valid @RequestBody Usuario usuario) {
        try {
            usuario.setId(System.currentTimeMillis());
            return usuarioRepository.save(usuario);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadasrado", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable("id") Long id) {
        return usuarioRepository.findById(id)
                .map(todo -> ResponseEntity.ok().body(todo))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Usuario> update(@PathVariable("id") Long id, @Valid @RequestBody Usuario usuario) {
        try {
            return usuarioRepository.findById(id)
                    .map(u -> {
                        u.setEmail(usuario.getEmail());
                        u.setNome(usuario.getNome());
                        Usuario uSalvo = usuarioRepository.save(u);
                        return ResponseEntity.ok().body(uSalvo);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadasrado para outro usuário", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable("id") Long id) {
        return usuarioRepository.findById(id)
                .map(todo -> {
                    usuarioRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
