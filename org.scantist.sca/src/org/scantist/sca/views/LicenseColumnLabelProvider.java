package org.scantist.sca.views;

import org.scantist.sca.model.*;

public class LicenseColumnLabelProvider extends ComponentTableColumnLabelProvider {
    public LicenseColumnLabelProvider(final int width, final int style) {
        super(width, style);
    }

    @Override
    public String getText(final Object input) {
        final ComponentModel model = (ComponentModel) input;
        return model.getLicense();
    }

    @Override
    public String getTitle() {
        return "License";
    }

}
