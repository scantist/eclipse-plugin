package org.scantist.sca.views;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.scantist.sca.model.*;

public class ComponentTableContentProvider implements ILazyContentProvider {
	private final TableViewer viewer;

	private String inputProject;

	private ComponentModel[] parsedElements;


	public ComponentTableContentProvider(final TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		if (newInput == null) {
			this.parsedElements = new ComponentModel[] {};
		} else {
			this.parsedElements = (ComponentModel[]) newInput;
		}
	}

	public String getInputProject() {
		return inputProject;
	}

	@Override
	public void updateElement(final int index) {
		// TODO: See if there's a graceful way around "Ignored reentrant call while viewer is busy"
		if (parsedElements.length > 0) {
			viewer.setItemCount(parsedElements.length);
			viewer.replace(parsedElements[index], index);
		}
	}

}