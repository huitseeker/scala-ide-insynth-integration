InSynth plugin in Scala IDE
==============================

This project contains plugins for building **InSynth** plugin in `Scala IDE`_.

*This is a work in progress.* Please submit `tickets`_ if you encounter problems.

.. _Scala IDE: http://scala-ide.org
.. _tickets: https://github.com/kaptoxic/scala-ide-insynth-integration/issues?state=open

Building
--------

Maven is used to manage the build process.  The project can be built for Scala IDE 2.0.2 (stable) and master (nightly/2.1.0).

*To build for Scala IDE with Scala 2.9 (nightly/2.9), use

  $ mvn clean install -P scala-ide-master-scala-2.9

*To build for Scala IDE with Scala 2.10 (nightly/2.1.0), use

  $ mvn clean install -P scala-ide-master-scala-trunk

InSynth user documentation
==========================

A short user documentation can be found at the project's `Wiki`_ pages.

.. _Wiki: https://github.com/kaptoxic/scala-ide-insynth-integration/wiki
