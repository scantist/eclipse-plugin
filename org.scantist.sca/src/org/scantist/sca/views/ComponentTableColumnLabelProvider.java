package org.scantist.sca.views;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

public abstract class ComponentTableColumnLabelProvider extends StyledCellLabelProvider {
	public final int alignment;

	public final int width;

	public final static String VALUE_UNKNOWN = "UNKNOWN";

	public ComponentTableColumnLabelProvider() {
		this(200, SWT.LEFT);
	}

	public ComponentTableColumnLabelProvider(final int width, final int alignment) {
		this.alignment = alignment;
		this.width = width;
	}

	public abstract String getText(Object input);

	public abstract String getTitle();

	public Image getImage(final Object input) {
		return null;
	}

	public static Color decodeHex(final Display display, final String hexString) {
		final java.awt.Color c = java.awt.Color.decode(hexString);
		return new Color(display, c.getRed(), c.getGreen(), c.getBlue());
	}

	public TableViewerColumn addColumnTo(final TableViewer viewer) {
		ColumnViewerToolTipSupport.enableFor(viewer);
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, alignment);
		final TableColumn column = tableViewerColumn.getColumn();
		column.setMoveable(true);
		column.setResizable(true);
		column.setText(getTitle());
		column.setWidth(width);
		tableViewerColumn.setLabelProvider(this);
		return tableViewerColumn;
	}

	@Override
	public void update(final ViewerCell cell) {
		super.update(cell);
		cell.setText(getText(cell.getElement()));
		cell.setImage(getImage(cell.getElement()));
		styleCell(cell);
	}

	public void styleCell(final ViewerCell cell) {
		// Do nothing, override if you want to style the cell
	}
}
