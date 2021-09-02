package io.jenkins.plugins.rancher;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

public class RancherActionBuilder extends Builder implements SimpleBuildStep {

    private String endpoint = "";
    private String apiToken = "";
    private String clusterId = "";
    private String namespaceId = "";
    private String action = "";
    private String serviceNames = "";

    @DataBoundConstructor
    public RancherActionBuilder(String endpoint, String apiToken, String clusterId, String namespaceId,
            String action, String serviceNames) {
        this.endpoint = endpoint;
        this.apiToken = apiToken;
        this.clusterId = clusterId;
        this.namespaceId = namespaceId;
        this.action = action;
        this.serviceNames = serviceNames;
    }

    public String getEndpoint() {
        return endpoint;
    }

    @DataBoundSetter
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiToken() {
        return apiToken;
    }

    @DataBoundSetter
    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getClusterId() {
        return clusterId;
    }

    @DataBoundSetter
    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    @DataBoundSetter
    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getAction() {
        return action;
    }

    @DataBoundSetter
    public void setAction(String action) {
        this.action = action;
    }

    public String getServiceNames() {
        return serviceNames;
    }

    @DataBoundSetter
    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("=================================RancherAction==================================");
        String[] serviceNameArr = serviceNames.split(",");
        String baseUrl = endpoint +
                "/project" +
                "/" + clusterId + ":" + namespaceId +
                "/workloads" + "/";
        listener.getLogger().println("BaseUrl: " + baseUrl);
        listener.getLogger().println("Begin request rancher server...");
        for (String serviceName : serviceNameArr) {
            String url = baseUrl + serviceName + "?action=" + action;
            listener.getLogger().print(" - " + serviceName + " -> " + action + " ... ");
            HttpResponse httpResponse = HttpUtil.doPost(url, null, apiToken);
            if (httpResponse.getCode() == 200) {
                listener.getLogger().println("[OK]");
            } else {
                listener.getLogger().println("[Failed] " + httpResponse.toString());
            }
        }
        listener.getLogger().println("=================================RancherAction==================================");
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckEndpoint(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Endpoint must not be null.");
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckApiToken(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("ApiToken must not be null.");
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckClusterId(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("ClusterId must not be null.");
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckNamespaceId(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("NamespaceId must not be null.");
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckAction(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Action must not be null.");
            }
            return FormValidation.ok();
        }
        public FormValidation doCheckServiceNames(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("ServiceNames must not be null.");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "RancherAction";
        }

    }

}
