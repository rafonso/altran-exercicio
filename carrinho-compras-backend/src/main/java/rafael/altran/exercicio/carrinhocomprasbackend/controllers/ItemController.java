package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rafael.altran.exercicio.carrinhocomprasbackend.models.Item;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.ItemRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/shopping/item")
@CrossOrigin("*")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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
        item.setId(System.currentTimeMillis());
        return itemRepository.save(item);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Item> update(@PathVariable("id") Long id, @Valid @RequestBody Item item) {
        return itemRepository.findById(id)
                .map(i -> {
                    i.setValue(item.getValue());
                    i.setName(item.getName());
                    Item savedItem = itemRepository.save(i);
                    return ResponseEntity.ok().body(savedItem);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return itemRepository.findById(id)
                .map(item -> {
                    itemRepository.deleteById(id);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

}
