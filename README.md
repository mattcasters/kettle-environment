# kettle-environment

This is a set of Kettle plugins and tools to help manage runtime and lifecycle environments.

# Build

Run ```mvn clean install```  or simply download a release from the github project.

# Install

Copy file ```target/kettle-environment-<version>.jar``` to a new folder ```<PDI>/plugins/kettle-plugins/```
Or simply unzip a release in the ```<PDI>/plugins``` folder.

# Configure

The environments are stored in folder your ```$HOME/.kettle/``` in a separate metastore under ```environment/metastore```.

You can point this folder somewhere else with the environment variable ```ENVIRONMENT_METASTORE_FOLDER```

# Spoon

When you start Spoon you'll now see a dialog with a list of environments.
You can use the **Add Default** button to add the default environment.  

**IMPORTANT**: make sure to configure this Default environment to match your needs.

Once you created and selected an environment you'll see it's name in the upper right corner of your transformations and jobs.

# Maitre

The ```Maitre.bat``` or ```maitre.sh``` scripts from the [kettle-needful-things](https://github.com/mattcasters/kettle-needful-things) project allows you to specify in which environment you want to execute your transformation or job.

# Configuration System Configuration

The only option available right now is the option to disable to whole Kettle environment system.

# Environment options

* **Name** : A unique name identifying your environment
* **Description** : A description
* **Company** : Free to use metadata
* **Department** : Free to use metadata
* **Project** : Free to use metadata
* **Version** : Free to use metadata
* **Environment base folder** : Manages environment variable ```ENVIRONMENT_HOME``` and specified the base of your folder structures.
* **Kettle home folder** : Manages environment variable ```KETTLE_HOME``` which defines the location of kettle.properties and a bunch more.
* **MetaStore base folder** : Manages environment variable ```PENTAHO_METASTORE_FOLDER``` which tells the system where to store metastore elements.
* **SpoonGit** : Not used yet, for future integration with pdi-git-plugin
* **Unit tests base path** : Manages environment variable ```UNIT_TESTS_BASE_PATH```. This allows your unit tests to reference a relative path.
* **DataSets CSV Folder** : Manages environment variable ```DATASETS_BASE_PATH```. This allows your CSV data sets (for unit testing) to be stored in a certain folder.
* **Execute executions in environment home** : When enabled every job or transformation needs to be located in ```${ENVIRONMENT_HOME}``` or a subdirectory.  You can use this to avoid accidental execution of a transformation or job from another environment.
* **System variables to set** : You can specify a list of variables and values to set whenever the environment is used.  You can (but don't need to) specify a description for each.

The default options metastore, unit tests and data sets are such that all data and metadata is stored under the environment home folder.
This way you can always check in your whole environment and project into version control.

# Feedback

Whenever you have suggestions or encounter bugs, please create an issue in [this project](https://github.com/mattcasters/kettle-environment)





 