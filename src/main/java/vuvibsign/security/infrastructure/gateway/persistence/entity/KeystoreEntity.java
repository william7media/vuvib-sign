package vuvibsign.security.infrastructure.gateway.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username")
        })
public class KeystoreEntity {

    @Id
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String username;
    private String keystoreName;
    private String keystoreUrl;
    private String keystorePass;

}
