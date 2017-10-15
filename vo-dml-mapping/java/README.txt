The java classes in the <vo-dml>/java/gen/src/ folder have been generated from VO-DML/XML files in the <vo-dml>/models/ subfolders
using the vo-dml2pojo.xsl script in <vo-dml>/xslt/. That translation is guided by the file <vo-dml>/models/mapping_file.xml . 
It governs which models are to be translated, what the target package is and when instead of generating a class, a particular 
predefined class or type should be used to represent the defined type.

The ant task "run_vo-dml2pojo" in <vo-dml>/build.xml executes the script.