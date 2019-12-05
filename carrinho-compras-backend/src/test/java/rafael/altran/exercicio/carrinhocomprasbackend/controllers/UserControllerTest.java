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
import rafael.altran.exercicio.carrinhocomprasbackend.models.User;
import rafael.altran.exercicio.carrinhocomprasbackend.repositories.UserRepository;

import java.util.Arrays;
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

    private List<User> storedUsers = Arrays.asList(
            new User(10L, "steve.rogers@avengers.com", "Captain America"),
            new User(20L, "peter.parker@marvel.com", "Spider Man"),
            new User(30L, "arnold.schwarznegger@rambo.com", "Terminator")
    );

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        storedUsers.forEach(user -> userRepository.save(user));
    }

    @AfterEach
    public void cleanData() {
        userRepository.deleteAll();
    }

    // getAll() - START

    @Test
    @DisplayName("Find All sorted by Email")
    public void getAllNoResults() throws Exception {
        mvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(storedUsers.get(2).getId().intValue())))
                .andExpect(jsonPath("$[1].id", is(storedUsers.get(1).getId().intValue())))
                .andExpect(jsonPath("$[2].id", is(storedUsers.get(0).getId().intValue())));
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
        Long id = storedUsers.get(0).getId();

        mvc.perform(get(URL + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(storedUsers.get(0).getId().intValue())))
                .andExpect(jsonPath("$.email", is(storedUsers.get(0).getEmail())))
                .andExpect(jsonPath("$.name", is(storedUsers.get(0).getName())));
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
        String email = storedUsers.get(0).getEmail();
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
        Long id = storedUsers.get(0).getId();
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
        Long id = storedUsers.get(0).getId();
        String email = storedUsers.get(2).getEmail();
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Update with Present User Id filled, valid Properties")
    public void updateWithPresentUserIdValidProperties() throws Exception {
        Long id = storedUsers.get(0).getId();
        String email = "foo@email.com";
        String name = "Foo Boo";
        String json = String.format(JSON_FORMAT, email, name);

        MvcResult result = mvc.perform(put(URL + id).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        User newUser = objectMapper.readValue(contentAsString, User.class);

        assertEquals(storedUsers.get(0).getId(), newUser.getId());
        assertEquals(email, newUser.getEmail());
        assertEquals(name, newUser.getName());
    }

    // update() - END

    // delete() - START

    @Test
    @DisplayName("Delete without User Id")
    public void deleteWithoutUserId() throws Exception {
        mvc.perform(delete(URL)) // .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete with Not Present User Id")
    public void deleteWithNotPresentId() throws Exception {
        mvc.perform(delete(URL + 2))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Delete with  Present User Id")
    public void deleteWithPresentId() throws Exception {
        mvc.perform(delete(URL + storedUsers.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        mvc.perform(get(URL + storedUsers.get(0).getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // delete() - END

}

