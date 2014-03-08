package io.torch.template;

import io.torch.controller.WebPage;
import java.lang.reflect.Field;

public class TemplateRootLocator {

    public Object locateTemplateRoot(WebPage webpage) throws IllegalArgumentException, IllegalAccessException {
        System.out.println(webpage.getClass());
        System.out.println(webpage.getClass().getFields().length);
        
        for (Field field : webpage.getClass().getFields()) {
                System.out.println(field.getName());
            if (field.getAnnotation(TemplateRoot.class) != null) {
                System.out.println("FOUND THE ROOT");
                return field.get(webpage);
            }
        }

        return null;
    }
}
