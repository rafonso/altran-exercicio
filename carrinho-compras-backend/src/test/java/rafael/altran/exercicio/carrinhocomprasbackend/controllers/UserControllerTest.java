package rafael.altran.exercicio.carrinhocomprasbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rafael.altran.exercicio.carrinhocomprasbackend.models.*;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.CartRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.ItemRepository;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Based on  https://www.baeldung.com/spring-boot-testing
 * https://stackoverflow.com/questions/49530149/using-embedded-mongodb-in-spring-junit-webmvctest
 * https://github.com/zak905/mongo-spring-test-demo/blob/master/src/test/java/com/gwidgets/mongotest/TransactionRepositoryTest.java
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureDataMongo
@ActiveProfiles("test")
public class UserControllerTest {

    private static final String JSON_FORMAT = "{\"email\":\"%s\",\"name\":\"%s\"}";
    private static final String URL = "/shopping/user/";

    private final User captainAmerica = new User(10L, "steve.rogers@avengers.com", "Captain America");
    private final User spiderMan = new User(20L, "peter.parker@marvel.com", "Spider Man");
    private final User terminator = new User(30L, "arnold.schwarznegger@rambo.com", "Terminator");
    private List<User> storedUsers = Arrays.asList(
            captainAmerica,
            spiderMan,
            terminator
    );

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        storedUsers.forEach(user -> userRepository.save(user));
    }

    @AfterEach
    public void cleanData() {
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    // getAll() - START

    @Test
    @DisplayName("Find All sorted by Email")
    public void getAllNoResults() throws Exception {
        mvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(terminator.getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(spiderMan.getId().intValue())))
                .andExpect(jsonPath("$[2].id", is(captainAmerica.getId().intValue())));
    }

    // getAll() - END

    // getById() - START

    @Test
    @DisplayName("Find By Id With ID not Present")
    public void getByIdNotPresent() throws Exception {
        long id = 80L;

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Find By Id With ID Present")
    public void getByIdPresent() throws Exception {
        Long id = captainAmerica.getId();

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(captainAmerica.getId().intValue())))
                .andExpect(jsonPath("$.email", is(captainAmerica.getEmail())))
                .andExpect(jsonPath("$.name", is(captainAmerica.getName())));
    }

    // getById() - END

    // create() - START

    @Test
    @DisplayName("Create With empty string")
    public void createWithNullJson() throws Exception {
        String json = "";

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Empty JSON")
    public void createWithEmptyJson() throws Exception {
        String json = "{}";

        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Empty Properties")
    public void createWithJsonEmptyProperties() throws Exception {
        String email = "";
        String name = "";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Invalid email, filled Name")
    public void createWithInvalidNameFilledName() throws Exception {
        String email = "new.useremailcom";
        String name = "New User";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Repeated email, filled Name")
    public void createWithRepeatedEmailFilledName() throws Exception {
        String email = captainAmerica.getEmail();
        String name = "New User";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Create With Valid Properties")
    public void createWithJsonValidProperties() throws Exception {
        String email = "new.user@email.com";
        String name = "New User";
        String json = String.format(JSON_FORMAT, email, name);

        MvcResult result = mvc.perform(post(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        User newUser = objectMapper.readValue(contentAsString, User.class);

        assertTrue(newUser.getId() > 0);
        assertEquals(email, newUser.getEmail());
        assertEquals(name, newUser.getName());
    }

    // create() - END

    // update() - START

    @Test
    @DisplayName("Update without User Id")
    public void updateWithoutUserId() throws Exception {
        String email = "";
        String name = "";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with User Id filled, empty Properties")
    public void updateWithFilledUserIdEmptyProperties() throws Exception {
        long id = 2L;
        String email = "";
        String name = "";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with not present User Id filled, valid Properties")
    public void updateWithNotPresentUserIdValidProperties() throws Exception {
        long id = 2L;
        String email = "foo@email.com";
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Present User Id filled, invalid email")
    public void updateWithPresentUserIdInvalidEmail() throws Exception {
        Long id = captainAmerica.getId();
        String email = "fooemailcom";
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Present User Id filled, Another User email")
    public void updateWithPresentUserIdAnotherUserEmail() throws Exception {
        Long id = captainAmerica.getId();
        String email = terminator.getEmail();
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Present User Id filled, valid Properties")
    public void updateWithPresentUserIdValidProperties() throws Exception {
        Long id = captainAmerica.getId();
        String email = "foo@email.com";
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        MvcResult result = mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        User newUser = objectMapper.readValue(contentAsString, User.class);

        assertEquals(captainAmerica.getId(), newUser.getId());
        assertEquals(email, newUser.getEmail());
        assertEquals(name, newUser.getName());
    }

    // update() - END

    // delete() - START

    @Test
    @DisplayName("Delete Without Id")
    public void deleteWithoutUserId() throws Exception {
        mvc.perform(delete(URL)) // .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete User not Present")
    public void deleteWithNotPresentId() throws Exception {
        mvc.perform(delete(URL + 2))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete User Present")
    public void deleteWithPresentId() throws Exception {
        mvc.perform(delete(URL + captainAmerica.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mvc.perform(get(URL + captainAmerica.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete User associate with cart")
    public void deleteUserWithCartAssociate() throws Exception {
        Item i = itemRepository.save(new Item(1_000L, "User Item", BigDecimal.TEN));
        CartItem ci = cartItemRepository.save(new CartItem(2_000L, i, 1));
        Cart c = cartRepository.save(new Cart(10000L, captainAmerica, Collections.singletonList(ci), CartStatus.OPEN));

        mvc.perform(delete(URL + captainAmerica.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(UserController.MSG_USER_IN_CARTS));
    }

    // delete() - END

}

