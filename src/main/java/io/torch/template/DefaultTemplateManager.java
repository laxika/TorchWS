package io.torch.template;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultTemplateManager implements TemplateManager {

    Configuration cfg = new Configuration();

    public DefaultTemplateManager() {
        try {
            // Specify the data source where the template files come from. Here I set a
            // plain directory for it, but non-file-system are possible too:
            cfg.setDirectoryForTemplateLoading(new File("templates"));

            // Specify how templates will see the data-model. This is an advanced topic...
            // for now just use this:
            cfg.setObjectWrapper(new DefaultObjectWrapper());

            // Set your preferred charset template files are stored in. UTF-8 is
            // a good choice in most applications:
            cfg.setDefaultEncoding("UTF-8");

            // Sets how errors will appear. Here we assume we are developing HTML pages.
            // For production systems TemplateExceptionHandler.RETHROW_HANDLER is better.
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

            // At least in new projects, specify that you want the fixes that aren't
            // 100% backward compatible too (these are very low-risk changes as far as the
            // 1st and 2nd version number remains):
            cfg.setIncompatibleImprovements(new Version(2, 3, 20));  // FreeMarker 2.3.20
        } catch (IOException ex) {
            Logger.getLogger(TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isTemplateExist(String templateName) throws IOException {
        return cfg.getTemplateLoader().findTemplateSource(templateName) != null;
    }

    @Override
    public String processTemplate(String templateName, Object templateData) throws TemplateException, IOException {
        StringWriter templateText = new StringWriter();

        cfg.getTemplate(templateName).process(templateData, templateText);

        return templateText.toString();

    }
}
