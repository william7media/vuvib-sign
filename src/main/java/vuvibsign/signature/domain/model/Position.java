package vuvibsign.signature.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Position implements Serializable {

    @NonNull
    @JsonProperty
    private Integer page;
    @NonNull
    @JsonProperty
    private Integer lowLeftX;
    @NonNull
    @JsonProperty
    private Integer lowLeftY;
    @NonNull
    @JsonProperty
    private Integer upperRightX;
    @NonNull
    @JsonProperty
    private Integer upperRightY;

}
