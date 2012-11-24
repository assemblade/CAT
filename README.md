CAT - Configuration Access Tool
===

CAT is a server application that allows secure storage and access to application configuration data in a hierarchical structure.

Sounds like LDAP, you say. Well, it is! Except we are attempting to make LDAP a lot more accessible to modern application developers using HTTP REST API to securely access the data.

The data is stored in directories, just like a filesystem and granular security control can be applied at every level.

There is user and group management, and token/HMAC signature based security used for resource access security. It also allows authentication pass-through to an external LDAP/AD server.

The configurations are essentially key/value pairs and any changes are recorded in the change history so we aim to support your applications being compliant with PCI etc.

Of course, all access to the data is logged as it is HTTP-based.

As this technology is based on proven OpenDJ LDAP searches are quick.


Getting Started
===============

```bash
git clone https://github.com/assemblade/CAT.git
```

Download gradle from http://www.gradle.org/downloads and install.

export GRADLE_HOME environment variable to the gradle installation directory.

```bash
export PATH=$GRADLE_HOME:$PATH
```

cd to cloned CAT directory.

```bash
gradle build
```


Use Cases
=========

We believe deploying production applications with raw properties file that contains sensitive data is dangerous! This tool allow externalisation of application config data, that can be securely managed by authorised individuals.

Build tools including Maven / ANT / Gradle / Rake / NANT etc could easily integrate with CAT to allow individual developer custom configurations.

We will be filling this section as we find more use cases for this product.


TODO
====

We will be working on Perl / Ruby / Python / PHP / .Net clients soon.

