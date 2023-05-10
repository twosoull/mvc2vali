package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes, Model model) {

        //검증로직
        Map<String,String> error = new HashMap<String,String>();

        //필드 검증
        if(StringUtils.isEmpty(item.getItemName())){
            error.put("itemName","상품명을 입력해주세요.");
        }
        if(item.getPrice() == null || item.getPrice() <1000 || item.getPrice() >= 1000000){
            error.put("price","상품 금액은 1000원 ~  1,000,000원 까지만 입력이 가능합니다.");
        }
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            error.put("quantity","수량은 최대 9999개 까지 가능합니다.");
        }

        if(item.getPrice() !=null && item.getQuantity() != null  ) {
            int result = item.getPrice() * item.getQuantity();
            if(result <10000)
            error.put("global", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + result);
        }
        //타입 검증

        if(!error.isEmpty()){
            model.addAttribute("error",error);
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
