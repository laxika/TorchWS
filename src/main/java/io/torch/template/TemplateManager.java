package io.torch.template;

import freemarker.template.TemplateException;
import java.io.IOException;

public interface TemplateManager {

    public boolean isTemplateExist(String templateName) throws IOException;

    public String processTemplate(String templateName, Object templateData) throws TemplateException, IOException;
}
