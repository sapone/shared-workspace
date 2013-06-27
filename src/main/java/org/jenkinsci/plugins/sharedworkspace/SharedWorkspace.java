package org.jenkinsci.plugins.sharedworkspace;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SharedWorkspace extends JobProperty<AbstractProject<?, ?>> {
	private String name;
	private String url;

	@DataBoundConstructor
	public SharedWorkspace(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		if(name != null)
			return name;
		return "NONE";
	}

	public String getUrl() {
		if(url != null)
			return url;
		return "NONE";
	}

	public void setName(String str) {
		name=str;
	}

	public void setUrl(String str) {
		url=str;
	}

	@Override
	public JobProperty<?> reconfigure(StaplerRequest req, JSONObject formData)
		throws Descriptor.FormException
	{	
		formData.put("url", getDescriptor().getUrl(formData.getString("name")));
		return super.reconfigure(req, formData);
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends JobPropertyDescriptor {

		public DescriptorImpl() {
			load();
		}

		List<SharedWorkspace> workspaces = new LinkedList<SharedWorkspace>();

		public List<SharedWorkspace> getWorkspaces() {
			return workspaces;
		}

		public void setWorkspaces(List<SharedWorkspace> val) {
			workspaces = val;
		}

		public String getUrl(String name) {
			for (SharedWorkspace sw : getWorkspaces())
				if(sw.name.equals(name))
				return sw.url;
			return null;
  		}

		public FormValidation doCheckName(@QueryParameter("name") final String nvalue, @QueryParameter("url") final String uvalue)
			throws IOException, ServletException
		{
			//on job config page we have only name field, url == null
			if (uvalue == null)
				return FormValidation.ok();
			if (nvalue.length() == 0 && uvalue.length() == 0)
				return FormValidation.ok();
			if (nvalue.length() == 0 && uvalue.length() != 0)
				return FormValidation.error("Please set a workspace name");
			if (!nvalue.matches("([a-zA-Z0-9_()\\[\\]\\-\\.\\ ])*[a-zA-Z0-9_()\\[\\]\\-]"))
				return FormValidation.warning("Be careful with special characters, this gonna be a directory name.");
			if (nvalue.length() < 2)
				return FormValidation.warning("Isn't the name too short?");
			return FormValidation.ok();
		}

		public FormValidation doCheckUrl(@QueryParameter("name") final String nvalue, @QueryParameter("url") final String uvalue)
			 throws IOException, InterruptedException
		{
			if (nvalue.length() != 0 && uvalue.length() == 0)
				return FormValidation.error("Please enter SCM repository URL.");
			return FormValidation.ok();
		}

		public String getDisplayName() {
			return "Shared Workspace";
		}

		public ListBoxModel doFillNameItems() throws IOException, ServletException {
			ListBoxModel m = new ListBoxModel();

			m.add("NONE", "NONE");
			for (SharedWorkspace sw : getWorkspaces())
				m.add(sw.name + " (" + sw.url + ")", sw.name);
			return m;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			req.bindJSON(this, formData);
			save();
			return super.configure(req, formData);
		}
	}
}
