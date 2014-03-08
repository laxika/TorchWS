package io.torch.template;

import io.torch.controller.WebPage;
import java.lang.reflect.Field;

public class TemplateRootLocator {

    public Field locateTemplateRoot(WebPage webpage) {
        for (Field field : webpage.getClass().getFields()) {
            if (field.getAnnotation(TemplateRoot.class) != null) {
                return field;
            }
        }

        return null;
    }
}
