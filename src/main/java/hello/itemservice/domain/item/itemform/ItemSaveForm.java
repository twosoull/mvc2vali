package hello.itemservice.domain.item.itemform;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemSaveForm {

    @NotBlank
    private String itemName;

    @NotNull
    @Range(max = 100000, min=10000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;

}
