package org.kettle.env.xp;

import org.kettle.env.util.EnvironmentUtil;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.job.Job;

@ExtensionPoint(
  id = "JobStartCheckEnvironmentExtensionPoint",
  description = "At the start of a job, verify it lives in the active environment",
  extensionPointId = "JobStart"
)
/**
 * set the debug level right before the step starts to run
 */
public class JobStartCheckEnvironmentExtensionPoint implements ExtensionPointInterface {

  @Override public void callExtensionPoint( LogChannelInterface log, Object object ) throws KettleException {

    if ( !( object instanceof Job ) ) {
      return;
    }

    Job job = (Job) object;

    String jobFilename = job.getFilename();

    try {
      EnvironmentUtil.validateFileInEnvironment( log, jobFilename, (VariableSpace) job );
    } catch ( Exception e ) {
      throw new KettleException( "Validation error against job '" + jobFilename+ "' in active environment", e );
    }
  }
}
