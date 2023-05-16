package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
        //넘어오는 clazz라는 객체가 Item에 지원이 되는지
        // item == clazz
        // item == item의 자식클래스
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        if(StringUtils.isEmpty(item.getItemName())){
            errors.rejectValue("itemName","required");


            //bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false , new String[]{"required.item.itemName"}, null, "상품명을 입력해주세요."));
        }
        if(item.getPrice() == null || item.getPrice() <1000 || item.getPrice() >= 1000000){
            errors.rejectValue("price","range",new Object[]{1000,1000000},null);
            //bindingResult.addError(new FieldError("item","price",item.getPrice(), false, new String[]{"range.item.price"},new Object[]{1000,1000000},"상품 금액은 1000원 ~  1,000,000원 까지만 입력이 가능합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            errors.rejectValue("quantity","max",new Object[]{10000},null);
            //bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{10000},"수량은 최대 9999개 까지 가능합니다."));
        }

        if(item.getPrice() !=null && item.getQuantity() != null  ) {
            int result = item.getPrice() * item.getQuantity();
            if(result <10000)
                errors.reject("totalPriceMin",new Object[]{10000,result},null);
            //bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,result},"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + result));
        }
    }
}
