package ch.epfl.insynth.test.completion

import scala.tools.eclipse.testsetup.SDTTestUtils
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import scala.tools.nsc.interactive.Response
import scala.tools.eclipse.ScalaWordFinder
import scala.tools.nsc.util.SourceFile
import scala.tools.eclipse.ScalaPresentationCompiler
import org.eclipse.jface.text.contentassist.ICompletionProposal
import scala.tools.eclipse.testsetup.TestProjectSetup
import org.eclipse.jdt.core.search.{ SearchEngine, IJavaSearchConstants, IJavaSearchScope, SearchPattern, TypeNameRequestor }
import org.eclipse.jdt.core.IJavaElement
import scala.tools.nsc.util.OffsetPosition
import scala.tools.eclipse.completion.ScalaCompletions
import scala.tools.eclipse.completion.CompletionProposal
import ch.epfl.insynth.core.completion.InsynthCompletionProposalComputer
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.core.runtime.NullProgressMonitor
import ch.epfl.insynth.core.completion.InnerFinder
import scala.collection.JavaConversions
import scala.collection.JavaConverters
import scala.tools.eclipse.testsetup.TestProjectSetup
import java.{ util => ju, lang => jl }
import org.junit.Assert._
import org.junit.Test
import org.junit.Ignore
import org.junit.After
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.junit.runner.RunWith
import java.util.ArrayList
import org.junit.BeforeClass
import ch.epfl.insynth.core.Activator
import ch.epfl.insynth.core.preferences.InSynthConstants
import ch.epfl.insynth.statistics.format.Utility
import ch.epfl.insynth.statistics.format.XMLable
import ch.epfl.insynth.statistics.ReconstructorStatistics
import ch.epfl.insynth.statistics.InSynthStatistics
import java.io.File
import scala.collection.mutable.{ LinkedList => MutableList }
import org.junit.AfterClass

@RunWith(value = classOf[Parameterized])
class InSynthBenchmarkCompletionParametrizedTests(fileName: String, expectedSnippet: String,
    expectedPositionJavaAPI: (Int, Int), expectedPositionGeneralized: (Int, Int)) {
	
	import InSynthBenchmarkCompletionParametrizedTests.testProjectSetup._
	import InSynthBenchmarkCompletionParametrizedTests._
	import Utility._
	
	def innerTestFunction(path: String, index: Int) = {
	  val myPosition = if (index == 0) expectedPositionJavaAPI else expectedPositionGeneralized
    val oraclePos = List( (expectedSnippet, myPosition) )
    
    val exampleCompletions = List(CheckContainsAtPosition(oraclePos))
    
    for (i <- 1 to 5)
    	checkCompletions(path + fileName + ".scala")(exampleCompletions)
        
  	import InSynthStatistics._
  	import ReconstructorStatistics._
    	
  	assertEquals("lastEngineTime should contain 5 elements", 5, lastEngineTime.size)
  	assertEquals("reconstructionTime should contain 5 elements", 5, reconstructionTime.size)
  	
  	assertEquals(1, lastNumberOfDeclarations.distinct.size)
  	
  	tableDeclarations :+= lastNumberOfDeclarations.head
  	tableEngineTimes :+= lastEngineTime.sum.toFloat/lastEngineTime.size
  	tableFilenames :+= currentRun.fileName
  	tableReconstructionTime :+= reconstructionTime.sum.toFloat/reconstructionTime.size
    	
    val statsFileName = statsFileNames(index)
    appendToFile(
      statsFileName, "######\n"
    )
    appendToFile(
      statsFileName, currentRun.fileName
    )    
    appendToFile(
      statsFileName, InSynthStatistics.toString
    )
    appendToFile(
      statsFileName, ReconstructorStatistics.toString
    )    
    
  }

	@Ignore
  @Test
  // non generalized tests (individual import.clazz used)
  def testJavaAPI() {
    innerTestFunction("main/scala/javaapi/nongenerics/", 0)
  }
	
  @Test
  // generalized tests
  def testGeneralized() {
    innerTestFunction("main/scala/generalized/nongenerics/", 1)
  }
        
	@After
	def resetRunStatistics = resetRunStatisticsStatic 

}

object InSynthBenchmarkCompletionParametrizedTests {
	val testProjectSetup = new CompletionUtility(InSynthBenchmarkCompletionTests)
	
  val statsFileNames = List("insynth_statistics_javaapi.txt", "insynth_statistics_generalized.txt")
  val statsCSVFileNames = List("insynth_statistics_javaapi.txt", "insynth_statistics_generalized.txt")
  val csvFile = "data.csv"
  for (statsFileName <- List(csvFile) ++ statsFileNames ++ statsCSVFileNames) {
	  val file = new File(statsFileName)
	  file.delete
	  file.createNewFile    
  }
	
	// data for csv
	val firstRowString = "Filename, position, #declarations, Engine (avg), Reconstruction (avg)"
  var tableFilenames: MutableList[String] = MutableList.empty
  var tableDeclarations: MutableList[Int] = MutableList.empty
  var tableEngineTimes: MutableList[Float] = MutableList.empty
  var tableReconstructionTime: MutableList[Float] = MutableList.empty
  
  val generalizedPositions = List(
    0, 0, 0, 0, 0,  0, 0, 0, 3, 0, 1, 3, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 
    3, 1, 0, 0, 0, 0, 0, 0, 1, 0, 7, 8, 0, 0, 0, 0, 0, 5, 1, 0, 0, 0, 0, 0, 1
  )
	
  def resetRunStatisticsStatic = {
	  import ReconstructorStatistics._
	  import InSynthStatistics._
    
    resetLastRun
    resetStatistics
	}
  
  @BeforeClass
  def setup() {    
		// tests are made according to the clean code style
		Activator.getDefault.getPreferenceStore.
			setValue(InSynthConstants.CodeStyleParenthesesPropertyString, InSynthConstants.CodeStyleParenthesesClean)
			
		// run "warming-up" tests
		val fileName = "FileInputStreamStringname"
    testProjectSetup.checkCompletions("main/scala/generalized/nongenerics/" + fileName + ".scala")(Nil)
    testProjectSetup.checkCompletions("main/scala/javaapi/nongenerics/" + fileName + ".scala")(Nil)
    
    resetRunStatisticsStatic
  }
	
	@AfterClass
	def writeCSVTable = {
		import Utility._
	
	  appendToFile(csvFile, firstRowString)
		assertEquals(parameters.size, tableFilenames.size)
		assertEquals(parameters.size, tableDeclarations.size)
		assertEquals(parameters.size, tableEngineTimes.size)
		assertEquals(parameters.size, tableReconstructionTime.size)
		assertEquals(parameters.size, generalizedPositions.size)
		for( ((((fileName, numberDec), engine), reconstruction), position) <- tableFilenames zip tableDeclarations zip
	    tableEngineTimes zip tableReconstructionTime zip generalizedPositions) {		  
			appendToFile(csvFile, fileName.dropRight(6) + "," + (position + 1) + ", " + numberDec + ", " + engine + ", " + reconstruction)
		}
	}
  
	@Parameters
	def parameters: ju.Collection[Array[Object]] = {
	  val list = new ju.ArrayList[Array[Object]]
	  
	  case class GivesObject(int: Int) {
	  	def unary_! = (int, int) //int : jl.Integer
	  }
	  implicit def convertIntToGivesObject(int: Int) = GivesObject(int)
	  
	  list add Array( "FileInputStreamStringname" , "new FileInputStream(\"?\")", ! 0, ! 0 ) // 0
	  list add Array( "FileOutputStreamFilefile" , "new FileOutputStream(tempFile)", ! 0, ! 0 )
	  list add Array( "FileStringname" , "new File(\"?\")", ! 0, ! 0 )
	  list add Array( "FileWriterFilefile" , "new FileWriter(outputFile)", ! 0, ! 0 )
	  list add Array( "FileWriterLPT1" , "new FileWriter(\"?\")", ! 0, ! 0 )
	  list add Array( "GridBagConstraints" , "new GridBagConstraints", ! 0, ! 0 )
	  list add Array( "GroupLayoutContainerhost" , "new GroupLayout(panel)", ! 0, ! 0 )
	  list add Array( "ImageIconStringfilename" , "new ImageIcon(\"?\")", ! 0, ! 0 )
	  list add Array( "InputStreamReaderInputStreamin" , "new InputStreamReader(System in)", ! 0, ! 3 )
	  list add Array( "JButtonStringtext" , "new JButton(\"?\")", ! 0, ! 0 )
	  list add Array( "JCheckBoxStringtext" , "new JCheckBox(\"?\")", ! 1, ! 1 ) // 10
	  list add Array( "JFormattedTextFieldAbstractFormatterFactoryfactoryObjectcurrentValue" , "new JFormattedTextField(factory)", ! 3, ! 3 )
	  list add Array( "JFormattedTextFieldFormatterformatter" , "new MaskFormatter(\"?\")", ! 1, ! 1 )
	  list add Array( "JTableObjectnameObjectdata" , "new JTable(rows, columns)", ! 1, ! 1 )
	  list add Array( "JToggleButtonStringtext" , "new JFrame(\"?\")", ! 1, ! 1 )
	  list add Array( "JTree" , "new JTree", ! 0, ! 0 ) // 15
	  list add Array( "JWindow" , "new JWindow", ! 0, ! 0 )
	  list add Array( "ObjectInputStreamInputStreamin" , "new ObjectInputStream(fis)", ! 0, ! 0 )
	  list add Array( "ObjectOutputStreamOutputStreamout" , "new ObjectOutputStream(fos)", ! 0, ! 0 )
	  list add Array( "PipedReaderPipedWritersrc" , "new PipedReader(pw)", ! 1, ! 1 )
	  list add Array( "PipedWriter" , "new PipedWriter", ! 0, ! 0 ) // 20
	  list add Array( "Pointintxinty" , "new Point(0, 0)", ! 1, ! 1 )
	  list add Array( "PrintStreamOutputStreamout" , "new PrintStream(fout)", ! 0, ! 0 )
	  list add Array( "PrintWriterBufferedWriterbooleanautoFlush" , "new PrintWriter(bw, false)", ! 2, ! 3 )
	  list add Array( "SequenceInputStreamInputStreams1InputStreams2" , "new SequenceInputStream(f1, f2)", ! 2, ! 1 )
	  list add Array( "ServerSocketintport" , "new ServerSocket(port)", ! 0, ! 0 )
	  list add Array( "StreamTokenizerFileReaderfileReader" , "new StreamTokenizer(br)", ! 0, ! 0 )
	  list add Array( "StringReaderStrings" , "new StringReader(\"?\")", ! 0, ! 0 )
	  list add Array( "TimerintvalueActionListeneract" , "new Timer(0, actionListener)", ! 0, ! 0 )
	  list add Array( "TransferHandlerStringproperty" , "new TransferHandler(s)", ! 0, ! 0 )
	  list add Array( "URLStringspecthrowsMalformedURLException" , "new URL(\"?\") openConnection", ! 0, ! 0 ) // 30
	  
	  // missing 4 tests
	  list add Array( "FileReaderFilefile" , "new FileReader(inputFile)", ! 1, ! 1 )
	  list add Array( "GridBagLayout" , "new GridBagLayout", ! 0, ! 0 )
	  list add Array( "JViewport" , "new JViewport", !7, ! 7 )
	  list add Array( "LineNumberReaderReaderin" , "new LineNumberReader(new InputStreamReader(System in))", ! 0, ! 8 )
	  // tests from InSynthBenchmarkCompletionTests
	  list add Array( "AWTPermissionStringname" , "new AWTPermission(\"?\")", ! 0, ! 0 ) // 35
	  // cannot find, not even in 100 snippets
		//list add Array( "BoxLayoutContainertargetintaxis" , "new BoxLayout(container, BoxLayout.Y_AXIS)", ! 0, ! 0 )
		list add Array( "BufferedInputStreamFileInputStream" , "new BufferedInputStream(fis)", ! 0, ! 0 )
		list add Array( "BufferedOutputStream" , "new BufferedOutputStream(file)", ! 0, ! 0 )
		list add Array( "BufferedReaderFileReaderfileReader" , "new BufferedReader(fr)", ! 0, ! 0 )
		list add Array( "BufferedReaderInputStreamReader" , "new BufferedReader(isr)", ! 0, ! 0 ) // 40
		list add Array( "BufferedReaderReaderin" , "new BufferedReader(new InputStreamReader(url openStream))", ! 0, ! 5 )
		list add Array( "ByteArrayOutputStreamintsize" , "new ByteArrayOutputStream(0)", ! 1, ! 1 )
		list add Array( "DatagramSocket" , "new DatagramSocket", ! 0, ! 0 )
		list add Array( "DataInputStreamFileInputStreamfileInputStream" , "new DataInputStream(fis)", ! 0, ! 0 )
		list add Array( "DataOutputStreamFileOutputStreamfileOutputStream" , "new DataOutputStream(fos)", ! 0, ! 0 ) // 45
		list add Array( "DefaultBoundedRangeModel" , "new DefaultBoundedRangeModel", ! 0, ! 0 )
		list add Array( "DisplayModeintwidthintheightintbitDepthintrefreshRate" , "gs getDisplayMode", ! 0, ! 0 )
		list add Array( "FileInputStreamFileDescriptorfdObj" , "new FileInputStream(aFile)", ! 1, ! 1 )
	  
	  list
	}

}