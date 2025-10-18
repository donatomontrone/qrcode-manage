package com.example.qrapp.dto;

import com.example.qrapp.validator.SizeIfNotEmpty;
import com.example.qrapp.validator.UniqueEmail;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UniqueEmail
public class UserEditDTO {

  private UUID id;

  @NotBlank(message = "Inserisci il nome")
  @Size(min = 3, max = 20, message = "Il nome deve essere compreso tra 3 e 20 caratteri")
  private String firstName;

  @NotBlank(message = "Inserisci il cognome")
  @Size(min = 3, max = 20, message = "Il cognome deve essere compreso tra 3 e 20 caratteri")
  private String lastName;

  @NotBlank(message = "Inserisci la mail")
  @Email(message = "Il formato della email non è corretto")
  @Size(min = 8, max = 30, message = "La mail deve essere compresa tra 8 e 30 caratteri")
  @Column(unique = true)
  private String email;

  @SizeIfNotEmpty(min = 6, max = 100, message = "La password deve avere minimo 6 caratteri")
  private String password;

  @SizeIfNotEmpty(min = 6, max = 100, message = "La password deve avere minimo 6 caratteri")
  private String confirmPassword;
}
