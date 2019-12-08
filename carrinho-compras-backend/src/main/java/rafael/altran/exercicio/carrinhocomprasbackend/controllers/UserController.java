package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rafael.altran.exercicio.carrinhocomprasbackend.models.User;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shopping/user")
@CrossOrigin("*")
public class UserController {

    public static final String MSG_USER_IN_CARTS = "It is not possible remove User because there are Carts associated to him.";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    private ResponseEntity<User> getUserResponseEntity(User receivedUser, User storedUser) {
        storedUser.setEmail(receivedUser.getEmail());
        storedUser.setName(receivedUser.getName());
        User savedUser = userRepository.save(storedUser);
        return ResponseEntity.ok().body(savedUser);
    }

    private ResponseEntity<Object> deleteUser(Long id, User storedUser) {
        if (cartRepository.countByUser(storedUser) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_USER_IN_CARTS);
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public List<User> getAll() {
        Sort sortByEmail = new Sort(Sort.Direction.ASC, "email");
        return userRepository.findAll(sortByEmail);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        try {
            user.setId(ControllerUtils.createUniqueId());
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok().body(savedUser);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail already registered", e);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id, @Valid @RequestBody User user) {
        try {
            return userRepository.findById(id)
                    .map(u -> getUserResponseEntity(user, u))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail already registered to another User", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .map(user -> deleteUser(id, user))
                .orElse(ResponseEntity.notFound().build());
    }

}
