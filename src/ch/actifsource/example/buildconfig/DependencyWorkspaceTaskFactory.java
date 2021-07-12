package ch.actifsource.example.buildconfig;

import javax.annotation.CheckForNull;
import ch.actifsource.core.INode;
import ch.actifsource.core.dependency.IDependency;
import ch.actifsource.core.job.Select;
import ch.actifsource.example.buildconfig.specific.SpecificPackage;
import ch.actifsource.generator.AbstractBuildTaskSingleThread;
import ch.actifsource.generator.GenerationException;
import ch.actifsource.generator.IBuildTask;
import ch.actifsource.generator.console.IGeneratorConsole;
import ch.actifsource.generator.target.ISingleThreadBuildTargetInfo;
import ch.actifsource.generator.workspacetask.IWorkspaceTaskFactory;
import ch.actifsource.util.ICancelStatus;

public class DependencyWorkspaceTaskFactory implements IWorkspaceTaskFactory {
  
  @Override
  public IBuildTask create(INode buildTask, ICancelStatus status) throws GenerationException {
    
    return new AbstractBuildTaskSingleThread(buildTask, status) {
      
      @Override
      @CheckForNull
      protected IDependency internalGenerate(ISingleThreadBuildTargetInfo buildTargetInfo) throws GenerationException {
        IGeneratorConsole console = buildTargetInfo.getBuildContext().console();
        
        console.warning().print("Read name form instanceA and add to dependency.\n");
        Select.simpleName(buildTargetInfo.getInstanceReadJobExecutor(), SpecificPackage.InstanceA);
          
        // Return not null enable dependency tracking. Returns null disable dependency tracking.
        return IDependency.DUMMY;
      }
      
    };
  }
  
}
