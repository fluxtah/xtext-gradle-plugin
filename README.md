xtext-gradle-plugin
===================

[![Build Status](https://travis-ci.org/xtext/xtext-gradle-plugin.svg?branch=master)](https://travis-ci.org/xtext/xtext-gradle-plugin)

A plugin for using Xtext languages from a Gradle build

Features
--------

- Use Xtext based code generators in your Gradle build
- automatically integrates with the Java plugin
- all your languages are built in one go, so they can have cross-references
- configures the Eclipse plugin to generate .settings files for each language

Usage
-----

Get the latest version of the plugin from the [Gradle Plugin Portal](http://plugins.gradle.org/plugin/org.xtext.xtext)

Add your languages to the xtextTooling configuration

```groovy
dependencies {
  xtextTooling 'org.example:org.example.hero.core:3.3.3'
  xtextTooling 'org.example:org.example.villain.core:6.6.6'
}
```

Add code that your models compile against to the xtext configuration. If you use the Java plugin, this configuration will automatically contain everything from compile and testCompile. So in many cases this can be omitted.

```groovy
dependencies {
  xtext 'com.google.guava:guava:15.0'
}
```

Configure your languages

```groovy
xtext {
  version = '2.7.2' // the current default, can be omitted
  encoding = 'UTF-8' //the default, can be omitted
  
  /* Java sourceDirs are added automatically,
   * so you only need this element if you have
   * additional source directories to add
   */
  sources {
    srcDir 'src/main/heroes'
    srcDir 'src/main/villains'
  }
  
  languages{
    heroes {
      setup = 'org.example.hero.core.HeroStandaloneSetup'
      consumesJava = true
      outputs {
        DEFAULT_OUTPUT.dir = 'build/heroes'
        SIDEKICKS {
          dir = 'build/sidekicks'
          //automatically adds the above folder as a Java source folder for the main sourceSet
          producesJavaFor sourceSets.main
        }
      }
    }
    
    villains {
      setup = 'org.example.villain.core.VillainStandaloneSetup'
      //equivalent to DEFAULT_OUTPUT.dir
      output.dir = 'build/villains'
    }
  }
}
```
