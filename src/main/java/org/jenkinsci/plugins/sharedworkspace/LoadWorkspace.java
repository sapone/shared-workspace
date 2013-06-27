package org.jenkinsci.plugins.sharedworkspace;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import jenkins.slaves.WorkspaceLocator;
import hudson.model.Node;
import java.io.File;
import java.util.Collection;

@Extension
public class LoadWorkspace extends WorkspaceLocator {
	static final String SW_DIR = "sharedspace";

	@Override
	public FilePath locate (TopLevelItem item, Node node) {
		Collection<? extends Job> jobs = item.getAllJobs();

		for(Job j: jobs) {
			SharedWorkspace sw = (SharedWorkspace) j.getProperty(SharedWorkspace.class);
			if (sw != null) {
				String name = sw.getName();
				if(name!=null && !name.equals("NONE") && !name.equals("null")) {
					FilePath fp=node.getRootPath();
					if(fp!=null)
						fp = new FilePath(fp, SW_DIR);
					else
						return null;
					fp = new FilePath(fp, name);
					return fp;
				}
			}
		}
		return null;
	}
}
