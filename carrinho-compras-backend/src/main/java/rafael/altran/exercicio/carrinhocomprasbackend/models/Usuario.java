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
@Document(collection="usuarios")
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    private Long id;

    @NotBlank
    @Size(max=100)
    @Email(message = "Email Inválido")
    @Indexed(unique=true)
    private String title;

    @NotBlank
    @Size(max=100)
    private String nome;



}
