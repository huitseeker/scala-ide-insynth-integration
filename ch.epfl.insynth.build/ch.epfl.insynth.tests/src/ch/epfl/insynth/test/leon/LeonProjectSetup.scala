package ch.epfl.insynth.test.leon

import scala.tools.eclipse.testsetup.TestProjectSetup
import ch.epfl.insynth.test.completion.CompletionUtility
import org.junit.Assert._
import org.junit.Test
import org.junit.BeforeClass
import org.junit.Ignore
import leon.{ Main => LeonMain }
import scala.tools.nsc.MainGenericRunner
import leon.DefaultReporter
import scala.tools.nsc.{Global,Settings=>NSCSettings,SubComponent,CompilerCommand}
import leon.Globals

object LeonProjectSetup extends TestProjectSetup("leon", bundleName = "ch.epfl.insynth.tests")

class LeonProjectSetup {
	val testProjectSetup = new CompletionUtility(LeonProjectSetup)
	
	import testProjectSetup._
	  
	  val classpathArray = Array(
      "/home/ivcha/git/leon-2.0/target/scala-2.9.1-1/classes",
      "/home/ivcha/git/leon-2.0/library/target/scala-2.9.1-1/classes",
      "/home/ivcha/git/leon-2.0/unmanaged/z3-64.jar",
      "/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-library.jar",
      "/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-compiler.jar"
		)
		
	val SCALACLASSPATH = classpathArray mkString ":" 
	  //"/home/ivcha/git/leon-2.0/unmanaged/z3-64.jar:/home/ivcha/git/leon-2.0/target/scala-2.9.1-1/classes:/home/ivcha/git/leon-2.0/library/target/scala-2.9.1-1/classes:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-library.jar:/home/ivcha/.sbt/boot/scala-2.9.1-1/lib/scala-compiler.jar"
	
	@Test
	def run() {
	  val validCompletions = List("sizeTail(tail, 1)", "sizeTail(tail, acc+1)", "0")
	  
	  val numberOfFiles = withCompletions("list/List.scala")(validCompletions, 0)("ListGenerated_%d.scala")
	  
	  assertTrue(numberOfFiles > 0)
	  
//	   val settings = new NSCSettings
//    settings.classpath.tryToSet(List(SCALACLASSPATH))    
//    println(settings.classpath.value)
//    
//    assertTrue(false)
	  
	  var results = scala.collection.mutable.Map[Boolean, Int]( (true -> 0), (false -> 0) )
	  
	  println("Calling leon with classpath argument: " + SCALACLASSPATH)
	  
	  for (fileIndex <- 0 until numberOfFiles) {
	  	println("Calling leon on file: " + ("ListGenerated_%d.scala" format fileIndex))
	  	
	  	LeonMain.run(Array("ListGenerated_%d.scala" format fileIndex, "--timeout=3", "--noLuckyTests"), new DefaultReporter, Some(List(SCALACLASSPATH)))
	  	
	  	Globals.allSolved match {
	  	  case Some(res) => results(res) += 1
	  	  case None => fail("Globals.allSolved is None")
	  	}	
	  }
	  
	  println("Results: solved(" + results(true) + "), not solved(" +  results(false) + ")")
	  
	  //withCompletions("RedBlackTree.scala")(List("Node(Red(),Node(Black(),a,xV,b),yV,Node(Black(),c,zV,d))"), 0)("RedBlackTreeGenerated_%d.scala")
	}

}