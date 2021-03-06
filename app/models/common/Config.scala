package models.common

import java.io.File

import scala.io.Source

object Config {

  type TopEntryTy = (Long, String)
  type A2DocTy = (String, Float)

  val homeDir = System.getProperty("user.home")
  val tempDir = System.getProperty("java.io.tmpdir")

  // NOTE: please change this value to the absolute path for "public/resources" of the project
  // and put dblp.xml file inside the directory.
  val rootDir = homeDir + File.separator + "Dropbox/PHDCourses/IR/assignment"
  val VALIDATION = "http://xml.org/sax/features/validation"
  val indexRoot = tempDir + File.separator + "index"
  val splitString = "; "
  val longSWFName = rootDir + File.separator + "en.txt"
  val xmlFile = rootDir + File.separator + "dblp.xml"
  //    val xmlFile = rootDir + File.separator + "sample.xml"

  val ignoredTerms = {
    val ignored = Source.fromFile(longSWFName).getLines().toSet
    require(ignored.nonEmpty)
    ignored
  }


  val I_PAPER_ID = "paperId"
  val I_TITLE = "title"
  val I_KIND = "kind"
  val I_AUTHORS = "authors"
  val I_VENUE = "venue"
  val I_PUB_YEAR = "pubYear"
  val I_ALL = "ALL"

  val allFields = List(I_PAPER_ID, I_TITLE, I_KIND, I_AUTHORS, I_VENUE, I_PUB_YEAR)
  val DBLPNOTE = "dblpnote"

  val DEFAULT_CONJ = "NIL"

  val TNG_ITERATIONS = 5

  //    interface Configurable
  val defaultTopN = 10
  val defaultStep = 5

  val defaultNGramSizes = Array(2, 3, 4)

  val a1Range = (2000, 2016)
  val a2Range = (1936, 2016)

}
