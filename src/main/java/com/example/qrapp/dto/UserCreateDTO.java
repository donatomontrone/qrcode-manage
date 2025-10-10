package com.example.qrapp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {

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

  @NotBlank(message = "Inserisci la password")
  @Size(min = 6, max = 100, message = "La password deve avere minimo 6 caratteri")
  private String password;

  private String confirmPassword;
}
