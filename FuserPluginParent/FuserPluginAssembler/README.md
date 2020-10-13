# Overview
---

The FuserAssembler constructs a zip artifact comprised of the Fuser and one or 
more transform plugins.  The set of included transform plugins is determined 
by the set of maven profiles activated during assembly.

The maven assembly process is not bound to the install phase.  To produce a new 
artifact the maven assembly phase must be explicity activated by running:

>mvn assembly:single

## Profiles
---

Profiles can be activated using the maven -P flag.

>mvn assembly:single -P asdex-transform-plugin,sfdps-transform-plugin

The set of available profiles are:

 - asdex-transform-plugin
 - sfdps-transform-plugin
 - smes-transform-plugin
 - surveillance-transform-plugin
 - tfm-tfdm-transform-plugin
 - tfm-transform-plugin
 - tma-transform-plugin
 - ttp-transform-plugin
   
## Activate Flags
---
 
To facilitate generation of common environments, such as producing an artifact 
to support all SWIM data sources, an activate flag was added which when present 
will automatically trigger a set of profiles.

The following command will activate all SWIM profiles:

> mvn assembly:single -Dprofile.swim
 