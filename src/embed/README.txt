
 - EMBEDDED CODE -

This directory contains source code embedded/forked from other projects to preclude the
need for separate bundling; prefixed class packages prevent naming conflicts with other
instances of the same source code, in case the original library is installed.

Code contained herein should be licensed such that it allows bundling/embedding without
violating the copyright of the original source code's author(s).

The source code for the original JAR should be added as a GIT sub-module under the REPO
directory.  After checking out the source code, you can use:
    ./embed-tool.jar MODULE_NAME SOURCE_PACKAGE DESTINATION_PACKAGE

This will copy the prefixed classes, resources, and txt-notices to the JAVA directory.

=========================================================================================
    LIBRARY                         AUTHORS
        VERSION                     PACKAGE
        LICENSE                     WEBSITE
=========================================================================================

    solid-tx                        BinaryBabel OSS
        0.0.4                       org.binbab.solidtx
        MIT                         https://github.com/org-binbab/solid-tx

    json-simple                     Yidong Fang
        1.1.1                       org.json.simple
        Apache 2.0                  https://code.google.com/p/json-simple/
