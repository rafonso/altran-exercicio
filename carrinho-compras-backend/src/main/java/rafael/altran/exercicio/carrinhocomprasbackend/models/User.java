package rafael.altran.exercicio.carrinhocomprasbackend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Document(collection="users")
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    private Long id;

    @NotBlank
    @Size(max=100)
    @Email(message = "Invalid Email")
    @Indexed(unique=true)
    private String email;

    @NotBlank
    @Size(max=100)
    private String name;



}