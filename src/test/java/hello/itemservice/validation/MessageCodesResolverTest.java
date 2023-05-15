package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;

import static org.assertj.core.api.Assertions.*;

public class MessageCodesResolverTest {

    MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required","item");

        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
        }
        /*
        * 결과 :
        * messageCode = required.item
          messageCode = required
          * 해당 결과는
          * new ObjectError("item",new String[]{"required.item","required"})
          * 2번에 나오는 것을 MessageCodesResolver가 찾아준다.
        * */
        assertThat(messageCodes).containsExactly("required.item","required");
        //검증시 사용

    }

    @Test
    void messageCodesResolverField(){
        String[] messageCodes = messageCodesResolver.resolveMessageCodes("required","item","itemName",String.class);
        for (String messageCode : messageCodes) {
            System.out.println("messageCode = " + messageCode);
            /*
            * 결과:
            * messageCode = required.item.itemName
                messageCode = required.itemName
                messageCode = required.java.lang.String
                messageCode = required
                * 디테일한 순서에서부터 내려간다.
            * */
            //BindingResult.rejectValue("itemName","required");
            //BindingResult의 rejectValue는 내부적으로 이미 MessageCodesResolver를 사용한다. 그래서 itemName과
            //errors.properties의 가장 앞의 required를 읽어내린다

            new FieldError("item","itemName",null,false,messageCodes,null,null);
            //rejectValue안에서 실제로 이런식으로 실제로 5번째 단에서 new String[]{"required.item.itemName"}여야 하는 부분을 MessageCodesResolver가 넘겨준다.

            assertThat(messageCodes).containsExactly(
                    "required.item.itemName",
                    "required.itemName",
                    "required.java.lang.String",
                    "required"
            );
        }
    }
}
