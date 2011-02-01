/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.DialogUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class DownloadAction extends BaseSyncAction
{

	private IJobChangeListener jobListener;

	private static String MESSAGE_TITLE = StringUtil.ellipsify(Messages.DownloadAction_MessageTitle);

	protected void performAction(final IAdaptable[] files, final ISiteConnection site) throws CoreException
	{
		final Synchronizer syncer = new Synchronizer();
		Job job = new Job(MESSAGE_TITLE)
		{

			@Override
			protected IStatus run(final IProgressMonitor monitor)
			{
				monitor.subTask(StringUtil.ellipsify(Messages.BaseSyncAction_RetrievingItems));

				try
				{
					IConnectionPoint source = site.getSource();
					IConnectionPoint target = site.getDestination();
					// retrieves the root filestore of each end
					final IFileStore sourceRoot = (fSourceRoot == null) ? source.getRoot() : fSourceRoot;
					if (!target.isConnected())
					{
						target.connect(monitor);
					}
					IFileStore targetRoot = (fDestinationRoot == null) ? target.getRoot() : fDestinationRoot;
					syncer.setClientFileManager(source);
					syncer.setServerFileManager(target);
					syncer.setClientFileRoot(sourceRoot);
					syncer.setServerFileRoot(targetRoot);

					// gets the filestores of the files to be copied
					IFileStore[] fileStores = new IFileStore[files.length];
					for (int i = 0; i < fileStores.length; ++i)
					{
						fileStores[i] = SyncUtils.getFileStore(files[i]);
					}
					IFileStore[] targetFiles = SyncUtils.getDownloadFiles(source, target, fileStores, fSelectedFromSource, true, monitor);

					VirtualFileSyncPair[] items = syncer.createSyncItems(new IFileStore[0], targetFiles, monitor);

					syncer.setEventHandler(new SyncActionEventHandler(Messages.DownloadAction_MessageTitle,
							items.length, monitor, new SyncActionEventHandler.Client()
							{

								public void syncCompleted()
								{
									for (IAdaptable file : files)
									{
										if (file instanceof IResource)
										{
											try
											{
												((IResource) file).refreshLocal(IResource.DEPTH_INFINITE, monitor);
											}
											catch (CoreException e)
											{
											}
										}
									}
									if (!fSelectedFromSource)
									{
										IOUIPlugin.refreshNavigatorView(sourceRoot);
									}

									postAction(syncer);
									syncer.setEventHandler(null);
									syncer.disconnect();
								}
							}));
					syncer.download(items, monitor);
				}
				catch (OperationCanceledException e)
				{
					return Status.CANCEL_STATUS;
				}
				catch (Exception e)
				{
					SyncingUIPlugin.logError(Messages.DownloadAction_ERR_FailToDownload, e);
					return new Status(Status.ERROR, SyncingUIPlugin.PLUGIN_ID, Messages.DownloadAction_ERR_FailToDownload, e);
				}

				return Status.OK_STATUS;
			}
		};
		if (jobListener != null)
		{
			job.addJobChangeListener(jobListener);
		}
		job.setUser(true);
		job.schedule();
	}

	public void addJobListener(IJobChangeListener listener)
	{
		jobListener = listener;
	}

	protected String getMessageTitle()
	{
		return MESSAGE_TITLE;
	}

	private void postAction(final Synchronizer syncer)
	{
		getShell().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				DialogUtils.openIgnoreMessageDialogInformation(getShell(), MESSAGE_TITLE, MessageFormat.format(
						Messages.DownloadAction_PostMessage, syncer.getServerFileTransferedCount(),
						syncer.getClientDirectoryCreatedCount()), SyncingUIPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.IGNORE_DIALOG_FILE_DOWNLOAD);
			}
		});
	}
}
