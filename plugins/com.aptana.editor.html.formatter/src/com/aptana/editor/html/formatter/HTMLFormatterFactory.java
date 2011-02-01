/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.html.HTMLSourceConfiguration;
import com.aptana.editor.html.HTMLSourceViewerConfiguration;
import com.aptana.editor.html.formatter.preferences.HTMLFormatterModifyDialog;
import com.aptana.formatter.AbstractScriptFormatterFactory;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.preferences.PreferenceKey;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.IFormatterModifyDialogOwner;

/**
 * HTML formatter factory
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterFactory extends AbstractScriptFormatterFactory
{

	private static final PreferenceKey FORMATTER_PREF_KEY = new PreferenceKey(HTMLFormatterPlugin.PLUGIN_ID,
			HTMLFormatterConstants.FORMATTER_ID);

	private static final String FORMATTER_PREVIEW_FILE = "formatterPreview.html"; //$NON-NLS-1$

	private static final String[] KEYS = {
			// TODO - Add more...
			HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE, HTMLFormatterConstants.FORMATTER_TAB_CHAR,
			HTMLFormatterConstants.FORMATTER_TAB_SIZE, HTMLFormatterConstants.WRAP_COMMENTS,
			HTMLFormatterConstants.WRAP_COMMENTS_LENGTH, HTMLFormatterConstants.INDENT_EXCLUDED_TAGS,
			HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, HTMLFormatterConstants.LINES_AFTER_ELEMENTS,
			HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS,
			HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, HTMLFormatterConstants.PRESERVED_LINES };

	public PreferenceKey[] getPreferenceKeys()
	{
		final PreferenceKey[] result = new PreferenceKey[KEYS.length];
		for (int i = 0; i < KEYS.length; ++i)
		{
			final String key = KEYS[i];
			result[i] = new PreferenceKey(HTMLFormatterPlugin.PLUGIN_ID, key);
		}
		return result;
	}

	public IScriptFormatter createFormatter(String lineSeparator, Map<String, String> preferences)
	{
		return new HTMLFormatter(lineSeparator, preferences, getMainContentType());
	}

	public URL getPreviewContent()
	{
		return getClass().getResource(FORMATTER_PREVIEW_FILE);
	}

	public IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner)
	{
		return new HTMLFormatterModifyDialog(dialogOwner, this);
	}

	public SourceViewerConfiguration createSimpleSourceViewerConfiguration(ISharedTextColors colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor, boolean configureFormatter)
	{
		return new HTMLSourceViewerConfiguration(preferenceStore, (AbstractThemeableEditor) editor);
	}

	public PreferenceKey getFormatterPreferenceKey()
	{
		return FORMATTER_PREF_KEY;
	}

	public IPreferenceStore getPreferenceStore()
	{
		return HTMLFormatterPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatterFactory#getPartitioningConfiguration()
	 */
	public Object getPartitioningConfiguration()
	{
		return HTMLSourceConfiguration.getDefault();
	}
}
