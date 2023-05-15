package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }
    // 프로퍼티로 만들기 배열로 넣는 이유

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //주의할 점은 BindingResult는 @ModelAttribute를 붙인 오브젝트 뒤에 오는 것으로 순서가 중요하다.
        //필드 검증
        if(StringUtils.isEmpty(item.getItemName())){
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false , null, null, "상품명을 입력해주세요."));
            //3번의 field값을 넣으면 오류가 발생했을 때에 오류값을 html에서 표시해 줄 수 있게 모델에 넣어준다. 필드의 타입이 Integer인데 문자열이 들어가도 넣어준다. 그 이유는 item에 넣기 이전에 아닌 리퀘스트에서 넣어준다고 생각하면 된다.
            //bindingResult.addError(new FieldError("item","itemName","상품명을 입력해주세요."));
        }
        if(item.getPrice() == null || item.getPrice() <1000 || item.getPrice() >= 1000000){
            bindingResult.addError(new FieldError("item","price",item.getPrice(), false, null,null,"상품 금액은 1000원 ~  1,000,000원 까지만 입력이 가능합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,null,null,"수량은 최대 9999개 까지 가능합니다."));
        }

        if(item.getPrice() !=null && item.getQuantity() != null  ) {
            int result = item.getPrice() * item.getQuantity();
            if(result <10000)
                bindingResult.addError(new ObjectError("item",null,null,"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + result));
        }
        //타입 검증

        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //주의할 점은 BindingResult는 @ModelAttribute를 붙인 오브젝트 뒤에 오는 것으로 순서가 중요하다.

        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult,"itemName","required");
        //공백같은 다순한 기능만 제공, 아래의 코드와 동일한 역할을 한다

        //필드 검증
        /*
        if(StringUtils.isEmpty(item.getItemName())){
            bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false , new String[]{"required.item.itemName"}, null, "상품명을 입력해주세요."));
            //3번의 field값을 넣으면 오류가 발생했을 때에 오류값을 html에서 표시해 줄 수 있게 모델에 넣어준다. 필드의 타입이 Integer인데 문자열이 들어가도 넣어준다. 그 이유는 item에 넣기 이전에 아닌 리퀘스트에서 넣어준다고 생각하면 된다.
            //bindingResult.addError(new FieldError("item","itemName","상품명을 입력해주세요."));
        }
        */

        if(item.getPrice() == null || item.getPrice() <1000 || item.getPrice() >= 1000000){
            bindingResult.addError(new FieldError("item","price",item.getPrice(), false, new String[]{"range.item.price"},new Object[]{1000,1000000},"상품 금액은 1000원 ~  1,000,000원 까지만 입력이 가능합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{10000},"수량은 최대 9999개 까지 가능합니다."));
        }

        if(item.getPrice() !=null && item.getQuantity() != null  ) {
            int result = item.getPrice() * item.getQuantity();
            if(result <10000)
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,result},"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + result));
        }
        //타입 검증

        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //주의할 점은 BindingResult는 @ModelAttribute를 붙인 오브젝트 뒤에 오는 것으로 순서가 중요하다.
        //필드 검증
        log.info("objectName = {}", bindingResult.getObjectName());
        log.info("object = {}", bindingResult.getTarget()); //item.toString 사용

        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v2/addForm";
        }

        // 로그를 돌려보면
        //objectName = item
        //object = Item(id=null, itemName=, price=1, quantity=2)
        //BindingResult는 이미 item이 무엇인지 알고 있게 된다. 그러므로 순서가 중요하다. 그래서 아래의 코드가 가능해진다.
        if(StringUtils.isEmpty(item.getItemName())){
            bindingResult.rejectValue("itemName","required");
            //rejectValue는 필드값,에러코드 순으로 이루어져있다.
            //required.item.itemName=상품 이름은 필수입니다.
            //값이 있는데 단순하게 item.itemName을 따라가는 것이 아닌
            //만약 required=필수 오류입니다. 만 있을 경우 required 값만 노출 시키지만
            // 프로퍼티에 required.오브젝트.필드명 이 있을 경우 더욱 상세할 수 있게 프로퍼티 키값을 찾아준다.

            //bindingResult.addError(new FieldError("item","itemName", item.getItemName(), false , new String[]{"required.item.itemName"}, null, "상품명을 입력해주세요."));
        }
        if(item.getPrice() == null || item.getPrice() <1000 || item.getPrice() >= 1000000){
            bindingResult.rejectValue("price","range",new Object[]{1000,1000000},null);
            //bindingResult.addError(new FieldError("item","price",item.getPrice(), false, new String[]{"range.item.price"},new Object[]{1000,1000000},"상품 금액은 1000원 ~  1,000,000원 까지만 입력이 가능합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            bindingResult.rejectValue("quantity","max",new Object[]{10000},null);
            //bindingResult.addError(new FieldError("item","quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{10000},"수량은 최대 9999개 까지 가능합니다."));
        }

        if(item.getPrice() !=null && item.getQuantity() != null  ) {
            int result = item.getPrice() * item.getQuantity();
            if(result <10000)
                bindingResult.reject("totalPriceMin",new Object[]{10000,result},null);
                //bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"},new Object[]{10000,result},"가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + result));
        }
        //타입 검증

        if(bindingResult.hasErrors()){
            log.info("errors={}",bindingResult);
            return "validation/v2/addForm";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

