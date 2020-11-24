package priv.rdo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) //needed, coz records are not yet fully supported by jackson
public record Book(String titleweb, String author, String isbn) {

}
