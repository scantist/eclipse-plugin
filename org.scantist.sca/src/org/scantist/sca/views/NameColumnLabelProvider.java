package org.scantist.sca.views;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.scantist.sca.model.*;

public class NameColumnLabelProvider extends ComponentTableColumnLabelProvider {
    @Override
    public String getText(final Object input) {
        if (input instanceof ComponentModel) {
            final String name = ((ComponentModel) input).getName();
            final String version = ((ComponentModel) input).getVersion();
            final String text = name + ":" + version;
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
    }

    @Override
    public String getTitle() {
        return "Component";
    }

    @Override
    public void styleCell(final ViewerCell cell) {
        final String[] compChunks = cell.getText().split(":");
        cell.setText(String.format("%s  %s ", compChunks[0], compChunks[1]));
        final Display display = Display.getCurrent();
        final Color versionColor = decodeHex(display, "#285F8F");
        final Color backgroundColor = decodeHex(display, "#fafafa");
        final Color borderColor = decodeHex(display, "#dddddd");
        final StyleRange versionStyle = new StyleRange(compChunks[0].length() + 1, compChunks[1].length() + 2, versionColor, backgroundColor);
        versionStyle.borderStyle = SWT.BORDER_SOLID;
        versionStyle.borderColor = borderColor;
        final int versionHeight = (int) (cell.getFont().getFontData()[0].getHeight() * 0.85);
        versionStyle.font = FontDescriptor.createFrom(cell.getFont()).setHeight(versionHeight).createFont(display);
        cell.setStyleRanges(new StyleRange[] { versionStyle });
    }

}
