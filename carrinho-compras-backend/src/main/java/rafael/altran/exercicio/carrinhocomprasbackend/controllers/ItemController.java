package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rafael.altran.exercicio.carrinhocomprasbackend.models.CartItem;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Item;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.ItemRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Predicate;

@RestController
@RequestMapping("/shopping/item")
@CrossOrigin("*")
public class ItemController {

    public static final String MSG_ITEM_IN_CARTS = "It is not possible remove Item because there is Carts associated to it.";

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    private ResponseEntity<Item> getItemResponseEntity(Item receivedItem, Item storedItem) {
        storedItem.setValue(receivedItem.getValue());
        storedItem.setName(receivedItem.getName());
        Item savedItem = itemRepository.save(storedItem);
        return ResponseEntity.ok().body(savedItem);
    }

    private ResponseEntity<Object> deleteUser(Long id, Item storedItem) {
        // Verify is there is any Cart associate with this item
        if (cartRepository.findAllBy()
                .flatMap(c -> c.getCartItems().stream())
                .map(CartItem::getItem)
                .anyMatch(Predicate.isEqual(storedItem))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MSG_ITEM_IN_CARTS);
        }

        itemRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public List<Item> getAll() {
        Sort sortByName = new Sort(Sort.Direction.ASC, "name");
        return itemRepository.findAll(sortByName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getById(@PathVariable("id") Long id) {
        return itemRepository.findById(id)
                .map(item -> ResponseEntity.ok().body(item))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public Item create(@Valid @RequestBody Item item) {
        item.setId(ControllerUtils.createUniqueId());
        return itemRepository.save(item);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Item> update(@PathVariable("id") Long id, @Valid @RequestBody Item item) {
        return itemRepository.findById(id)
                .map(i -> getItemResponseEntity(item, i))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return itemRepository.findById(id)
                .map(item -> deleteUser(id, item))
                .orElse(ResponseEntity.notFound().build());
    }


}
