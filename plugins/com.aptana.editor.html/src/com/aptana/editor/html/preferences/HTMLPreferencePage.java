/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.html.HTMLEditor;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.ui.preferences.AptanaPreferencePage;

public class HTMLPreferencePage extends CommonEditorPreferencePage
{
	/**
	 * HTMLPreferencePage
	 */
	public HTMLPreferencePage()
	{
		super();
		setDescription(Messages.HTMLPreferencePage_LBL_Description);
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createMarkOccurrenceOptions(Composite parent)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		super.createFieldEditors();

		Composite group = AptanaPreferencePage.createGroup(getFieldEditorParent(),
				Messages.HTMLPreferencePage_ContentAssistLabel);
		FieldEditor closingTag = new BooleanFieldEditor(IPreferenceContants.HTML_AUTO_CLOSE_TAG_PAIRS,
				Messages.HTMLPreferencePage_AutoInsertCloseTagLabel, group);

		addField(closingTag);
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return new InstanceScope().getNode(HTMLPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return HTMLEditor.getChainedPreferenceStore();
	}
}
