package org.jenkinsci.plugins.sharedworkspace;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Environment;
import hudson.model.Run;
import hudson.model.listeners.RunListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Extension
public class PushWorkspaceURL extends RunListener<AbstractBuild> {

	@Override
	public Environment setUpEnvironment(AbstractBuild build, Launcher launcher, BuildListener listener)
		throws IOException, InterruptedException, Run.RunnerAbortedException
	{
		Map<String, String> map = new HashMap<String, String>();
		SharedWorkspace sw = (SharedWorkspace) build.getProject().getProperty(SharedWorkspace.class);

		if(sw != null && sw.getUrl() != null)
			map.put("SHAREDSPACE_SCM_URL", sw.getUrl());

		EnvVars envVars = new EnvVars(map);
		Environment env = Environment.create(envVars);
		return env;
	}
}
